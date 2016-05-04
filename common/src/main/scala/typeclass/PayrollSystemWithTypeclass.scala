package typeclass

import scala.annotation.implicitNotFound
import scala.language.higherKinds

/**
  * Please doc ...
  */
object PayrollSystemWithTypeclass {

  case class USPayroll[A](payees: Seq[A])(implicit processor: PayrollProcessor[USPayroll, A]) {
    def processPayroll = processor.processPayroll(payees)
  }

  case class CanadaPayroll[A](payees: Seq[A]) {

    def processPayroll(implicit processor: PayrollProcessor[CanadaPayroll, A]) = processor.processPayroll(payees)
  }

  @implicitNotFound("implicit type is not found")
  trait PayrollProcessor[C[_], A] {
    def processPayroll(payees: Seq[A]): Either[String, Throwable]
  }

  case class Employee(name: String, id: Long)

}

object PayrollProcessors {

  import PayrollSystemWithTypeclass._

  implicit object USPayrollProcessor extends PayrollProcessor[USPayroll, Employee] {
    def processPayroll(payees: Seq[Employee]) = Left("us employees are processed")
  }

  implicit object CanadaPayrollProcessor extends PayrollProcessor[CanadaPayroll, Employee] {
    def processPayroll(payees: Seq[Employee]) = Left("canada employees are processed")
  }

}

object RunPayroll {
  import PayrollSystemWithTypeclass._
  import PayrollProcessors._

  def run = {
    val r = CanadaPayroll(Vector(Employee("James", 1))).processPayroll
    println(r)
  }
  def main(args: Array[String]) {
    run
  }
}
