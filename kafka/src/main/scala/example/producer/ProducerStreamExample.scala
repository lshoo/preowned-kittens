package example.producer

import scala.util.Random

/**
  * Please doc ...
  */
object ProducerStreamExample {
  def main(args: Array[String]) {

    val producer = Producer[String]("testTopic")
    val messageStream = Stream.continually {
      Random.alphanumeric.take(5).mkString
    }.take(100)

    producer.sendStream(messageStream)
  }
}
