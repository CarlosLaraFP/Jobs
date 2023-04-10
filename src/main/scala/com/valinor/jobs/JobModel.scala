package com.valinor.jobs

import JobTypes._
import ErrorTypes._
import zio.prelude.Validation
import zio._
import zio.json._
import zio.http.Request
import java.time.Instant
import java.util.UUID
import java.nio.charset.Charset

/*
  Event storming is a collaborative workshop technique used to model complex business domains and design event-driven architectures.
  It helps teams to gain a shared understanding of the business domain, its processes, and the interactions between different actors in the system.
  In the context of microservices, event storming is typically used to identify the boundaries of a microservice and to define its responsibilities.
  It helps to identify the events that the microservice should react to and the actions that it should take in response.

  Bounded contexts are a key concept in domain-driven design (DDD) and microservices architecture.
  A bounded context is a specific area of a business domain where a particular language, model, and set of rules apply.
  It represents a boundary within which a particular microservice operates, and defines the boundaries of a microservice and the relationships between microservices.

  For example, consider an e-commerce platform that allows customers to browse and purchase products.
  One bounded context could be the product catalog, which defines how products are described, categorized, and priced.
  Another bounded context could be the shopping cart, which handles the customer's selections, quantities, and payment information.
  Each of these bounded contexts could be implemented as a separate microservice, with clear boundaries and well-defined interactions between them.

  In conclusion, event storming is a useful technique for identifying the boundaries of microservices and defining t
  heir responsibilities, while bounded contexts help to define the boundaries of a microservice and the relationships
  between microservices in a complex business domain.
*/

final case class CreateJobRequest private (
  title: String,
  description: Option[String],
  hourlyRate: Double,
  companyName: String,
  companySize: Int
)
object CreateJobRequest {
  // This is the only way CreateJobRequest can be instantiated
  implicit val decoder: JsonDecoder[CreateJobRequest] = DeriveJsonDecoder.gen[CreateJobRequest]

  // TODO: Property-based tests
  def deserialize(request: Request): IO[PostRequestError, CreateJobRequest] = {
    for {
      req <- request.body.asString(Charset.defaultCharset())
      createJobRequest <- ZIO.fromEither { req.fromJson[CreateJobRequest] }
    } yield createJobRequest
  } mapError {
    case t: Throwable => PostRequestError(t.getMessage)
    case e: String => PostRequestError(e)
  }
}

/*
  Preventing instantiation through constructor or apply methods and preventing .copy => illegal Job instances are unrepresentable
  In Scala 3, case classes with private constructors will have apply and copy methods private automatically.
*/
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
  // TODO: Property-based tests
  def createFromRequest(request: CreateJobRequest): IO[PostRequestError, Job] = {
    // Run-time validation with error accumulation
    Validation.validateWith(
      JobTitle.validate(request.title),
      JobDescription.validate(request.description),
      CompanyName.validate(request.companyName),
      CompanySize.validate(request.companySize),
      HourlyRate.validate(request.hourlyRate)
    )(createJob)
  }.toZIOAssociative

  private def createJob(
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

  implicit val encoder: JsonEncoder[Job] = DeriveJsonEncoder.gen[Job]
}

sealed trait JobStatus
object JobStatus {
  case object Created extends JobStatus
  case object Filled extends JobStatus
  case object Completed extends JobStatus

  implicit val encoder: JsonEncoder[JobStatus] = DeriveJsonEncoder.gen[JobStatus]
}

sealed trait CompanySize
object CompanySize {
  private case object Small extends CompanySize
  private case object Medium extends CompanySize
  private case object Large extends CompanySize
  private case object Enterprise extends CompanySize

  implicit val encoder: JsonEncoder[CompanySize] = DeriveJsonEncoder.gen[CompanySize]

  private def getCategory(size: Int): CompanySize = size match {
    case a if a < 50 => Small
    case b if b > 50 && b < 250 => Medium
    case c if c > 250 && c < 1000 => Large
    case _ => Enterprise
  }

  def validate(size: Int): RequestValidation[CompanySize] =
    if (size < 0) Validation.fail {
      PostRequestError("Company size must be greater than zero")
    }
    else Validation.succeed {
      CompanySize.getCategory(size)
    }
}
