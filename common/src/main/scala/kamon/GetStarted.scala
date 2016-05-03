package kamon

/**
  * Please doc ...
  */
object GetStarted extends App {
  Kamon.start()

  val someHistogram = Kamon.metrics.histogram("some-histogram")
  val someCounter = Kamon.metrics.counter("some-counter")

  someHistogram.record(42)
  someHistogram.record(50)
  someCounter.increment()

  println(someHistogram)
  println(someCounter)

  // This application wont terminate unless you shutdown Kamon.
  Kamon.shutdown()
}
