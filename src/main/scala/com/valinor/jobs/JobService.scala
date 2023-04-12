package com.valinor.jobs

import JobTypes._
import ErrorTypes._
import zio._
import zio.json._
import zio.http.{Request, Response}
import zio.prelude.NonEmptyList
import scala.collection.mutable.{Map => MutableMap}

// Use JobService trait?
final case class JobService(jobsRepository: JobsRepository) {
  def createJob(request: Request): UIO[Response] = {
    for {
      createJobRequest <- CreateJobRequest.deserialize(request)
      job <- Job.createFromRequest(createJobRequest)
      _ <- jobsRepository.create(job)
    } yield Response.text(job.toString)
  } catchAll { e: PostRequestError =>
    ZIO.succeed {
      Response.text(PostRequestError.unwrap(e))
    }
  }

  def getJobs(companyName: CompanyName): UIO[Response] = {
    for {
      jobs <- jobsRepository.get(companyName)
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
  val live: ZLayer[JobsRepository, Throwable, JobService] = ZLayer.fromFunction(this.apply _)
}

final case class InMemoryDatabase private () extends JobsRepository {
  // TODO: TMap for thread safety
  private val jobs: MutableMap[CompanyName, NonEmptyList[Job]] = MutableMap.empty

  override def create(job: Job): UIO[Unit] = ZIO.succeed {
    jobs.update(
      job.name,
      jobs.get(job.name) match {
        case None => NonEmptyList(job)
        case Some(jobs) => job :: jobs
      }
    )
  }

  override def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]] =
    ZIO.fromOption {
      jobs.get(companyName)
    } mapError { _ =>
      GetRequestError(s"${CompanyName.unwrap(companyName)} does not have any jobs.")
    }
}
object InMemoryDatabase {
  val live: ZLayer[Any, Throwable, InMemoryDatabase] = ZLayer.fromFunction(this.apply _)
}

final case class PostgresDatabase private () extends JobsRepository {
  // TODO: TMap for thread safety
  private val jobs: MutableMap[CompanyName, NonEmptyList[Job]] = MutableMap.empty

  // TODO: Implement Doobie
  override def create(job: Job): IO[PostRequestError, Unit] = ???

  override def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]] = ???
}
object PostgresDatabase {
  // TODO: Add DbConfig dependency
  val live: ZLayer[Any, Throwable, PostgresDatabase] = ZLayer.fromFunction(this.apply _)
}
