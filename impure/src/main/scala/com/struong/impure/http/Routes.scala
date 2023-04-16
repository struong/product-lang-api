package com.struong.impure.http

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import cats.implicits._
import com.struong.impure.db.Repository
import com.struong.impure.domain.product.Product
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.{ExecutionContext, Future}

class Routes(repo: Repository)(implicit ec: ExecutionContext) {

  private val productRoute: Route =
    path(JavaUUID) { id =>
      concat(
        get {
          complete {
            for {
              rows <- repo.loadProduct(id)
              prod <- Future(Product.fromDatabase(rows))
            } yield prod
          }
        },
        put {
          entity(as[Product]) { product =>
            complete {
              repo.updateProduct(product)
            }
          }
        }
      )
    }

  private val productsRoute: Route =
    pathEndOrSingleSlash {
      concat(
        get {
          implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
            EntityStreamingSupport.json()

          val src = Source.fromPublisher(repo.loadProducts())

          val products: Source[Product, NotUsed] = src
            .collect { rows =>
              Product.fromDatabase(Seq(rows)) match {
                case Some(p) => p
              }
            }.groupBy(Int.MaxValue, _.id) // create a new stream per id
            .fold(Option.empty[Product]) {
              case (optProduct, product) => optProduct match {
                case Some(p) =>
                  // merge the list of translations
                  p.copy(names = p.names ++ product.names).some
                case None => product.some
              }
            }.mergeSubstreams
            .collect {
              case Some(p) => p
            }

          complete(products)
        },
        post {
          entity(as[Product]) { product =>
            complete {
              repo.saveProduct(product)
            }
          }
        }
      )
    }

  val allRoutes: Route = concat(
    pathPrefix("product")(productRoute),
    pathPrefix("products")(productsRoute)
  )
}
