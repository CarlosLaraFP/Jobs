package com.valinor.jobs

import com.valinor.jobs.ErrorTypes.{GetRequestError, PostRequestError}
import com.valinor.jobs.JobTypes.CompanyName
import zio.IO
import zio.prelude.NonEmptyList

trait DatabaseService {
  protected[jobs] def create(job: Job): IO[PostRequestError, Unit]
  protected[jobs] def get(companyName: CompanyName): IO[GetRequestError, NonEmptyList[Job]]
}
