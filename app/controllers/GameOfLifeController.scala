package controllers

import java.io.File

import engine._
import javax.inject.Inject
import play.Logger
import play.api.mvc._

import scala.concurrent.ExecutionContext

class GameOfLifeController @Inject()(cc: ControllerComponents)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  var currentWorld: World = new SparseMatrix(Map())
  var stepCount = 0
  val engine = new GameOfLife(new SparseMatrix(Map()))

  private val logger = Logger.of(classOf[GameOfLifeController])

  def setWorld(worldName: String): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val data = Util.getWorld(worldName)
      currentWorld = data
      engine.reset(data)
      logger.info(s"World $worldName set to engine")
      Ok(currentWorld.toString)
  }

  def getWorldNames: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val dir = new File("public")
      if (dir.exists() && dir.isDirectory) {
        Ok(
          dir
            .listFiles()
            .filter(_.isFile)
            .map(
              file =>
                file.getName.substring(0, file.getName.indexOf(".")).capitalize
            )
            .toList
            .mkString(",")
        )
      } else {
        NotFound("Cannot find files")
      }
  }

  def reset(worldName: String): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val world = Util.getWorld(worldName)
      engine.reset(world)
      currentWorld = world
      stepCount = 0
      logger.info(s"World $worldName has been reset")
      Ok(currentWorld.toString)
  }

  def step: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      currentWorld = engine.step(1)
      stepCount += 1
      logger.info(s"World has evolved to $stepCount generation")
      Ok(currentWorld.toString)
  }
}
