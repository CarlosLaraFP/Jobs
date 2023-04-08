package com.valinor.jobs

import cats.data.Validated
import zio._
import zio.json._

import java.util.UUID

// Bounded context

case class CreateJobRequest(
  title: JobTitle,
  description: Option[JobDescription],
  hourlyRate: HourlyRate
)
object CreateJobRequest {
  implicit val decoder: JsonDecoder[CreateJobRequest] = DeriveJsonDecoder.gen[CreateJobRequest]

  def deserialize(request: String): Task[CreateJobRequest] =
    request.fromJson[CreateJobRequest] match {
      case Right(r) => ZIO.succeed(r)
      case Left(e) => throw new RuntimeException(e)
    }
}

case class JobTitle(title: String)
case class JobDescription(description: String)
case class HourlyRate(rate: Long)

case class Job private (id: UUID, title: JobTitle, description: JobDescription, rate: HourlyRate)

object Job {
  def createFromRequest(request: CreateJobRequest): UIO[Validated[Throwable, Job]] = ???
}
