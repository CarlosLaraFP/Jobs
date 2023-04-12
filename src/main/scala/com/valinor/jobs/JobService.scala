package com.valinor.jobs

import JobTypes._
import ErrorTypes._
import zio._
import zio.json._
import zio.http.{Request, Response}
import zio.prelude.NonEmptyList
import scala.collection.mutable.{Map => MutableMap}


class JobService(databaseService: DatabaseService) {
  def createJob(request: Request): UIO[Response] = {
    for {
      createJobRequest <- CreateJobRequest.deserialize(request)
      job <- Job.createFromRequest(createJobRequest)
      _ <- databaseService.create(job)
    } yield Response.text(job.toString)
  } catchAll { e: PostRequestError =>
    ZIO.succeed {
      Response.text(PostRequestError.unwrap(e))
    }
  }

  def getJobs(companyName: CompanyName): UIO[Response] = {
    for {
      jobs <- databaseService.get(companyName)
      json <- ZIO.succeed {
        jobs.map(_.toJson).mkString(",")
      }
    } yield Response.text(s"[$json]")
  } catchAll { e: GetRequestError =>
    ZIO.succeed {
      Response.text(GetRequestError.unwrap(e))
    }
  }
}
object JobService {
  private[this] def create(databaseService: DatabaseService): JobService = new JobService(databaseService)

  val live: ZLayer[DatabaseService, Throwable, JobService] = ZLayer.fromFunction(create _)
}

class InMemoryDatabase extends DatabaseService {
  // TODO: TMap for thread safety
  private val jobs: MutableMap[CompanyName, NonEmptyList[Job]] = MutableMap.empty

  override def create(job: Job): IO[PostRequestError, Unit] =
    ZIO.attempt {
      jobs.update(
        job.name,
        jobs.get(job.name) match {
          case None => NonEmptyList(job)
          case Some(jobs) => job :: jobs
        }
      )
    } mapError { t: Throwable => PostRequestError(t.getMessage) }

  override def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]] =
    ZIO.fromOption {
      jobs.get(companyName)
    } mapError { _ =>
      GetRequestError(s"${CompanyName.unwrap(companyName)} does not have any jobs.")
    }
}
object InMemoryDatabase {
  private[this] def create: InMemoryDatabase = new InMemoryDatabase

  val live: ZLayer[Any, Throwable, InMemoryDatabase] = ZLayer.succeed(create)
}

class PostgresDatabase extends DatabaseService {
  // TODO: TMap for thread safety
  private val jobs: MutableMap[CompanyName, NonEmptyList[Job]] = MutableMap.empty

  // TODO: Implement Doobie
  override def create(job: Job): IO[PostRequestError, Unit] = ???

  override def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]] = ???
}
object PostgresDatabase {
  private[this] def create: PostgresDatabase = new PostgresDatabase

  val live: ZLayer[Any, Throwable, PostgresDatabase] = ZLayer.succeed(create)
}
