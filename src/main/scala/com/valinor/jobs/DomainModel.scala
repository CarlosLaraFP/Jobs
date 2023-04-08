package com.valinor.jobs

import cats.data.Validated
import zio._
import zio.json._
import zio.prelude.{Newtype, Validation}
import java.time.Instant
import java.util.UUID

// Bounded context

/*
  A new type in ZIO Prelude is a type that has the same underlying representation
  as another type at runtime but is a separate type at compile time.
 */

final case class CreateJobRequest(
  title: String,
  description: Option[String],
  hourlyRate: Double,
  companySize: Int,
  companyName: String
)
object CreateJobRequest {
  implicit val decoder: JsonDecoder[CreateJobRequest] = DeriveJsonDecoder.gen[CreateJobRequest]

  def deserialize(request: String): Task[CreateJobRequest] =
    request.fromJson[CreateJobRequest] match {
      case Right(r) => ZIO.succeed(r)
      case Left(e) => throw new RuntimeException(e)
    }
}
object CreateJobRequestError extends Newtype[List[String]]
type CreateJobRequestError = CreateJobRequestError.Type

object JobId extends Newtype[UUID]
type JobId = JobId.Type
object JobTitle extends Newtype[String]
type JobTitle = JobTitle.Type
object JobDescription extends Newtype[Option[String]]
type JobDescription = JobDescription.Type
object HourlyRate extends Newtype[Double]
type HourlyRate = HourlyRate.Type
object Created extends Newtype[Instant]
type Created = Created.Type
object JobStatusChanged extends Newtype[Instant]
type JobStatusChanged = JobStatusChanged.Type
object CompanyName extends Newtype[String]
type CompanyName = CompanyName.Type

final case class Job private (
  id: JobId,
  title: JobTitle,
  description: JobDescription,
  name: CompanyName,
  size: CompanySize,
  status: JobStatus,
  rate: HourlyRate,
  created: Created,
  statusChanged: JobStatusChanged
)
object Job {
  def createFromRequest(request: CreateJobRequest): UIO[Validation[CreateJobRequestError, Job]] =
    ZIO.succeed {
      Validation.validateWith(
        validateJobTitle(request.title),
        validateJobDescription(request.description),
        validateCompanyName(request.companyName)
      )(createJob)
    }

  def createJob(jobTitle: JobTitle, jobDescription: JobDescription, companyName: CompanyName): Job =
    Job(
      JobId(UUID.randomUUID),
      jobTitle,
      jobDescription,
      companyName,
      CompanySize.getCategory(request.companySize),
      JobStatus.Created,
      HourlyRate(request.hourlyRate),
      Created(Instant.now),
      JobStatusChanged(Instant.now)
    )

  def validateJobTitle(title: String): Validation[CreateJobRequestError, JobTitle] =
    if (title.isEmpty) Validation.fail(CreateJobRequestError(List("Job title must not be empty")))
    else Validation.succeed(JobTitle(title))

  def validateJobDescription(description: Option[String]): Validation[CreateJobRequestError, JobDescription] =
    if (description.nonEmpty && description.get.length > 250)
      Validation.fail(CreateJobRequestError(List("Job description must not exceed 250 characters")))
    else Validation.succeed(JobDescription(description))

  def validateCompanyName(name: String): Validation[CreateJobRequestError, CompanyName] =
    if (name.isEmpty) Validation.fail(CreateJobRequestError(List("Company name must not be empty")))
    else Validation.succeed(CompanyName(name))

}

sealed trait JobStatus
object JobStatus {
  case object Created extends JobStatus
  case object Filled extends JobStatus
  case object Completed extends JobStatus
}

sealed trait CompanySize
object CompanySize {
  private case object Small extends CompanySize
  private case object Medium extends CompanySize
  private case object Large extends CompanySize
  private case object Enterprise extends CompanySize

  def getCategory(size: Int): CompanySize = size match {
    case a if a < 50 => Small
    case b if b > 50 && b < 250 => Medium
    case c if c > 250 && c < 1000 => Large
    case _ => Enterprise
  }
}
