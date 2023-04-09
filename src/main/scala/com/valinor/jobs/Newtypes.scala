package com.valinor.jobs

import zio.prelude.Newtype
import java.time.Instant
import java.util.UUID

/*
  A new type in ZIO Prelude is a type that has the same underlying representation
  as another type at runtime but is a separate type at compile time.
*/
object Newtypes {
  object PostRequestError extends Newtype[String]
  type PostRequestError = PostRequestError.Type
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
}
