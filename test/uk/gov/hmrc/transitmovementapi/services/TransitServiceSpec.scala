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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.transitmovementapi.errorhandler.CrossingNotFoundException
import uk.gov.hmrc.transitmovementapi.repositories.{CrossingRepository, TransitRepository}
import uk.gov.hmrc.transitmovementapi.helpers.{DataGenerator, BaseSpec, DataTransformer}

import scala.concurrent.Future

class TransitServiceSpec extends BaseSpec with DataGenerator with DataTransformer {

  val mockTransitRepository: TransitRepository = mock[TransitRepository]
  val mockCrossingRepository: CrossingRepository = mock[CrossingRepository]
  val mockAuditConnector: AuditConnector = mock[AuditConnector]

  val service: TransitService = new TransitService(mockTransitRepository, mockCrossingRepository, mockAuditConnector)

  "create" should {
    "return () if there were no errors when attempting to store the submitted transit data" in {
      withTransitAndCrossing {
        (transit, crossing) =>
          withTransitMetadata {
            transitMetadata =>
              when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))
              when(mockTransitRepository.create(any())).thenReturn(Future.successful(()))
              when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Success))
              val result: Unit = await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit, transitMetadata))))

              result shouldBe ()
          }
      }
    }

    "throw a CrossingNotFoundException if the crossing does not exist for the supplied crossing ID" in {
      withTransit {
        transit =>
          withTransitMetadata {
            transitMetadata =>
              when(mockCrossingRepository.get(any())).thenReturn(Future.failed(CrossingNotFoundException("Crossing does not exist")))

              intercept[CrossingNotFoundException] {
                await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit, transitMetadata))))
              }
          }
      }
    }

    "throw an InternalServerException if any errors occurred when attempting to store the submitted transit data" in {
      withTransitAndCrossing {
        (transit, crossing) =>
          withTransitMetadata {
            transitMetadata =>
              when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))
              when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Success))
              when(mockTransitRepository.create(any())).thenReturn(Future.failed(new InternalServerException("Failed to create transit")))

              intercept[InternalServerException] {
                await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit, transitMetadata))))
              }
          }
      }
    }
  }

}