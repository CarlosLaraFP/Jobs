package com.valinor.jobs

import JobTypes._
import ErrorTypes._
import zio._
import zio.json._
import zio.http.{Request, Response}
import zio.prelude.NonEmptyList
import scala.collection.mutable.{Map => MutableMap}


class JobService(databaseService: DatabaseService) {
  def createJob(request: Request): IO[PostRequestError, Job] = for {
    createJobRequest <- CreateJobRequest.deserialize(request)
    job <- Job.createFromRequest(createJobRequest)
    _ <- databaseService.storeInMemory(job)
  } yield job

  def getJobs(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]] =
    ZIO.fromOption {
      databaseService.jobs.get(companyName)
    }.mapError { _ =>
      GetRequestError(s"${CompanyName.unwrap(companyName)} does not have any jobs.")
    }

  def jobsResponse(jobs: NonEmptyList[Job]): UIO[Response] = ZIO.succeed {
    val jobsJson = jobs.map(_.toJson).mkString(",")
    Response.text(s"[$jobsJson]")
  }
}
object JobService {
  private def create(databaseService: DatabaseService): JobService = new JobService(databaseService)

  val live: ZLayer[DatabaseService, Throwable, JobService] = ZLayer.fromFunction(create _)
}

class DatabaseService {
  // TODO: TMap for thread safety
  val jobs: MutableMap[CompanyName, NonEmptyList[Job]] = MutableMap.empty

  def storeInMemory(job: Job): IO[PostRequestError, Unit] =
    ZIO.attempt {
      jobs.update(
        job.name,
        jobs.get(job.name) match {
          case None => NonEmptyList(job)
          case Some(jobs) => job :: jobs
        }
      )
    }.mapError { t: Throwable =>
      PostRequestError(t.getMessage)
    }

  // TODO: Implement Doobie
  def storeInPostgres(job: Job): IO[PostRequestError, Unit] = ???
}
object DatabaseService {
  private def create: DatabaseService = new DatabaseService

  val live: ZLayer[Any, Throwable, DatabaseService] = ZLayer.succeed(create)
}
