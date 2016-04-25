package streams

import akka.actor.{Props, ActorSystem}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnNext}
import akka.stream.actor.{OneByOneRequestStrategy, RequestStrategy, ActorSubscriber}
import akka.stream.{ClosedShape, ActorMaterializer, SourceShape}
import akka.stream.scaladsl._
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * https://neildunlop.wordpress.com/2016/01/03/getting-started-with-reactive-streams-part-2/
  * An Akka Streams Source helper that produces message at a defined rate.
  */
object ThrottledProducer {

  case object Tick

  def produceThrottled(initialDelay: FiniteDuration, interval: FiniteDuration, numberOfMessage: Int, name: String) = {
    val ticker = Source.tick(initialDelay, interval, Tick)
    val numbers = 1 to numberOfMessage

    val rangeMessageSource = Source(numbers.map(message => s"Message $message"))

    // define a stream to bring it all together
    val throttledStream = Source.fromGraph(GraphDSL.create() { implicit builder =>

      // 1. create a Kamon counter so we can track number of message produced
      val createCounter = Kamon.metrics.counter("throttledProducer-create-counter")

      // define a zip operation that expects a tuple with a Tick and a Message in it
      // (note that the operations must be added to the builder before they can be used)
      val zip = builder.add(Zip[Tick.type , String])

      // create a flow to extract the second element in the tuple (our message - we don't need the tick part of after this stage)
      val messageExtractorFlow = builder.add(Flow[(Tick.type , String)].map(_._2))

      // 2. create a flow to log performance information to Kamon and pass on the message object unmolested
      val statsDExporterFlow = builder.add(Flow[(String)].map { message => createCounter.increment(1); message })
      // import this so we can use the ~> syntax
      import GraphDSL.Implicits._

      // define the inputs for the zip function - it wont fire until something arrives at both inputs, so we are essentially
      // throttling the out of this stream
      ticker ~> zip.in0
      rangeMessageSource ~> zip.in1

      // send the output of our zip operation to a processing messageExtractorFlow that just allows us to take the second
      // element of each Tuple, in our case this is the string message, we don't care about the Tick, it was just for
      // timing and we can throw it away.
      // 3. Then we route the output of the extractor to a flow that exports data to StatsD
      // then route that to the 'out' Sink.
      zip.out ~> messageExtractorFlow ~> statsDExporterFlow

      // SourceShape(messageExtractorFlow.out)
      SourceShape(statsDExporterFlow.out)
    })

    throttledStream

  }
}

object SimpleStream {

  def printSimpleMessagesToConsole(implicit materializer: ActorMaterializer) = {
    val simpleMessage = "Message 1" :: "Message 2" :: "Message 3" :: "Message 4" :: "Message 5" :: Nil

    Source(simpleMessage)
      .map(println(_))
      .to(Sink.ignore)
      .run()
  }

  def throttledProducerToConsole() = {
    val theGraph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
      val source = builder.add(ThrottledProducer.produceThrottled(
      1 second, 250 milliseconds, 20000, "fastProducer"
    ))
      val printFlow = builder.add(Flow[String].map(println(_)))
      val sink = builder.add(Sink.ignore)

      import GraphDSL.Implicits._

      source ~> printFlow ~> sink

      ClosedShape
    })

    theGraph
  }
}

class DelayingActor(name: String, delay: Long) extends ActorSubscriber with LazyLogging {
  override protected def requestStrategy: RequestStrategy = OneByOneRequestStrategy

  val actorName = name
  val consumerCounter = Kamon.metrics.counter("delayingactor-consumed-counter")

  def this(name: String) = this(name, 0)

  def receive = {
    case OnNext(msg: String) =>
      Thread.sleep(delay)
      logger.debug(s"Message in delaying actor sink ${self.path} '$actorName' : $msg")
      consumerCounter.increment(1)

    case OnComplete =>
      logger.debug(s"Completed Message receive in ${self.path} '$actorName'")

    case msg =>
      logger.debug(s"Unknown message $msg in $actorName")
  }
}

object Scenarios {

  def fastPublisherFastSubscriber() = {
    val theGraph = RunnableGraph.fromGraph(GraphDSL.create() { implicit  builder =>

      val source = builder.add(ThrottledProducer.produceThrottled(1 second, 20 milliseconds, 20000, "fastProducer"))
      val fastSink = builder.add(Sink.actorSubscriber(Props(classOf[DelayingActor], "fastsink")))

      import GraphDSL.Implicits._

      source ~> fastSink

      ClosedShape
    })

    theGraph
  }
}

object ThrottledApp {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("ThrottledSystem")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    //SimpleStream.printSimpleMessagesToConsole(materializer)

    //SimpleStream.throttledProducerToConsole().run()

    Scenarios.fastPublisherFastSubscriber().run()
    //Await.result(system.terminate(), Duration.Inf)
  }
}
