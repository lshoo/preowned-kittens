package streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object ConstructingGraphApp {

  val pairs = Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    // prepare graph elements
    val zip = b.add(Zip[Int, Int]())
    val ints = Source(1 to 20)

    // connect the graph
    ints.filter(_ % 2 != 0) ~> zip.in0
    ints.filter(_ % 2 == 0) ~> zip.in1

    // expose port
    SourceShape(zip.out)
  })



  val pairUpWithToString = Flow.fromGraph(GraphDSL.create() {implicit b =>
    import GraphDSL.Implicits._

    val broadcast = b.add(Broadcast[Int](2))
    val zip = b.add(Zip[Int, String]())

    broadcast.out(0).map(identity) ~> zip.in0
    broadcast.out(1).map(_ + "!") ~> zip.in1

    FlowShape(broadcast.in, zip.out)
  })

  def main(args: Array[String]) {
    implicit val system = ActorSystem("Constructing-Graph")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    //val firstPair = pairs runWith Sink.foreach(println)
    /*firstPair
      .andThen {
      case a =>
        println(a)
        Await.result(system.terminate(), Duration.Inf)
    }*/

    pairUpWithToString.runWith(Source(List(1, 20, 30, 50)), Sink.foreach(println))
    Thread.sleep(1000)
    Await.result(system.terminate(), Duration.Inf)
  }
}
