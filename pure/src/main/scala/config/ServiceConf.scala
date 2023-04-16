package config

import domain.database._
import eu.timepit.refined.types.net.PortNumber
import eu.timepit.refined.types.string.NonEmptyString

final case class ApiConf(host: NonEmptyString, port: PortNumber)

final case class DatabaseConf(
                               driver: NonEmptyString,
                               url: DatabaseUrl,
                               user: DatabaseLogin,
                               pass: DatabasePassword
                             )

final case class ServiceConf(api: ApiConf, database: DatabaseConf)



