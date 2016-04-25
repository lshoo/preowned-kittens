
/**
  * https://code.sahebmotiani.com/patterns-in-scala-101-5d0fa70aaf3f#.fwhbofdh4
  */
object PatternsInScala {

  // Legacy
  abstract class Lannister {
    def payTheirDebts: Boolean
    def trueLannister = payTheirDebts
  }

  // Father
  trait Tywin extends Lannister {
    override def payTheirDebts = true
    def debt: Int
    def addToDebt(amount: Int) = debt + amount
  }

  // Son #1
  trait Jamie extends Tywin {
    override def payTheirDebts = true
    override def addToDebt(amount: Int) = super.addToDebt(amount * 2)
  }

  // Son #2
  trait Tyrion extends Tywin {
    override def payTheirDebts = true
    override def addToDebt(amount: Int) = amount
  }

  // Daughter #1
  trait Cersei extends Tywin {
    override def payTheirDebts = false
    override def addToDebt(amount: Int) = super.addToDebt(amount / 4)
  }

  class Joffrey extends Lannister with Jamie with Cersei {
    override def debt = 100
    override def addToDebt(amount: Int) = debt * 10
  }

  class Tommen extends Lannister with Cersei with Jamie {
    override def debt = 200
  }

  // Tyrion son
  class Leon extends Tyrion {
    override def debt = 300
  }
}

object GameOfThrones extends App {
  import PatternsInScala._

  val joffrey = new Joffrey
  println(joffrey.addToDebt(100))

  val tommen = new Tommen
  println(tommen.addToDebt(100))

  val leon = new Leon with Jamie
  println(leon.addToDebt(100))

  val leon2 = new Leon with Jamie
  println(leon2.addToDebt(200))

  val leon3 = new Leon with Cersei
  println(leon3.addToDebt(100))
}