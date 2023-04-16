package com.struong.pure.db

import cats.effect.Sync
import com.struong.pure.domain.product._
import com.struong.pure.domain.translation._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.refined.implicits._
import eu.timepit.refined.auto._

import fs2.Stream

trait Repository[F[_]] {
  def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]]
  def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)]
  def saveProduct(p: Product): F[Int]
  def updateProduct(p: Product): F[Int]
}

class DoobieRepository[F[_]: Sync](tx: Transactor[F]) extends Repository[F] {
  override def loadProduct(
      id: ProductId
  ): F[Seq[(ProductId, LanguageCode, ProductName)]] =
    sql"""SELECT products.id, names.lang_code, names.name
         FROM products
         JOIN names ON products.id = names.product_id
         WHERE products.id = $id"""
      .query[(ProductId, LanguageCode, ProductName)]
      .to[Seq]
      .transact(tx)

  override def loadProducts()
      : Stream[F, (ProductId, LanguageCode, ProductName)] =
    sql"""SELECT products.id, names.lang_code, names.name
         FROM products
         JOIN names ON products.id = names.product_id
         ORDER BY products.id"""
      .query[(ProductId, LanguageCode, ProductName)]
      .stream
      .transact(tx)

  override def saveProduct(p: Product): F[Int] = {
    val namesSql =
      "INSERT INTO names (product_id, lang_code, name) VALUES (?, ?, ?)"

    val namesValues = p.names.toNonEmptyList.map(t => (p.id, t.lang, t.name))

    val program = for {
      pi <- sql"INSERT INTO products (id) VALUES(${p.id})".update.run
      ni <- Update[(ProductId, LanguageCode, ProductName)](namesSql)
        .updateMany(namesValues)
    } yield pi + ni

    program.transact(tx)
  }

  override def updateProduct(p: Product): F[Int] = {
    val namesSql =
      "INSERT INTO names (product_id, lang_code, name) VALUES (?, ?, ?)"

    val namesValues = p.names.toNonEmptyList.map(t => (p.id, t.lang, t.name))

    val program = for {
      dl <- sql"DELETE FROM names WHERE product_id = ${p.id}".update.run
      ts <- Update[(ProductId, LanguageCode, ProductName)](namesSql)
        .updateMany(namesValues)
    } yield dl + ts

    program.transact(tx)
  }
}
