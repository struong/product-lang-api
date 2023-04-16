import cats.data._
import com.struong.impure.domain.product.{Product, ProductId}
import com.struong.impure.domain.translation._
import eu.timepit.refined.auto._
import eu.timepit.refined.api._
import org.scalacheck.{Arbitrary, Gen}

import java.util.UUID

object Generators {
  val genLanguageCode: Gen[LanguageCode] = Gen.oneOf(LanguageCodes.all)
  val genUuid: Gen[UUID] = Gen.delay(UUID.randomUUID())
  val genProductId: Gen[ProductId] = genUuid
  val defaultProductName: ProductName = "Default product name"
  val genProductName: Gen[ProductName] = for {
    str <- Gen.alphaStr
    name = RefType.applyRef[ProductName](str)
      .getOrElse(defaultProductName)
  } yield name

  val genTranslation: Gen[Translation] = for {
    lang <- genLanguageCode
    name <- genProductName
  } yield Translation(lang, name)

  implicit val arbitraryTranslation: Arbitrary[Translation] = Arbitrary(genTranslation)

  val genTranslationsList: Gen[List[Translation]] = for {
    translations <- Gen.nonEmptyListOf(genTranslation)
  } yield translations

  val genNonEmptyTranslationList: Gen[NonEmptySet[Translation]] = for {
    t <- genTranslation
    ts <- genTranslationsList
    ns = NonEmptyList.fromList(ts).map(_.toNes)
  } yield ns.getOrElse(NonEmptySet.one(t))

  val genProduct: Gen[Product] = for {
    id <- genProductId
    ts <- genNonEmptyTranslationList
  } yield Product(id, ts)

  implicit val arbitraryProduct: Arbitrary[Product] =
    Arbitrary(genProduct)
}
