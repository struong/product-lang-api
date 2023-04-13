package domain

import cats.data.NonEmptySet
import domain.translation.Translation
import io.circe.{Decoder, Encoder}

object product {
  type ProductId = java.util.UUID

  final case class Product(id: ProductId, names: NonEmptySet[Translation])

  object Product {
    def tupled = (Product.apply _).tupled
    implicit val decode: Decoder[Product] =
      Decoder.forProduct2("id", "names")(Product.apply)

    implicit val encode: Encoder[Product] =
      Encoder.forProduct2("id", "names")(p => (p.id, p.names))
  }
}
