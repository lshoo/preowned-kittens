package reactivescalaakka

import scala.io.Source
import java.io.File

object Indexer {

  def readFile(path: String, skipLines: Int): Iterator[String] = {
    Source.fromFile(new File(path)).getLines.drop(skipLines)
  }

}
