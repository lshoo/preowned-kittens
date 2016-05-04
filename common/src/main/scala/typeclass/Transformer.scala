package typeclass

/**
  * http://www.casualmiracles.com/2012/05/03/a-small-example-of-the-typeclass-pattern-in-scala/
  */
trait Transformer[T, R] {
  def transform(t: T): R
}

object Transformer {
  implicit object IntToStringTransformer extends Transformer[Int, String] {
    def transform(t: Int): String = t.toString
  }

  implicit def ListToStringTransformer[T](implicit tToString: Transformer[T, String]) = new Transformer[List[T], String] {
    def transform(t: List[T]) = t.map(tToString.transform(_)).mkString(",")
  }
}


trait Transform {
  def transform[T, R](t: T)(implicit transformer: Transformer[T, R]): R = transformer.transform(t)
}

object ExampleWithDefault extends App with Transform {
  println(transform(2))
  println(transform(List(2, 3, 5)))
}