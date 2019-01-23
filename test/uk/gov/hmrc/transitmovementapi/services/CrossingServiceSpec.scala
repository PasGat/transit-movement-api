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
import uk.gov.hmrc.transitmovementapi.models.api.CrossingId
import uk.gov.hmrc.transitmovementapi.repositories.CrossingRepository
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataSetupSpec, DataTransformer}

import scala.concurrent.Future


class CrossingServiceSpec extends DataSetupSpec with DataGenerator with DataTransformer {

  val mockCrossingRepository: CrossingRepository = mock[CrossingRepository]
  val service: CrossingService = new CrossingService(mockCrossingRepository)

  "create" should {
    "return the crossing ID if the crossing creation is successful" in {
      withCrossing {
        crossing =>
          when(mockCrossingRepository.getCrossing(any())).thenReturn(Future.successful(Some(crossing)))
          when(mockCrossingRepository.create(any())).thenReturn(Future.successful(CrossingId(crossing.crossingId)))

          val result = await(service.submitCrossing(toCrossingSubmission(crossing)))

          result shouldBe CrossingId(crossing.crossingId)
      }
    }

    "throw an InternalServerException if the crossing creation fails" in {
      withCrossing {
        crossing => {
          when(mockCrossingRepository.getCrossing(any())).thenReturn(Future.successful(None))
          when(mockCrossingRepository.create(any())).thenReturn(Future.failed(new InternalServerException("Failed to create crossing")))

          intercept[InternalServerException] {
            await(service.submitCrossing(toCrossingSubmission(crossing)))
          }
        }
      }
    }
  }

}