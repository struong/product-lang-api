package com.struong.impure.domain

import com.struong.impure.domain.translation._
import com.struong.impure.BaseSpec
import com.struong.impure.Generators._
import eu.timepit.refined.api._
import io.circe.parser._
import io.circe.refined._
import io.circe.syntax._

class TranslationSpec extends BaseSpec {
  "Translation" when {
    "decoding from JSON" when {
      "fail to decode garbage input" in {
        forAll("input") { s: String =>
          decode[Translation](s).isLeft shouldBe true
        }
      }

      "fail to decode invalid input values" in {
        forAll("lang", "name") { (l: String, n: String) =>
          // only use invalid language codes and product names
          whenever(
            RefType
              .applyRef[LanguageCode](l)
              .toOption
              .isEmpty || RefType.applyRef[ProductName](n).toOption.isEmpty
          ) {
            val json =
              s"""
                     |{
                     | "lang": ${l.asJson},
                     | "name": ${n.asJson}
                     |}
                     |""".stripMargin

            decode[Translation](json).isLeft shouldBe true
          }
        }
      }

      "can decode valid input" in {
        forAll("input") { i: Translation =>
          val json =
            s"""{
                   |"lang": ${i.lang.asJson},
                   |"name": ${i.name.asJson}
                   |}""".stripMargin
          withClue(s"Unable to decode JSON: $json") {
            decode[Translation](json) match {
              case Left(e)  => fail(e.getMessage)
              case Right(v) => v shouldBe i
            }
          }
        }
      }
    }

    "encoding to JSON" must {
      "return decodeable JSON" in {
        forAll("input") { i: Translation =>
          decode[Translation](i.asJson.noSpaces) match {
            case Left(_) => fail("Must be able to decode encoded JSON!")
            case Right(d) =>
              withClue("Must decode the same product!")(d shouldBe i)
          }
        }
      }
    }
  }
}
