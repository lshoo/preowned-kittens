package example

import kafka.example.KafkaConfig
import org.scalatest.FunSuite

class KafkaConfigSpec extends FunSuite {
  val config = new KafkaConfig {}

  test("Consumer config should be read") {
    assert(config.getProperty("group.id") == "1234")
    assert(config.getProperty("zookeeper.connect") == "localhost:2181")
  }

  test("kafka.producer.Producer config should be read") {
    assert(config.getProperty("metadata.broker.list") == "localhost:9092")
    assert(config.getProperty("serializer.class") == "kafka.serializer.StringEncoder")
    //assert(config.getProperty("partitioner.class") == "kafka.producer.SimplePartitioner")
    assert(config.getProperty("request.required.acks") == "1")
  }

  test("Missing keys should be null") {
    assert(config.getProperty("some.other.key") == null)
  }
}