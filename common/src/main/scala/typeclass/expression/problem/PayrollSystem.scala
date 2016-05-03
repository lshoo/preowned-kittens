package typeclass.expression.problem

/**
  * Please doc ...
  */

trait PayrollSystem {
  case class Employee(name: String, id: Long)
  type P <: Payroll

  trait Payroll {
    def processEmployees(
                          employees: Vector[Employee]
                        ): Either[String, Throwable]
  }

  def processPayroll(p: P): Either[String, Throwable]
}

trait USPayrollSystem extends PayrollSystem {
  class USPayroll extends Payroll {
    def processEmployees(employees: Vector[Employee]) =
      Left("US payroll")
  }
}

trait CanadaPayrollSystem extends PayrollSystem {
  class CanadaPayroll extends Payroll {
    def processEmployees(employees: Vector[Employee]) =
      Left("Canada payroll")
  }
}

object USPayrollInstance extends USPayrollSystem {
  type P = USPayroll

  def processPayroll(p: USPayroll) = {
    val employees: Vector[Employee] = Vector.empty[Employee]
    p.processEmployees(employees)
  }
}

trait JapanPayrollSystem extends PayrollSystem {
  class JapanPayroll extends Payroll {
    def processEmployees(emplyees: Vector[Employee]) =
      Left("Japan payroll")
  }
}

