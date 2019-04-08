package engine

import org.apache.commons.lang3.StringUtils

import scala.collection.{GenMap, GenSet}
import scala.io.Source
import scala.language.implicitConversions

object SparseMatrix {
  implicit def string2matrix(s: String): SparseMatrix = {

    def parseString(d: String): GenMap[Int, GenSet[Int]] = {
      // remove comment lines
      val lines = Source.fromString(d).getLines().filter(!_.startsWith("#"))
      lines.zipWithIndex
        .flatMap {
          case (line, rowIndex) =>
            // get indices of '*' characters on the line
            val colIndices =
              line.zipWithIndex.filter(p => p._1 == '*').map(_._2)
            Map(rowIndex -> colIndices.toSet)
        }
        .filter(entry => entry._2.nonEmpty)
        .toMap
    }

    s match {
      case x if StringUtils.isBlank(x) => new SparseMatrix(Map())
      case d                           => new SparseMatrix(parseString(d))
    }
  }
}

/**
  * http://en.wikipedia.org/wiki/Conway's_Game_of_Life
  *
  * Sparse matrix uses immutable maps and sets to store GoL world in sparse matrix presentation
  * Only alive cells are stored.
  *
  * Map( 0 -> Set(0,1,3), 1 -> Set(2)) means that coordinates
  * (0,0), (0,1), (0,3) and (1,2) are alive
  *
  */
class SparseMatrix(val generation: Int, val data: GenMap[Int, GenSet[Int]])
    extends World {

  def this(d: GenMap[Int, GenSet[Int]]) = this(1, d)

  def generateNext: SparseMatrix = {
    var unprocessedNeighbours = Set[Location]()
    var result = data
      .flatMap { rowEntry =>
        val x = rowEntry._1
        val aliveCells = rowEntry._2.filter { y =>
          val loc = Location(x, y)
          // store dead neighbours for later processing
          unprocessedNeighbours ++= deadNeighbours(loc)
          staysAliveAtNextGeneration(loc)
        }
        Map(x -> aliveCells)
      }
      .filter(rowEntry => rowEntry._2.nonEmpty)

    // loop over dead neighbours of current alive cells
    unprocessedNeighbours.foreach(loc => {
      if (becomesAliveAtNextGeneration(loc)) {
        val ySet = result.getOrElse(loc.x, Set())
        result += loc.x -> (ySet ++ Set(loc.y))
      }
    })
    new SparseMatrix(generation + 1, result)
  }

  def isAlive(loc: Location): Boolean = {
    data.get(loc.x) match {
      case Some(value) => value.contains(loc.y)
      case None        => false
    }
  }

  def aliveNeighbourCount(loc: Location): Int = {
    aliveNeighbours(loc).size
  }

  def aliveNeighbours(loc: Location): Set[Location] = {
    this.neighbourLocations(loc).filter(l => isAlive(l))
  }

  def deadNeighbours(loc: Location): Set[Location] = {
    this.neighbourLocations(loc).filter(l => !isAlive(l))
  }

  def aliveLocations: Iterable[Location] = {
    (for ((x, xSet) <- data.seq; y <- xSet) yield Location(x, y)).seq
  }

  override def population: Int = data.map(entry => entry._2.size).sum

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: SparseMatrix => other.data == this.data
      case _                   => false
    }
  }

  override def toString: String = {
    this.getClass.getSimpleName + ":gen:" + generation + ":" + data
  }
}
