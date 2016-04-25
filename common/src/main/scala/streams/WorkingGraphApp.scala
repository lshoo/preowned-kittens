package streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

/**
  * Please doc ...
  */
object WorkingGraphApp {

  val in = Source(1 to 10)
  val out = Sink.foreach(println)

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val bcast = b.add(Broadcast[Int](2))
    val merge = b.add(Merge[Int](2))

    val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
    bcast ~> f4 ~> merge

    ClosedShape
  })

  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]

  val sharedDouble = Flow[Int].map(_ * 2)

  val head = RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) { implicit b =>
    (topHS, bottomHS) =>
      import GraphDSL.Implicits._
      val broadcast = b.add(Broadcast[Int](2))
      Source.single(1) ~> broadcast.in

      broadcast.out(0) ~> sharedDouble ~> topHS.in
      broadcast.out(1) ~> sharedDouble ~> bottomHS.in

      ClosedShape
  })

  val pickMaxOfThree = GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val zip1 = b.add(ZipWith[Int, Int, Int](math.max _))
    val zip2 = b.add(ZipWith[Int, Int, Int](math.max _))
    zip1.out ~> zip2.in0

    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)

  }

  val resultSink = Sink.head[Int]

  val g2 = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b =>
    sink =>
      import GraphDSL.Implicits._

      // importing the partial graph will return its shape (inlets & outlets)
      val pm3 = b.add(pickMaxOfThree)

      Source.single(1) ~> pm3.in(0)
      Source.single(2) ~> pm3.in(1)
      Source.single(3) ~> pm3.in(2)

      pm3.out ~> sink.in

      ClosedShape
  })

  def main(args: Array[String]) {
    implicit val system = ActorSystem("Working-Graph")
    implicit val materializer = ActorMaterializer()

    import system.dispatcher
    import GraphDSL.Implicits._
    /*g.run

    Thread.sleep(1000)
    Await.result(system.terminate(), Duration.Inf)*/

    /*val (ths, bhs) = head.run()

    for {
      t <- ths
      b <- bhs
    } yield s"Top: $t, Bottom: $b"
      .andThen {
        case _ =>
          Await.result(system.terminate(), 3.seconds)
      }*/

    val max: Future[Int] = g2.run()
    max onComplete {
      m =>
        println(m)
        Await.result(system.terminate(), 3.seconds)
    }
  }
}
