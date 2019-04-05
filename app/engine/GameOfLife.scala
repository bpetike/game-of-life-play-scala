package engine

import play.Logger

class GameOfLife(initialWorld: World) {
  lazy val logger = Logger.of(classOf[GameOfLife])

  private var currentWorld = initialWorld

  def step(x: Int) = {
    currentWorld.generateNext
    logger.info(
      "Stepped " + x + " steps forward. Generation: " +
        currentWorld.generation + " population: " + currentWorld.population
    )
  }

  def reset(world: World) = {
    logger.info("Reset to world " + world)
    currentWorld = world
  }
}
