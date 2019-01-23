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

package uk.gov.hmrc.transitmovementapi.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.transitmovementapi.helpers.AuditEvents
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Transit
import uk.gov.hmrc.transitmovementapi.repositories.{CrossingRepository, TransitRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransitService @Inject()(transitRepository: TransitRepository,
                               crossingRepository: CrossingRepository,
                               val auditConnector: AuditConnector)
                              (implicit val ec: ExecutionContext) extends AuditEvents {

  def submitTransits(crossingId: String, transits: List[TransitSubmission])(implicit hc: HeaderCarrier): Future[Unit] = {
    for {
      _ <- crossingRepository.get(crossingId)
      _ <- Future.traverse(transits){ transit =>
        audit(sendTransitEvent(transit.auditData, transit.movementReferenceNumber, crossingId), s => s"Transit metadata audit event failed with $s")
        transitRepository.create(Transit.fromSubmission(crossingId, transit))
      }
    } yield ()
  }

}
