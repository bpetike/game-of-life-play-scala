package engine

import org.scalatest.{Matchers, WordSpec}

class SparseMatrixSpec extends WordSpec with Matchers {
  "A SparseMatrix" should {
    "determine if a cell is alive or not" should {
      "return true if a cell is alive" in {
        val block = new SparseMatrix(Map(1 -> Set(2, 3), 2 -> Set(2, 3)))
        block.isAlive(Location(2, 3)) shouldBe true
      }

      "return false is a cell is not alive" in {
        val eyes = new SparseMatrix(Map(2 -> Set(1, 3)))
        eyes.isAlive(Location(2, 2)) shouldBe false
      }
    }

    "get alive neighbours" should {
      "return empty set if a cell has no alive neighbours" in {
        val verticalLine =
          new SparseMatrix(Map(1 -> Set(2), 2 -> Set(2), 3 -> Set(2)))
        verticalLine.aliveNeighbours(Location(6, 6)) shouldBe Set()
      }

      "return a set with one location if a cell has one alive neighbour" in {
        val dot = new SparseMatrix(Map(2 -> Set(2)))
        dot.aliveNeighbours(Location(2, 3)) shouldBe Set(Location(2, 2))
      }
    }

    "get locations of alive cells" in {
      val anotherDot = new SparseMatrix(Map(3 -> Set(2)))
      anotherDot.aliveLocations.toList shouldBe List(Location(3, 2))
    }

    "produce next generation" should {
      val block = new SparseMatrix(Map(1 -> Set(2, 3), 2 -> Set(2, 3)))
      "next generation of a block of cells is the same block" in {
        block.generateNext shouldBe block
      }

      "population for block of cells is 4" in {
        block.population shouldBe 4
      }

      val glider1 =
        new SparseMatrix(Map(1 -> Set(3), 2 -> Set(1, 3), 3 -> Set(2, 3)))
      val glider2 =
        new SparseMatrix(Map(1 -> Set(2), 2 -> Set(3, 4), 3 -> Set(2, 3)))
      val glider3 =
        new SparseMatrix(Map(1 -> Set(3), 2 -> Set(4), 3 -> Set(2, 3, 4)))

      "next generation of glider1 is glider2" in {
        glider1.generateNext shouldBe glider2
      }

      "next generation of glider2 is glider3" in {
        glider2.generateNext shouldBe glider3
      }
    }
  }
}
