package http

import cats.Applicative
import cats.effect._
import cats.implicits._
import db.Repository
import fs2.Stream
import domain.product.Product
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

object Routes {

  final class ProductRoutes[F[_]: Async: Applicative](repo: Repository[F])
      extends Http4sDsl[F] {
    implicit def decodeProduct: EntityDecoder[F, Product] = jsonOf
    implicit def encodeProduct: EntityEncoder[F, Product] = jsonEncoderOf

    val routes: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / "product" / UUIDVar(id) =>
        for {
          load <- repo.loadProduct(id).attempt
          resp <- load match {
            case Left(error) => InternalServerError(error.getMessage)
            case Right(rows) =>
              Product.fromDatabase(rows).fold(NotFound())(Ok(_))
          }
        } yield resp
      case req @ PUT -> Root / "product" / UUIDVar(id) =>
        for {
          p <- req.as[Product]
          update <- repo.updateProduct(p).attempt
          res <- update match {
            case Left(error) => InternalServerError(error.getMessage)
            case Right(count) =>
              count match {
                case 0 => NotFound()
                case _ => NoContent()
              }
          }
        } yield res
    }
  }

  final class ProductsRoutes[F[_]: Async](repo: Repository[F])
      extends Http4sDsl[F] {
    implicit def decodeProduct: EntityDecoder[F, Product] = jsonOf
    implicit def encodeProduct: EntityEncoder[F, Product] = jsonEncoderOf

    val routes: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / "products" =>
        val stream: Stream[F, Product] = repo
          .loadProducts()
          .attempt
          .map {
            case Left(error) =>
              throw new RuntimeException(s"${error.getMessage}")
            case Right(row) => row
          }
          .groupAdjacentBy(_._1)
          .map { case (_, rows) =>
            Product.fromDatabase(rows.toList)
          }
          .collect { case Some(p) => p }

        Ok(stream)
      case req @ POST -> Root / "products" =>
        for {
          p <- req.as[Product]
          save <- repo.saveProduct(p).attempt
          res <- save match {
            case Left(error) => InternalServerError(error.getMessage)
            case Right(count) =>
              count match {
                case 0 => InternalServerError()
                case _ => NoContent()
              }
          }
        } yield res
    }
  }
}
