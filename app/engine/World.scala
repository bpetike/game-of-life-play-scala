package engine

trait World {
  def isAlive(loc: Location): Boolean

  def aliveNeighbours(loc: Location): Set[Location]

  def deadNeighbours(loc: Location): Set[Location]

  def aliveLocations: Iterable[Location]

  def population: Int

  def generation: Int

  def generateNext: World

  def staysAliveAtNextGeneration(loc: Location) = {
    aliveNeighbours(loc).size match {
      case 2 | 3 => true
      case _ => false
    }
  }

  def becomesAliveAtNextGeneration(loc: Location) = {
    aliveNeighbours(loc).size == 3
  }

  def neighbourLocations(loc: Location) = {
    val offsets = Set(-1, 0, 1)
    for {dx <- offsets
         dy <- offsets
         if (dx, dy) != (0, 0)}
      yield Location(loc.x + dx, loc.y + dy)
  }
}

case class Location(x: Int, y: Int)
