package engine

import play.Logger

class GameOfLife(initialWorld: World) {
  lazy val logger: Logger.ALogger = Logger.of(classOf[GameOfLife])

  private var currentWorld = initialWorld

  def step(x: Int): World = {
    logger.info(
      "Stepped " + x + " steps forward. Generation: " +
        currentWorld.generation + " population: " + currentWorld.population
    )
    for (i <- currentWorld.generation until currentWorld.generation + x)
      currentWorld = currentWorld.generateNext
    currentWorld
  }

  def reset(world: World): Unit = {
    logger.info("Reset to world " + world)
    currentWorld = world
  }
}
