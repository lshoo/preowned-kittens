import org.apache.ignite.Ignition
import org.apache.ignite.lang.IgniteCallable

import scala.collection.JavaConverters._

object IgniteCompute {

  def main(args: Array[String]) {
    val ignite = Ignition.start()
    val inputStr = "Count characters using callable"

    val calls = inputStr.split(" ").map { word =>
      new IgniteCallable[Int] {
        override def call(): Int = word.length
      }
    }

    val res = ignite.compute().call(calls.toSeq.asJava)

    println(s"Total number of characters in '$inputStr' is ${res.asScala.sum}")
  }
}
