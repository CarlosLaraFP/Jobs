package com.valinor.jobs

import zio._
import zio.json._
import zio.prelude.{Subtype, Validation}

import java.time.Instant
import java.util.UUID
import Newtypes._
import zio.http.Request

import java.nio.charset.Charset

// Bounded context

final case class CreateJobRequest(
  title: String,
  description: Option[String],
  hourlyRate: Double,
  companyName: String,
  companySize: Int
)
object CreateJobRequest {
  implicit val decoder: JsonDecoder[CreateJobRequest] = DeriveJsonDecoder.gen[CreateJobRequest]

  def deserialize(request: Request): IO[PostRequestError, CreateJobRequest] = {
    for {
      req <- request.body.asString(Charset.defaultCharset())
      createJobRequest <- ZIO.fromEither { req.fromJson[CreateJobRequest] }
    } yield createJobRequest
  }.mapError {
    case t: Throwable => PostRequestError(t.getMessage)
    case e: String => PostRequestError(e)
  }
}

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
) {
  override def toString: String =
    s"${CompanyName.unwrap(name)} ${status.toString.toLowerCase} ${JobTitle.unwrap(title)} #${JobId.unwrap(id)}"
}
object Job {
  type RequestValidation[T] = Validation[PostRequestError, T]

  def createFromRequest(request: CreateJobRequest): IO[PostRequestError, Job] = {
    Validation.validateWith(
      validateJobTitle(request.title),
      validateJobDescription(request.description),
      validateCompanyName(request.companyName),
      validateCompanySize(request.companySize),
      validateHourlyRate(request.hourlyRate)
    )(createJob)
  }.toZIOAssociative

  def createJob(
    jobTitle: JobTitle,
    jobDescription: JobDescription,
    companyName: CompanyName,
    companySize: CompanySize,
    hourlyRate: HourlyRate
  ): Job = Job(
    JobId(UUID.randomUUID),
    jobTitle,
    jobDescription,
    companyName,
    companySize,
    JobStatus.Created,
    hourlyRate,
    Created(Instant.now),
    JobStatusChanged(Instant.now)
  )

  def validateJobTitle(title: String): RequestValidation[JobTitle] =
    if (title.isEmpty) Validation.fail(PostRequestError("Job title must not be empty"))
    else Validation.succeed(JobTitle(title))

  def validateJobDescription(description: Option[String]): RequestValidation[JobDescription] =
    if (description.nonEmpty && description.get.length > 250)
      Validation.fail(PostRequestError("Job description must not exceed 250 characters"))
    else Validation.succeed(JobDescription(description))

  def validateCompanyName(name: String): RequestValidation[CompanyName] =
    if (name.isEmpty) Validation.fail(PostRequestError("Company name must not be empty"))
    else Validation.succeed(CompanyName(name))

  def validateCompanySize(size: Int): RequestValidation[CompanySize] =
    if (size < 0) Validation.fail(PostRequestError("Company size must be greater than zero"))
    else Validation.succeed(CompanySize.getCategory(size))

  def validateHourlyRate(rate: Double): RequestValidation[HourlyRate] =
    if (rate <= 7.25) Validation.fail(PostRequestError("Hourly rate must be greater than $7.25 USD"))
    else Validation.succeed(HourlyRate(rate))
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
