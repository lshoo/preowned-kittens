package typeclass

/**
  * Please doc ...
  */
object PayrollSystemWithTypeclassExtension {

  import PayrollSystemWithTypeclass._

  case class Contractor(name: String)

  case class JapanPayroll[A](payees: Seq[A]) {
    def processPayroll(implicit processor: PayrollProcessor[JapanPayroll, A]) =
      processor.processPayroll(payees)
  }
}

object PayrollProcessorsExtension {
  import PayrollSystemWithTypeclass._
  import PayrollSystemWithTypeclassExtension._

  implicit object JapanPayrollProcessor extends PayrollProcessor[JapanPayroll, Contractor] {
    def processPayroll(payees: Seq[Contractor]) = Left("japan contractor are processed")
  }
}
