package com.valinor.jobs

import zio.prelude.{Associative, Newtype, Subtype, Validation}
//import zio.prelude.Assertion._ // for compile-time validation
import java.time.Instant
import java.util.UUID
/*
  A new type in ZIO Prelude is a type that has the same underlying representation
  as another type at runtime but is a separate type at compile time.
*/
object Newtypes {
  type RequestValidation[T] = Validation[PostRequestError, T]

  object PostRequestError extends Subtype[String] {
    implicit val associative: Associative[PostRequestError] =
      new Associative[PostRequestError] {
        def combine(left: => PostRequestError, right: => PostRequestError): PostRequestError = {
          PostRequestError(s"$left | $right")
        }
      }
  }
  type PostRequestError = PostRequestError.Type

  object JobId extends Newtype[UUID]
  type JobId = JobId.Type

  object JobTitle extends Newtype[String] {
    def validate(title: String): RequestValidation[JobTitle] =
      if (title.isEmpty) Validation.fail(PostRequestError("Job title must not be empty"))
      else Validation.succeed(JobTitle(title))
  }
  type JobTitle = JobTitle.Type

  object JobDescription extends Newtype[Option[String]] {
    def validate(description: Option[String]): RequestValidation[JobDescription] =
      if (description.nonEmpty && description.get.length > 250)
        Validation.fail(PostRequestError("Job description must not exceed 250 characters"))
      else Validation.succeed(JobDescription(description))
  }
  type JobDescription = JobDescription.Type

  object HourlyRate extends Newtype[Double] {
    def validate(rate: Double): RequestValidation[HourlyRate] =
      if (rate <= 7.25) Validation.fail(PostRequestError("Hourly rate must be greater than $7.25 USD"))
      else Validation.succeed(HourlyRate(rate))
  }
  type HourlyRate = HourlyRate.Type

  object Created extends Newtype[Instant]
  type Created = Created.Type

  object JobStatusChanged extends Newtype[Instant]
  type JobStatusChanged = JobStatusChanged.Type

  object CompanyName extends Newtype[String] {
    def validate(name: String): RequestValidation[CompanyName] =
      if (name.isEmpty) Validation.fail(PostRequestError("Company name must not be empty"))
      else Validation.succeed(CompanyName(name))
  }
  type CompanyName = CompanyName.Type
}
