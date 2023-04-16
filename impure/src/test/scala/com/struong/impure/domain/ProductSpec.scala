package com.struong.impure.domain

import cats.implicits._
import com.struong.impure.domain.product._
import com.struong.impure.BaseSpec
import com.struong.impure.Generators._
import io.circe.parser._
import io.circe.syntax._

class ProductSpec extends BaseSpec {
  "Product" when {
    "decoding from JSON" when {
      "fail to decode garbage input" in {
        forAll("input") { s: String =>
          decode[Product](s).isLeft shouldBe true
        }
      }

      "fail to decode invalid input values" in {
        forAll("id", "names") { (badUuid: String, ns: List[String]) =>
          val json =
            s"""
            |{
            | "id": $badUuid,
            | "names": ${ns.asJson}
            |}
            |""".stripMargin

          decode[Product](json).isLeft shouldBe true
        }
      }

      "can decode valid input" in {
        forAll("input") { p: Product =>
          val json =
            s"""
             |{
             | "id": ${p.id.asJson},
             | "names": ${p.names.asJson}
             |}
             |""".stripMargin

          withClue(s"Unable to decode JSON: $json") {
            decode[Product](json) match {
              case Left(e)  => fail(e.getMessage)
              case Right(v) => v shouldBe p
            }
          }
        }
      }
    }

    "decoding from JSON" when {
      "round trip" in {
        forAll("input") { p: Product =>
          decode[Product](p.asJson.toString()) match {
            case Left(_) => fail("Must be able to decode encoded JSON!")
            case Right(d) => withClue("Must decode the same product!")(d shouldBe p)
          }
        }
      }
    }

    "fromDatabase" must {
      "create a product from a db row" in {
        forAll("input") { p: Product =>
          val rows = p.names.map(t => (p.id, t.lang.value, t.name.value)).toList
          Product.fromDatabase(rows) should contain(p)
        }
      }
    }
  }
}
