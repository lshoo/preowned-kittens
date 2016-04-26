package streams

import akka.NotUsed
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent._
import scala.concurrent.duration._

import scala.collection._

object ReusableGraphApp {

  case class PriorityWorkerPoolShape[In, Out] (
                                              jobsIn: Inlet[In],
                                              priorityJobsIn: Inlet[In],
                                              resultsOut: Outlet[Out]
                                              ) extends Shape {
    // It is important to provide the list of all input and output
    // ports with a stable order. Duplicates are not allowed.
    override def inlets: immutable.Seq[Inlet[_]] =
      jobsIn :: priorityJobsIn :: Nil
    override def outlets: immutable.Seq[Outlet[_]] =
      resultsOut :: Nil

    // A Shape must be able to create a copy of itself.
    // Basically it means a new instance with copies of ports
    override def deepCopy() = PriorityWorkerPoolShape(
      jobsIn.carbonCopy(),
      priorityJobsIn.carbonCopy(),
      resultsOut.carbonCopy()
    )

    // A Shape must also be able to create itself from existing ports
    override def copyFromPorts(
                              inlets: immutable.Seq[Inlet[_]],
                              outlets: immutable.Seq[Outlet[_]]
                              ) = {
      assert(inlets.size == this.inlets.size)
      assert(outlets.size == this.outlets.size)
      // This is why order matters when overriding inlets and outlets
      PriorityWorkerPoolShape[In, Out](inlets(0).as[In], inlets(1).as[In], outlets(0).as[Out])
    }
  }

  import FanInShape.{ Name, Init }
  class PriorityWorkerPoolShape2[In, Out](_init: Init[Out] = Name("PriorityWorkerPool"))
  extends FanInShape[Out](_init) {
    protected override def construct(i: Init[Out]) = new PriorityWorkerPoolShape2(i)

    val jobsIn = newInlet("jobsIn")
    val priorityJobsIn = newInlet("priorityJobsIn")
    // Outlet[Out] with name out is automatically created
  }

  object PriorityWorkerPool {
    def apply[In, Out](
                      worker: Flow[In, Out, Any],
                      workerCount: Int): Graph[PriorityWorkerPoolShape[In, Out], NotUsed] = {
      GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        val priorityMerge = b.add(MergePreferred[In](1))
        val balance = b.add(Balance[In](workerCount))
        val resultsMessage = b.add(Merge[Out](workerCount))

        // After merging priority and ordinary jobs, we feed the balancer
        priorityMerge ~> balance

        // Wire up each of the outputs of the balancer to a worker flow then merge the back
        for (i <- 0 until workerCount)
          balance.out(i) ~> worker ~> resultsMessage.in(i)

        // We now expose the input ports of the priorityMerge and the output
        // of the resultsMerge as our PriorityWorkerPool ports
        // -- all neatly wrapped in our domain specific Shape
        PriorityWorkerPoolShape(
          jobsIn = priorityMerge.in(0),
          priorityJobsIn = priorityMerge.preferred,
          resultsOut = resultsMessage.out
        )
      }
    }

  }

  val worker1 = Flow[String].map("step 1 " + _)
  val worker2 = Flow[String].map("step 2 " + _)

  def main(args: Array[String]) {
    implicit val system = ActorSystem("Reusable-Graph")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val priorityPool1 = b.add(PriorityWorkerPool(worker1, 4))
      val priorityPool2 = b.add(PriorityWorkerPool(worker2, 2))

      Source(1 to 10).map("job: " + _) ~> priorityPool1.jobsIn
      Source(1 to 10).map("priority job: " + _) ~> priorityPool1.priorityJobsIn

      priorityPool1.resultsOut ~> priorityPool2.jobsIn
      Source(1 to 10).map("one-step, priority job: " + _) ~> priorityPool2.priorityJobsIn

      priorityPool2.resultsOut ~> Sink.foreach(println)
      ClosedShape
    }).run()

    Thread.sleep(1000)
    Await.result(system.terminate(), Duration.Inf)
  }
}
