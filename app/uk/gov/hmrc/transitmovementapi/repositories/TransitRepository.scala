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
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.errorhandler.DuplicateTransitException
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Transit
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.transitmovementapi.models.types._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransitRepository @Inject()(mongo: ReactiveMongoComponent)(implicit executionContext: ExecutionContext)
  extends ReactiveRepository[Transit, BSONObjectID](
    collectionName = "transit",
    mongo = mongo.mongoConnector.db,
    domainFormat = Transit.format
  ) {

  def create(transit: TransitSubmission): Future[Transit] = {
    def isNewTransit: Future[Unit] = {
      this.getTransit(transit) flatMap {
        case None    => Future.successful(())
        case Some(_) => Future.failed(DuplicateTransitException("Transit already exists!"))
      }
    }

    lazy val record = Transit.fromTransitSubmission(transit)

    for {
      _ <- isNewTransit
      _ <- insert(record)
    } yield record
  }

  def getByCrossingId(crossingId: String): Future[List[Transit]] = find("crossingId" -> crossingId)

  def getTransit(submission: TransitSubmission): Future[Option[Transit]] = find(
    "crossingId" -> submission.crossingId,
    "movementReferenceNumber" -> submission.movementReferenceNumber,
    "vehicleReferenceNumber" -> submission.vehicleReferenceNumber
  ).map(_.headOption)

  def clear(): Future[Boolean] = removeAll().map(_.ok)
}
