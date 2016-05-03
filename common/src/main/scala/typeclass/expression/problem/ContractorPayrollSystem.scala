package typeclass.expression.problem

/**
  * Please doc ...
  */
trait ContractorPayrollSystem extends PayrollSystem {
  type P <: Payroll

  case class Contractor(name: String)

  trait Payroll extends super.Payroll {
    def processContractors(
                          contractors: Vector[Contractor]
                          ): Either[String, Throwable]
  }
}

trait USContractorPayrollSystem extends USPayrollSystem with ContractorPayrollSystem {
  class USPayroll extends super.USPayroll with Payroll {
    def processContractors(contractors: Vector[Contractor]) =
      Left("US contractor payroll")
  }
}