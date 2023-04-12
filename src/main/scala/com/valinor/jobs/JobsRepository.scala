package com.valinor.jobs

import com.valinor.jobs.ErrorTypes.{GetRequestError, PostRequestError}
import com.valinor.jobs.JobTypes.CompanyName
import zio.IO
import zio.prelude.NonEmptyList

/*
  Repositories present clients with a simple model for obtaining persistent objects and managing their lifecycle.
  They decouple application and domain design from persistence stack, database strategies, and/or data sources.
  They communicate design decisions about object access.
  They allow easy substitution of dummy implementations for use in testing (i.e. mock interfaces).
  - Eric Evans (Blue DDD book)
 */
trait JobsRepository {
  protected[jobs] def create(job: Job): IO[PostRequestError, Unit]
  protected[jobs] def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]]

  //protected[jobs] def update
  //protected[jobs] def delete
}
