package example.consumer

import example.KafkaConfig
import kafka.consumer._

/**
  * Please doc ...
  */
abstract class Consumer(topics: List[String]) {

  protected val kafkaConfig = KafkaConfig()
  protected val config = new ConsumerConfig(kafkaConfig)

  def read(): Iterable[String]
}
