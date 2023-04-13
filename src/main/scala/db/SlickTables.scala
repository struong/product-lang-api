package db

import domain.product.Product

import java.util.UUID

object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  final class Products(tag: Tag) extends Table[(UUID)](tag, "products") {
    def id = column[UUID]("id", O.PrimaryKey)

    def * = (id)
  }
  val productsTable = TableQuery[Products]

  final class Names(tag: Tag)
      extends Table[(UUID, String, String)](tag, "names") {
    def productId = column[UUID]("product_id")

    def langCode = column[String]("lang_code")

    def name = column[String]("name")

    def pk = primaryKey("names_pk", (productId, langCode))

    def productFk =
      foreignKey("names_product_id_fk", productId, productsTable)(
        _.id,
        onDelete = ForeignKeyAction.Cascade,
        onUpdate = ForeignKeyAction.Cascade
      )

    def * = (productId, langCode, name)
  }

  val namesTable = TableQuery[Names]
}
