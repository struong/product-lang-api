package com.struong.pure.domain

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._
import eu.timepit.refined.types.string.NonEmptyString

object database {
  // A string containing a database login which must be non empty.
  type DatabaseLogin = NonEmptyString
  // A string containing a database password which must be non empty.
  type DatabasePassword = NonEmptyString
  // A string containing a database url.
  type DatabaseUrl = String Refined Uri
}
