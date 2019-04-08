package engine

import java.io.File

import play.Logger

class GameOfLife(initialWorld: World) {
  lazy val logger: Logger.ALogger = Logger.of(classOf[GameOfLife])

  private var currentWorld = initialWorld

  def step(x: Int): Unit = {
    currentWorld.generateNext
    logger.info(
      "Stepped " + x + " steps forward. Generation: " +
        currentWorld.generation + " population: " + currentWorld.population
    )
  }

  def reset(world: World): Unit = {
    logger.info("Reset to world " + world)
    currentWorld = world
  }
}
