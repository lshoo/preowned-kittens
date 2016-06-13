package example.streams

import java.util.Properties

import org.apache.kafka.streams.StreamsConfig

/**
  * Please doc ...
  */
object Example {
  def main(args: Array[String]) {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")

  }
}
