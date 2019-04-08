package controllers

import java.io.File

import engine.{GameOfLife, SparseMatrix}
import javax.inject.Inject
import play.Logger
import play.api.mvc._
import play.libs.Json

import scala.concurrent.ExecutionContext
import scala.io.Source

class GameOfLifeController @Inject()(cc: ControllerComponents)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  private val engine = new GameOfLife(new SparseMatrix(Map()))
  private val logger = Logger.of(classOf[GameOfLifeController])

  def setWorld(worldName: String): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val content = Source.fromFile(s"public/${worldName.toLowerCase}.lif")
      val data: SparseMatrix = content.getLines().mkString("\n")
      content.close()
      logger.info(s"World $worldName set to engine")
      engine.reset(data)
      Ok(data.toString)
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
            .toList.mkString(",")
        )
      } else {
        NotFound("Cannot find files")
      }
  }
}
