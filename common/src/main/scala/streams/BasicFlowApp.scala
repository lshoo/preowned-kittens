package streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

object BasicFlowApp {

  val flow = Flow[Int].map(_ * 2).filter(_ > 500)
  val fuse = Fusing.aggressive(flow)

  def main(args: Array[String]) {

    implicit val system = ActorSystem("basic-flow")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    Source.fromIterator { () => Iterator from 0 }
      .via(fuse)
      .take(1000)
  }

}
