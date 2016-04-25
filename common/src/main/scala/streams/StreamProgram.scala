package streams

import akka.stream.scaladsl._
import akka.stream._
import akka.actor._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * http://eng.localytics.com/akka-streams-akka-without-the-actors/
  */
object StreamProgram {

  val sayFlow = Flow[String].map{ s => s + "." }

  val shoutFlow = Flow[String].map { s => s + "!!!" }

  val sayAndShoutFlow = Flow.fromGraph (GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val broadcast = b.add(Broadcast[String](2))
    val merge = b.add(Merge[String](2))

    broadcast ~> sayFlow ~> merge
    broadcast ~>shoutFlow ~> merge

    FlowShape(broadcast.in, merge.out)
  } )

  def run() = {
    implicit val system = ActorSystem("example")
    implicit val materializer = ActorMaterializer()

    Source(List("Hello World"))
      .via(sayAndShoutFlow)
      .runWith(Sink.foreach(println))
      .onComplete {
        case _ =>
          Await.result(system.terminate(), Duration.Inf)
      }
  }

  def main(args: Array[String]) {
    run()
  }
}
