package controllers

import java.io.File

import engine._
import javax.inject.Inject
import play.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext

class GameOfLifeController @Inject()(cc: ControllerComponents)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  private[controllers] var currentWorld: World = new SparseMatrix(Map())
  private[controllers] val engine = new GameOfLife(new SparseMatrix(Map()))

  private val logger = Logger.of(classOf[GameOfLifeController])

  def setWorld(worldName: String): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val data = Util.getWorld(worldName)
      currentWorld = data
      engine.reset(data)
      logger.info(s"World $worldName set to engine")
      val response = createResponse
      Ok(Json.toJson(response))
  }

  def getWorldNames: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val dir = new File("public")
      if (dir.exists() && dir.isDirectory) {
        Ok(
          Json.obj(
            "worldNames" ->
              dir
                .listFiles()
                .filter(_.isFile)
                .map(
                  file =>
                    file.getName
                      .substring(0, file.getName.indexOf("."))
                      .capitalize
                )
                .toList
          )
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
      logger.info(s"World $worldName has been reset")
      val response = createResponse
      Ok(Json.toJson(response))
  }

  def step: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      currentWorld = engine.step(1)
      logger.info(s"World has evolved to ${currentWorld.generation} generation")
      val response = createResponse
      Ok(Json.toJson(response))
  }

  def getNumberOfSteps: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(Json.obj("steps" -> currentWorld.generation))
  }

  private def createResponse: Iterable[JsObject] = {
    currentWorld.aliveLocations.toList
      .map(location => Json.obj("x" -> location.x, "y" -> location.y)).seq
  }
}
