package com.struong.pure.db

import cats.effect.Sync
import com.struong.pure.domain.database._
import org.flywaydb.core.Flyway

trait DatabaseMigrator[F[_]] {
  def migrate(
      url: DatabaseUrl,
      user: DatabaseLogin,
      pass: DatabasePassword
  ): F[Boolean]
}

class FlywayDatabaseMigrator[F[_]](implicit F: Sync[F]) extends DatabaseMigrator[F] {
  override def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): F[Boolean] = {
    F.delay {
      val flyway: Flyway = Flyway.configure()
        .dataSource(url.value, user.value, pass.value)
        .load()

      flyway.migrate().success
    }
  }
}
