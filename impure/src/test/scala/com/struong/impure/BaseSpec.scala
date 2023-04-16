package com.struong.impure

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

abstract class BaseSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {}
