package com.valinor.jobs

import JobTypes._
import ErrorTypes._
import zio._
import zio.http._
import zio.http.model.Method


object JobsApp extends ZIOAppDefault {
  /*
    ZIO HTTP handles each incoming request in its own Fiber out-of-the-box.
    HttpApp is a type alias for Http[-R, +E, Request, Response]
    curl -i http://localhost:8080/jobs/Valinor
    curl -X POST http://localhost:8080/jobs -d '{"title":"Scala Backend Engineer","hourlyRate":18.50,"companyName":"Valinor","companySize":1150}'
  */
  private val route = "jobs"

  private val app: HttpApp[JobService, Nothing] = Http.collectZIO[Request] {

    case req@ Method.POST -> !! / route => {
      for {
        jobService <- ZIO.service[JobService]
        job <- jobService.createJob(req)
      } yield Response.text(job.toString)
    } catchAll {
      e: PostRequestError => ZIO.succeed(Response.text(e))
    }

    case Method.GET -> !! / route / company => {
      for {
        jobService <- ZIO.service[JobService]
        jobs <- jobService.getJobs(CompanyName(company))
        response <- jobService.jobsResponse(jobs)
      } yield response
    } catchAll {
      e: GetRequestError => ZIO.succeed {
        Response.text(e)
      }
    }

    case _ => ZIO.succeed {
      Response.text("Scala FP with ZIO")
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
          JobService.live,
          DatabaseService.live,
          ZLayer.Debug.mermaid
        )
    } yield ()
}
