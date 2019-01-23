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

package uk.gov.hmrc.transitmovementapi.repositories

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Format
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.transitmovementapi.errorhandler.CrossingNotFoundException
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingId, CrossingSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.Crossing
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.transitmovementapi.models.types._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrossingRepository @Inject()(mongo: ReactiveMongoComponent)(implicit executionContext: ExecutionContext)
  extends ReactiveRepository[Crossing, String](
    collectionName = "crossing",
    mongo = mongo.mongoConnector.db,
    domainFormat = Crossing.format,
    implicitly[Format[String]]
  ) {

  def create(crossing: Crossing): Future[CrossingId] = {
    insert(crossing).map(_ => CrossingId(crossing.crossingId))
  }

  def get(crossingId: String): Future[Crossing] = {
    findById(id = crossingId).map(_.getOrElse(throw CrossingNotFoundException(s"Crossing with ID $crossingId was not found")))
  }

  def getCrossing(crossingSubmission: CrossingSubmission): Future[Option[Crossing]] = {
    find("departureDateTime" -> crossingSubmission.departureDateTime,
      "departurePort" -> crossingSubmission.departurePort,
      "destinationPort" -> crossingSubmission.destinationPort,
      "duration" -> crossingSubmission.duration,
      "carrier" -> crossingSubmission.carrier).map(_.headOption)
  }

  def clear(): Future[Boolean] = removeAll().map(_.ok)

}
