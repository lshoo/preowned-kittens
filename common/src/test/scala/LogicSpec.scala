package com.lshoo

import org.scalatest._

class LogicSpec extends FreeSpec with Matchers {
  "The 'matchLikehihood' method " - {
    "be 100% when all attributes match " in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = BuyerPreferences(List("male", "tabby"))
      Logic.matchLikelihood(tabby, prefs) should be (1)
    }

    "be 0% when no attributes match" in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = BuyerPreferences(List("female", "calico"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result should be < (0.001)
    }
  }

}