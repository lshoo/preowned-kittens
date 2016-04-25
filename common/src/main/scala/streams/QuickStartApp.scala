package streams

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import java.io.File

object QuickStartApp {
  val source: Source[Int, NotUsed] = Source(1 to 100)


  def main(args: Array[String]) {
    implicit val system = ActorSystem("Quick-Start")
    implicit val marterializer = ActorMaterializer()
    import system.dispatcher

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)
    val result: Future[IOResult] =
      factorials.map(num => ByteString(s"$num\n" ))
        .runWith(FileIO.toFile(new File("factorials.txt")))

    def lineSink(filename: String): Sink[String, Future[IOResult]] =
      Flow[String]
          .map(s => ByteString(s + "\n"))
          .toMat(FileIO.toFile(new File(filename)))(Keep.right)

    def done: Future[Done] =
      factorials
        .zipWith(Source(0 to 1000))((num, idx) => s"$idx! = $num")
        .throttle(1, 1.second, 1, ThrottleMode.shaping)
        .runForeach(println)
//    Await.result(result, Duration.Inf)
    /*result.andThen {
      case _ =>
        Await.result(system.terminate(), Duration.Inf)
    }*/
    /*factorials.map(_.toString()).runWith(lineSink("factorials2.text")) */
    done
      .andThen  {
      case _ =>
        Await.result(system.terminate(), Duration.Inf)
    }


  }
}
