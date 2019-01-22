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
import reactivemongo.core.errors.ConnectionException
import uk.gov.hmrc.transitmovementapi.models.api.CrossingId
import uk.gov.hmrc.transitmovementapi.repositories.CrossingRepository
import uk.gov.hmrc.transitmovementapi.utils.DataSetupSpec
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataTransformer}

import scala.concurrent.Future


class CrossingServiceSpec extends DataSetupSpec with DataGenerator with DataTransformer {

  val mockCrossingRepository: CrossingRepository = mock[CrossingRepository]
  val service: CrossingService = new CrossingService(mockCrossingRepository)

  "create" should {
    "return the crossing ID if the crossing creation is successful" in {
      withCrossing{
        crossing =>
          when(mockCrossingRepository.getCrossing(any())).thenReturn(Future.successful(Some(crossing)))
          when(mockCrossingRepository.create(any())).thenReturn(Future.successful(CrossingId.fromCrossing(crossing)))

          val result = await(service.submitCrossing(toCrossingSubmission(crossing)))

          result shouldBe CrossingId.fromCrossing(crossing)
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

  "getAllCrossings" should {
    "return all crossings" in {
      withCrossing {
        crossing => {
          when(mockCrossingRepository.getAll()).thenReturn(Future.successful(List(crossing)))

          val result = await(service.getAllCrossings())

          result shouldBe List(crossing)
        }
      }
    }

    "pipeline the exception from the repository" in {
      withNoSetup {
        when(mockCrossingRepository.getAll()).thenReturn(Future.failed(ConnectionException("Primary node not found")))

        intercept[ConnectionException] {
          await(service.getAllCrossings())
        }
      }
    }
  }

  "getCrossing" should {
    "return a single crossing" in {
      withCrossing {
        crossing =>
          when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))

          val result = await(service.getCrossing(crossing.crossingId))

          result shouldBe crossing
      }
    }

    "pipeline the exception from the repository" in {
      withCrossing {
        crossing =>
          when(mockCrossingRepository.get(crossing.crossingId)).thenReturn(Future.failed(ConnectionException("Primary node not found")))

          intercept[ConnectionException] {
            await(service.getCrossing(crossing.crossingId))
          }
      }
    }
  }

}