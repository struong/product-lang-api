package db

import cats.data.{NonEmptyList, NonEmptySet}
import db.SlickTables.{namesTable, productsTable}
import domain.product._
import domain.translation._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import eu.timepit.refined.auto._
import slick.basic._

import java.util.UUID
import scala.concurrent.Future

class Repository(dbConfig: DatabaseConfig[JdbcProfile]) {

  import dbConfig.profile.api._

  def loadProduct(id: ProductId): Future[Seq[(UUID, String, String)]] = {
    val program = for {
      (p, ns) <- productsTable
        .filter(_.id === id)
        .join(namesTable)
        .on(_.id === _.productId)
    } yield (p.id, ns.langCode, ns.name)

    dbConfig.db.run(program.result)
  }

  // Streams all products back
  def loadProducts(): DatabasePublisher[(UUID, String, String)] = {
    val program = for {
      (p, ns) <- productsTable
        .join(namesTable)
        .on(_.id === _.productId)
        .sortBy(_._1.id)
    } yield (p.id, ns.langCode, ns.name)

    dbConfig.db.stream(program.result)
  }

  def saveProduct(p: Product): Future[List[Int]] = {
    val productQuery = productsTable += (p.id)
    val program = DBIO.sequence(
      productQuery :: saveTranslations(p).toList
    ).transactionally

    dbConfig.db.run(program)
  }

  def updateProduct(p: Product): Future[List[Int]] = {
    val program =
      namesTable
        .filter(_.productId === p.id)
        .delete
        .andThen(DBIO.sequence(saveTranslations(p).toList))
        .transactionally

    dbConfig.db.run(program)
  }

  private def saveTranslations(p: Product): NonEmptyList[DBIO[Int]] = {
    p.names.toNonEmptyList.map(t => saveTranslation(p.id, t))
  }

  private def saveTranslation(id: ProductId, t: Translation): DBIO[Int] = {
    namesTable.insertOrUpdate(id, t.lang, t.name)
  }
}

