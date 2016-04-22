package streams

/**
  * https://stackoverflow.com/questions/35120082/how-to-get-started-with-akka-streams
  */
object GetStartApp {

  import scala.concurrent._
  import akka._
  import akka.actor._
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.util._

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val source = Source.empty

}
