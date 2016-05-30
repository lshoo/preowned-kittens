package example.producer

import java.util.Properties

import example.KafkaConfig
import kafka.producer.{KeyedMessage, ProducerConfig, Producer => KafkaProducer}
//import org.apache.kafka.clients.producer.KafkaProducer


/**
  * Please doc ...
  */
case class Producer[A](topic: String) {
  protected val config = new ProducerConfig(KafkaConfig())
  private lazy val producer = new KafkaProducer[A, A](config)

  def send(message: A) = sendMessage(producer, keyMessage(topic, message))

  def sendStream(stream: Stream[A]) = {
    val iter = stream.iterator
    while (iter.hasNext)
      send(iter.next())
  }

  private def sendMessage(producer: KafkaProducer[A, A], message: KeyedMessage[A, A]) =  producer.send(message)
  private def keyMessage(topic: String, message: A): KeyedMessage[A, A] = new KeyedMessage[A, A](topic, message)
}

object Producer {
  def apply[T](topic: String, props: Properties) = new Producer[T](topic) {
    override val config = new ProducerConfig(props)
  }
}
