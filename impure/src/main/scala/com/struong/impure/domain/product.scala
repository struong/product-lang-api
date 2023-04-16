package com.struong.impure.domain

import cats.data.NonEmptySet
import com.struong.impure.domain.translation._
import eu.timepit.refined.auto._
import io.circe.{Decoder, Encoder}

import java.util.UUID

object product {
  type ProductId = java.util.UUID

  final case class Product(id: ProductId, names: NonEmptySet[Translation])

  object Product {
    def tupled = (Product.apply _).tupled

    implicit val decode: Decoder[Product] =
      Decoder.forProduct2("id", "names")(Product.apply)

    implicit val encode: Encoder[Product] =
      Encoder.forProduct2("id", "names")(p => (p.id, p.names))

    //  Try to create a Product from the given list of database rows.
    def fromDatabase(rows: Seq[(UUID, String, String)]): Option[Product] = {
      import translation._

      Translation("ab", "b")

      val productOption =
        for {
          (id, lang, name) <- rows.headOption
          t <- Translation.unsafeApply(lang, name)
        } yield {
          Product(id = id, NonEmptySet.one(t))
        }

      productOption.map { product =>
        rows.drop(1).foldLeft(product) { case (a, cols) =>
          val (_, lang, productName) = cols
          Translation
            .unsafeApply(lang, productName)
            .fold(a)(translation => a.copy(names = a.names.add(translation)))
        }
      }
    }
  }
}
