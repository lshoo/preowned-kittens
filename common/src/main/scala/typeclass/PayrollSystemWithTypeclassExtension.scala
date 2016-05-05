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

  implicit object JapanPayrollProcessor extends PayrollProcessor[JapanPayroll, Employee] {
    def processPayroll(payees: Seq[Employee]) = Left("japan employee are processed")
  }

  implicit object USContractorPayrollProcessor extends PayrollProcessor[USPayroll, Contractor] {
    def processPayroll(payees: Seq[Contractor]) = Left("us contractor are processed.")
  }

  implicit object CanadaContractorPayrollProcessor extends PayrollProcessor[CanadaPayroll, Contractor] {
    def processPayroll(payees: Seq[Contractor]) = Left("canada contractor are processed.")
  }

  implicit object JapanContractorPayrollProcessor extends PayrollProcessor[JapanPayroll, Contractor] {
    def processPayroll(payees: Seq[Contractor]) = Left("japan contractor are processed.")
  }
}

object RunNewPayroll {
  import PayrollSystemWithTypeclass._
  import PayrollSystemWithTypeclassExtension._
  import PayrollProcessors._
  import PayrollProcessorsExtension._

  def main(args: Array[String]) {
    run
  }

  def run = {
    val r1 = JapanPayroll(Vector(Employee("James", 2))).processPayroll
    val r2 = JapanPayroll(Vector(Contractor("Wade"))).processPayroll
    println(r1)
    println(r2)
  }
}
