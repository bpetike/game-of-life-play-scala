package controllers

import engine._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test._
import play.api.test.Helpers._
import play.mvc.Http

import scala.concurrent.ExecutionContext.Implicits.global

class GameOfLifeControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "GameOfLifeController GET" should {
    "return the name of available worlds" in {
      val controller =
        new GameOfLifeController(stubControllerComponents())
      val home =
        controller.getWorldNames.apply(FakeRequest(GET, "/game/worlds"))

      status(home) mustBe OK
      contentType(home).get mustBe Http.MimeTypes.TEXT
      contentAsString(home).split(",").contains("Fuses") mustBe true
    }

    "set the world to the engine" in {
      val controller =
        new GameOfLifeController(stubControllerComponents())

      val home =
        controller.setWorld("Pi").apply(FakeRequest(GET, "/game/world/Pi"))

      status(home) mustBe OK
      contentType(home).get mustBe Http.MimeTypes.TEXT
      contentAsString(home) mustBe "SparseMatrix:gen:0:Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2))"
      controller.currentWorld.aliveLocations.toList mustBe List(
        Location(0, 0),
        Location(0, 1),
        Location(0, 2),
        Location(1, 0),
        Location(1, 2),
        Location(2, 0),
        Location(2, 2)
      )
      controller.currentWorld.population mustBe 7
    }

    "produce the next generation of the current world" in {
      val controller =
        new GameOfLifeController(stubControllerComponents())
      val initialWorld =
        new SparseMatrix(Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2)))
      controller.currentWorld = initialWorld
      controller.engine.reset(initialWorld)
      val home =
        controller.step.apply(FakeRequest(GET, "/game/step"))

      status(home) mustBe OK
      contentType(home).get mustBe Http.MimeTypes.TEXT
      contentAsString(home) mustBe "SparseMatrix:gen:1:Map(0 -> Set(0, 2), 1 -> Set(0, 2, -1, 3), -1 -> Set(1))"
      controller.currentWorld.aliveLocations.toList mustBe List(
        Location(0, 0),
        Location(0, 2),
        Location(1, 0),
        Location(1, 2),
        Location(1, -1),
        Location(1, 3),
        Location(-1, 1),
      )
      controller.currentWorld.population mustBe 7
      controller.currentWorld.generation mustBe 1
    }

    "reset the current world to the initial one" in {
      val controller =
        new GameOfLifeController(stubControllerComponents())
      val initialWorld =
        new SparseMatrix(Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2)))
      controller.currentWorld = initialWorld
      controller.engine.reset(initialWorld)
      controller.currentWorld = controller.engine.step(2)
      val home =
        controller.reset("Pi").apply(FakeRequest(GET, "/game/reset/Pi"))

      status(home) mustBe OK
      contentType(home).get mustBe Http.MimeTypes.TEXT // Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2))
      contentAsString(home) mustBe "SparseMatrix:gen:0:Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2))"
      controller.currentWorld.aliveLocations.toList mustBe List(
        Location(0, 0),
        Location(0, 1),
        Location(0, 2),
        Location(1, 0),
        Location(1, 2),
        Location(2, 0),
        Location(2, 2)
      )
      controller.currentWorld.population mustBe 7
    }

    "return the number of steps taken" in {
      val controller =
        new GameOfLifeController(stubControllerComponents())
      val initialWorld =
        new SparseMatrix(Map(0 -> Set(0, 1, 2), 1 -> Set(0, 2), 2 -> Set(0, 2)))
      controller.currentWorld = initialWorld
      controller.engine.reset(initialWorld)
      controller.currentWorld = controller.engine.step(2)

      val home =
        controller.getNumberOfSteps.apply(FakeRequest(GET, "/game/stepsnumber"))

      status(home) mustBe OK
      contentType(home).get mustBe Http.MimeTypes.TEXT
      contentAsString(home) mustBe "2"
    }
  }
}
