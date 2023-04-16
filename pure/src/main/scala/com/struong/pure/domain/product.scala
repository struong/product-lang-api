package com.struong.pure.domain

import cats.data.NonEmptySet
import com.struong.pure.domain.translation.{LanguageCode, ProductName, Translation}
import eu.timepit.refined.auto._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import io.circe.refined._

object product {
  type ProductId = java.util.UUID

  final case class Product(id: ProductId, names: NonEmptySet[Translation])

  object Product {
    def tupled = (Product.apply _).tupled

    implicit val decode: Decoder[Product] = deriveDecoder
    implicit val encode: Encoder[Product] = deriveEncoder

    //  Try to create a Product from the given list of database rows.
    def fromDatabase(rows: Seq[(ProductId, LanguageCode, ProductName)]): Option[Product] = {
      import translation._

      val productOption =
        for {
          (id, lang, name) <- rows.headOption
        } yield {
          Product(id = id, NonEmptySet.one(Translation(lang, name)))
        }

      productOption.map { product =>
        rows.drop(1).foldLeft(product) { case (a, cols) =>
          val (_, lang, productName) = cols
          a.copy(names = a.names.add(Translation(lang, productName)))
        }
      }
    }
  }
}
