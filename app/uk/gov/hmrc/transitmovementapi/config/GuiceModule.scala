/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.transitmovementapi.config

import java.net.URL

import com.google.inject.binder.ScopedBindingBuilder
import com.google.inject.name.Names.named
import com.google.inject.{AbstractModule, Provider, TypeLiteral}
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.play.bootstrap.auth.DefaultAuthConnector
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.play.bootstrap.http.{DefaultHttpClient, HttpClient}

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  val servicesConfig = new ServicesConfig(
    configuration,
    new RunMode(configuration, environment.mode)
  )

  protected lazy val mode: Mode = environment.mode
  protected lazy val runModeConfiguration: Configuration = configuration

  override def configure(): Unit = {

    bind(classOf[AuthConnector]).to(classOf[DefaultAuthConnector])
    bind(classOf[HttpClient]).to(classOf[DefaultHttpClient])

    bindConfigBaseUrl("common-transit-convention")
    bindConfigString("appUrl")
    bindConfigStringSeq("api.access.white-list.applicationIds")
  }

  private def bindConfigStringSeq(path: String): Unit = {
    val configValue: Seq[String] = configuration.getStringSeq(path).getOrElse(throw new RuntimeException(s"""Config property "$path" missing"""))
    bind(new TypeLiteral[Seq[String]] {})
      .annotatedWith(named(path))
      .toInstance(configValue)
  }

  private def bindConfigString(path: String): Unit = {
    bindConstant().annotatedWith(named(path))
      .to(configuration.underlying.getString(path))
  }

  private def bindConfigBaseUrl(serviceName: String): ScopedBindingBuilder =
    bind(classOf[URL])
      .annotatedWith(named(s"$serviceName.baseUrl"))
      .toProvider(new BaseUrlProvider(serviceName))

  private class BaseUrlProvider(serviceName: String) extends Provider[URL] {
    override lazy val get = new URL(servicesConfig.baseUrl(serviceName))
  }

}
