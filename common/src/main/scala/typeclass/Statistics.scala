package typeclass

import typeclass.Match.NumberLike

/**
  * Please doc ...
  */
object Statistics {
  def median[T](xs: Vector[T])(implicit ev: NumberLike[T]): T = xs(xs.size / 2)
  def quartiles[T](xs: Vector[T])(implicit ev: NumberLike[T]): (T, T, T) =
    (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
  def iqr[T: NumberLike](xs: Vector[T]): T = quartiles(xs) match {
    case (lowerQuartile, _, upperQuartile) =>
      implicitly[NumberLike[T]].minus(upperQuartile, lowerQuartile)
  }
  def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T = {
    ev.divide(xs.reduce(ev.plus(_, _)), xs.size)
  }
}

object Match {
  trait NumberLike[T] {
    def plus(x: T, y: T): T
    def minus(x: T, y: T): T
    def divide(x: T, y: Int): T
  }

  object NumberLike {
    implicit object NumberLikeDouble extends NumberLike[Double] {
      def plus(x: Double, y: Double): Double = x + y
      def divide(x: Double, y: Int): Double = x / y
      def minus(x: Double, y: Double): Double = x - y
    }

    implicit object NumberLikeInt extends NumberLike[Int] {
      def plus(x: Int, y: Int) = x + y
      def divide(x: Int, y: Int) = x / y
      def minus(x: Int, y: Int) = x - y
    }
  }

}

object DurationImplicits {
  import Match._

  import java.time._
  implicit object NumberLikeDuration extends NumberLike[Duration] {
    def plus(x: Duration, y: Duration): Duration = x.plus(y)
    def divide(x: Duration, y: Int): Duration =
      Duration.ofMillis(x.toMillis / y)
    def minus(x: Duration, y: Duration): Duration = x.minus(y)
  }
}