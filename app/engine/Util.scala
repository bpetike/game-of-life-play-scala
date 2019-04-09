package engine

import scala.io.Source

object Util {
  def getWorld(worldName: String): World = {
    val content = Source.fromFile(s"public/${worldName.toLowerCase}.lif")
    val data: SparseMatrix = content.getLines().mkString("\n")
    content.close()
    data
  }
}
