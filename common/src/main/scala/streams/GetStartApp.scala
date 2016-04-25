package streams

import scala.concurrent._
import akka._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util._

import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.util.Random

/**
  * https://stackoverflow.com/questions/35120082/how-to-get-started-with-akka-streams
  */

object InputCustomer {
  def random(): InputCustomer = {
    InputCustomer(s"FirstName${Random.nextInt(1000)} LastName${Random.nextInt(1000)}")
  }
}

case class InputCustomer(name: String)
case class OutputCustomer(firstName: String, lastName: String)

object GetStartApp extends  App {

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val inputSources = Source((1 to 100)).map(_ => InputCustomer.random())

  val normalize = Flow[InputCustomer].map(c => c.name.split(" ").toList).collect {
    case firstName :: lastName :: Nil =>
      OutputCustomer(firstName, lastName)
  }

  val writeCustomer = Sink.foreach[OutputCustomer] { customer =>
    println(customer)
  }

  inputSources.via(normalize).runWith(writeCustomer).andThen {
    case _ =>
      system.shutdown()
      system.awaitTermination()
  }
  /*val out = inputSources via normalize to writeCustomer
  out.run()

  val terminated = system.terminate()
  Await.result(terminated, Duration.Inf)*/

}
