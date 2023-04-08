package com.valinor

import zio._
import zio.http._
import zio.http.model.Method

import scala.language.postfixOps


object WebsiteApp extends ZIOAppDefault {
  /*
    ZIO HTTP handles each incoming request in its own Fiber out-of-the-box.
    HttpApp is a type alias for Http[-R, +E, Request, Response]
    curl -i http://localhost:8080/greet
  */
  private val app: HttpApp[Any, Nothing] = Http.collectZIO[Request] {

      case Method.GET -> !! / "greet" =>
        ZIO.succeed {
          Response.text("Welcome to Valinor")
        }

      case _ => ZIO.succeed {
        Response.text("Health check passed")
      }
    }

  override val run =
    for {
      _ <- Console.printLine(s"Starting server at port 8080")
      _ <- Server
        .serve(app)
        .provide(
          Server.live(
            ServerConfig.default // port 8080 by default
          ),
          ZLayer.Debug.mermaid
        )
    } yield ()
}
