package com.valinor

import zio._
//import zio.json._
import zio.http._
import zio.http.model.Method
import java.nio.charset.Charset


object WebsiteApp extends ZIOAppDefault {
  /*
    ZIO HTTP handles each incoming request in its own Fiber out-of-the-box.
    HttpApp is a type alias for Http[-R, +E, Request, Response]
    curl -i http://localhost:8080/jobs
    curl -X POST http://localhost:8080/jobs -d '{"Name":"Scala Backend Engineer"}'
  */
  private val route = "jobs"

  private val app: HttpApp[Any, Nothing] = Http.collectZIO[Request] {

    case req@ Method.POST -> !! / route => {
      for {
        request <- req.body.asString(Charset.defaultCharset())
      } yield Response.text(request)
    }.catchAll { e =>
      ZIO.succeed {
        Response.text(e.getMessage)
      }
    }

    case Method.GET -> !! / route =>
      ZIO.succeed {
        Response.text("Jobs pending")
      }

    case _ => ZIO.succeed {
      Response.text("Valinor with ZIO")
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
