package com.struong.pure.domain

import cats.Order
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string._
import io.circe.generic.semiauto._
import io.circe.refined._
import io.circe.{Decoder, Encoder}

object translation {
  type LanguageCode = String Refined MatchesRegex[W.`"^[a-z]{2}$"`.T]
  type ProductName = String Refined NonEmpty

  final case class Translation(lang: LanguageCode, name: ProductName)

  object Translation {
    // For NonEmptySet decoding
    implicit val order: Order[Translation] = new Order[Translation] {
      override def compare(x: Translation, y: Translation): Int =
        x.lang.value.compareTo(y.lang.value)
    }

    implicit val decode: Decoder[Translation] = deriveDecoder
    implicit val encode: Encoder[Translation] = deriveEncoder
  }
}
