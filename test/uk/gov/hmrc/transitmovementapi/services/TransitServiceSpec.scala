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
import uk.gov.hmrc.transitmovementapi.errorhandler.CrossingNotFoundException
import uk.gov.hmrc.transitmovementapi.repositories.{CrossingRepository, TransitRepository}
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataSetupSpec, DataTransformer}

import scala.concurrent.Future


class TransitServiceSpec extends DataSetupSpec with DataGenerator with DataTransformer {

  val mockTransitRepository: TransitRepository = mock[TransitRepository]
  val mockCrossingRepository: CrossingRepository = mock[CrossingRepository]
  val service: TransitService = new TransitService(mockTransitRepository, mockCrossingRepository)

  "create" should {
    "return () if there were no errors when attempting to store the submitted transit data" in {
      withTransitAndCrossing {
        (transit, crossing) =>
          when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))
          when(mockTransitRepository.create(any())).thenReturn(Future.successful(()))

          val result = await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit))))

          result shouldBe (())
      }
    }

    "throw a CrossingNotFoundException if the crossing does not exist for the supplied crossing ID" in {
      withTransit {
        transit =>
          when(mockCrossingRepository.get(any())).thenReturn(Future.failed(CrossingNotFoundException("Crossing does not exist")))

          intercept[CrossingNotFoundException] {
            await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit))))
          }
      }
    }

    "throw an InternalServerException if any errors occurred when attempting to store the submitted transit data" in {
      withTransitAndCrossing {
        (transit, crossing) =>
          when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))
          when(mockTransitRepository.create(any())).thenReturn(Future.failed(new InternalServerException("Failed to create transit")))

          intercept[InternalServerException] {
            await(service.submitTransits("test-crossing-id", List(toTransitSubmission(transit))))
          }
      }
    }
  }

}