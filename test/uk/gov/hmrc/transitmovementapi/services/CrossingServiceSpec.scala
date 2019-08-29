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
import uk.gov.hmrc.transitmovementapi.connectors.OfficeOfTransitCompletionConnector
import uk.gov.hmrc.transitmovementapi.helpers.{BaseSpec, DataGenerator}

import scala.concurrent.Future

class CrossingServiceSpec extends BaseSpec with DataGenerator {

  val mockAuditConnector:         AuditConnector                     = mock[AuditConnector]
  val mockOotCompletionConnector: OfficeOfTransitCompletionConnector = mock[OfficeOfTransitCompletionConnector]

  val service: CrossingService = new CrossingService(mockOotCompletionConnector, mockAuditConnector)

  "create" should {
    "return () if there were no errors when attempting to store the submitted crossing data" in {
      withCrossing { crossing =>
        when(mockOotCompletionConnector.postCrossing(any())(any())).thenReturn(Future.successful(()))
        when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Success))

        val result: Unit = await(service.submitCrossing(crossing))

        result shouldBe ()
      }
    }

    "throw an InternalServerException if any errors occurred when attempting to store the submitted crossing data" in {
      withCrossing { crossing =>
        when(mockOotCompletionConnector.postCrossing(any())(any())).thenReturn(Future.failed(new InternalServerException("Internal server error")))
        when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Success))

        intercept[InternalServerException] {
          await(service.submitCrossing(crossing))
        }
      }
    }
  }
}
