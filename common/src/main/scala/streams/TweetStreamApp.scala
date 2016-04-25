package streams

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl._

import akka.actor._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

object TweetStreamApp {
  final case class Author(handle: String)
  final case class Hashtag(name: String)
  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect {
        case t if t.startsWith("#") =>
          Hashtag(t)
      }.toSet
  }

  val akka = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = ???
  val authors: Source[Author, NotUsed] =
    tweets.filter(_.hashtags.contains(akka)).map(_.author)

  val hashtags: Source[Hashtag, NotUsed] = tweets.mapConcat(_.hashtags)

  val writeAuthors: Sink[Author, Unit] = ???
  val writeHashtag: Sink[Hashtag, Unit] = ???

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val bcast = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags) ~> writeHashtag
    ClosedShape
  })

  val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)
  val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
  val counterGraph: RunnableGraph[Future[Int]] =
    tweets.via(count).toMat(sumSink)(Keep.right)


  def main(args: Array[String]) {
    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    authors.runWith(Sink.foreach(println)) andThen {
      case _ =>
        Await.result(system.terminate(), Duration.Inf)
    }

    val sum = counterGraph.run()
    sum.andThen {
      case _ =>
        Await.result(system.terminate(), Duration.Inf)
    }
  }
}
