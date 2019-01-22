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

import java.util.UUID

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import reactivemongo.core.errors.ConnectionException
import uk.gov.hmrc.transitmovementapi.errorhandler.{CrossingNotFoundException, DuplicateTransitException}
import uk.gov.hmrc.transitmovementapi.models.api.TransitId
import uk.gov.hmrc.transitmovementapi.models.data.Transit
import uk.gov.hmrc.transitmovementapi.repositories.{CrossingRepository, TransitRepository}
import uk.gov.hmrc.transitmovementapi.utils.DataSetupSpec
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataTransformer}

import scala.concurrent.Future


class TransitServiceSpec extends DataSetupSpec with DataGenerator with DataTransformer {

  val mockTransitRepository: TransitRepository = mock[TransitRepository]
  val mockCrossingRepository: CrossingRepository = mock[CrossingRepository]
  val service: TransitService = new TransitService(mockTransitRepository, mockCrossingRepository)

  "create" should {
    "return the transit ID if the transit creation is successful" in {
      withTransitAndCrossing {
        (transit, crossing) =>
          when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))
          when(mockTransitRepository.create(any())).thenReturn(Future.successful(transit))
          when(mockTransitRepository.getTransit(any())).thenReturn(Future.successful(None))

          val result = await(service.submitTransit(toTransitSubmission(transit)))

          result shouldBe TransitId.fromTransit(transit)
      }
    }

    "throw a CrossingNotFoundException if the crossing does not exist for the supplied crossing ID" in {
      withTransit {
        transit =>
          when(mockTransitRepository.create(any())).thenReturn(Future.failed(CrossingNotFoundException("Crossing does not exist")))
          when(mockTransitRepository.getTransit(any())).thenReturn(Future.successful(None))

          intercept[CrossingNotFoundException] {
            await(service.submitTransit(toTransitSubmission(transit)))
          }
      }
    }

    "throw an InternalServerException if the transit creation fails" in {
      withTransit {
        transit =>
          when(mockTransitRepository.create(any())).thenReturn(Future.failed(new InternalServerException("Failed to create transit")))
          when(mockTransitRepository.getTransit(any())).thenReturn(Future.successful(None))

          intercept[InternalServerException] {
            await(service.submitTransit(toTransitSubmission(transit)))
          }
      }
    }

    "throw a DuplicateTransitException if the transit already exists" in {
      withTransitAndCrossing {
        (transit, crossing) =>

          when(mockTransitRepository.create(any())).thenReturn(Future.failed(DuplicateTransitException("Transit already exists")))
          when(mockCrossingRepository.get(any())).thenReturn(Future.successful(crossing))

          intercept[DuplicateTransitException] {
            await(service.submitTransit(toTransitSubmission(transit)))
          }
      }
    }
  }

  "getByCrossingId" should {
    "return all transits associated with an existing crossing ID" in {
      withTransitAndCrossing{
        (transit, crossing) =>
          when(mockTransitRepository.getByCrossingId(eqTo(crossing.crossingId))).thenReturn(Future.successful(List(transit)))

          val result = await(service.getByCrossingId(crossing.crossingId))
          result shouldBe List(transit)
      }
    }

    "return empty list for a crossing ID that does not exist" in {
      withNoSetup {
        when(mockTransitRepository.getByCrossingId(any())).thenReturn(Future.successful(List.empty[Transit]))

        val result = await(service.getByCrossingId(UUID.randomUUID().toString))
        result shouldBe List.empty[Transit]
      }
    }

    "pipeline the exception from the repository" in {
      withCrossing{
        crossing =>
          when(mockTransitRepository.getByCrossingId(any())).thenReturn(Future.failed(ConnectionException("Primary node not found")))

          intercept[ConnectionException] {
            await(service.getByCrossingId(crossing.crossingId))
          }
      }
    }
  }

}