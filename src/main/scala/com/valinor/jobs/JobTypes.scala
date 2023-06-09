package com.valinor.jobs

import ErrorTypes._
import zio.json.JsonEncoder
import zio.prelude.{Associative, Newtype, Validation}
//import zio.prelude.Assertion._ // for compile-time validation
import java.time.Instant
import java.util.UUID
/*
  A new type in ZIO Prelude is a type that has the same underlying representation
  as another type at runtime but is a separate type at compile time.
*/
object JobTypes {
  type RequestValidation[T] = Validation[PostRequestError, T]

  object JobId extends Newtype[UUID] {
    implicit val encoder: JsonEncoder[JobId] = JsonEncoder[UUID].contramap { unwrap }
  }
  type JobId = JobId.Type

  object JobTitle extends Newtype[String] {
    implicit val encoder: JsonEncoder[JobTitle] = JsonEncoder[String].contramap { unwrap }

    private[jobs] def validate(title: String): RequestValidation[JobTitle] =
      if (title.isEmpty || title.length > 50) Validation.fail(PostRequestError("Job title must be between 1 and 50 characters"))
      else Validation.succeed(JobTitle(title))
  }
  type JobTitle = JobTitle.Type

  object JobDescription extends Newtype[Option[String]] {
    implicit val encoder: JsonEncoder[JobDescription] = JsonEncoder[Option[String]].contramap { unwrap }

    private[jobs] def validate(description: Option[String]): RequestValidation[JobDescription] =
      if (description.nonEmpty && description.get.length > 250)
        Validation.fail(PostRequestError("Job description must not exceed 250 characters"))
      else Validation.succeed(JobDescription(description))
  }
  type JobDescription = JobDescription.Type

  object HourlyRate extends Newtype[Double] {
    implicit val encoder: JsonEncoder[HourlyRate] = JsonEncoder[Double].contramap { unwrap }

    private[jobs] def validate(rate: Double): RequestValidation[HourlyRate] =
      if (rate <= 7.25) Validation.fail(PostRequestError("Hourly rate must be greater than $7.25 USD"))
      else Validation.succeed(HourlyRate(rate))
  }
  type HourlyRate = HourlyRate.Type

  object Created extends Newtype[Instant] {
    implicit val encoder: JsonEncoder[Created] = JsonEncoder[Instant].contramap { unwrap }
  }
  type Created = Created.Type

  object JobStatusChanged extends Newtype[Instant] {
    implicit val encoder: JsonEncoder[JobStatusChanged] = JsonEncoder[Instant].contramap { unwrap }
  }
  type JobStatusChanged = JobStatusChanged.Type

  object CompanyName extends Newtype[String] {
    implicit val encoder: JsonEncoder[CompanyName] = JsonEncoder[String].contramap { unwrap }

    private[jobs] def validate(name: String): RequestValidation[CompanyName] =
      if (name.isEmpty) Validation.fail(PostRequestError("Company name must not be empty"))
      else Validation.succeed(CompanyName(name))
  }
  type CompanyName = CompanyName.Type
}

object ErrorTypes {
  object PostRequestError extends Newtype[String] {
    implicit val associative: Associative[PostRequestError] = new Associative[PostRequestError] {
      override def combine(left: => PostRequestError, right: => PostRequestError): PostRequestError =
        PostRequestError(s"${unwrap(left)} | ${unwrap(right)}")
    }
  }
  type PostRequestError = PostRequestError.Type

  object GetRequestError extends Newtype[String]
  type GetRequestError = GetRequestError.Type
}
