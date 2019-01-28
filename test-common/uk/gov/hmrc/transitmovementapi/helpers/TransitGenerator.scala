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

package uk.gov.hmrc.transitmovementapi.helpers

import java.time.Instant
import eu.timepit.refined.auto._
import eu.timepit.refined._
import org.scalacheck.Gen
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Transit
import wolfendale.scalacheck.regexp.RegexpGen
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

trait TransitGenerator {
  dataTransformer: DataTransformer =>

  private[helpers] def getRandomTransit(withDefaultCrossingId: Option[String] = None): Transit = transitGenerator(withDefaultCrossingId).sample.get

  private[helpers] def getRandomMetadata: TransitMetadata = transitMetadataGenerator.sample.get

  private[helpers] def getRandomTransitSubmission(withDefaultCrossingId: Option[String] = None): TransitSubmission =
    transitSubmissionGenerator(withDefaultCrossingId).sample.get

  private def transitGenerator(withDefaultCrossingId: Option[String]): Gen[Transit] = {
    for {
      id                 <- transitIdGenerator
      mrn                <- movementReferenceNumberGenerator
      vrn                <- vehicleReferenceNumberGenerator
      crossingId         <- crossingIdGenerator(withDefaultCrossingId)
      creationDate       <- Gen.const(Instant.now)
      mrnCaptureMethod   <- captureMethodGenerator
      mrnCaptureDateTime <- Gen.const(Instant.now)
    } yield Transit(id, mrn, vrn, crossingId, creationDate, mrnCaptureMethod, mrnCaptureDateTime)
  }

  private def transitSubmissionGenerator(withDefaultCrossingId: Option[String] = None): Gen[TransitSubmission] = for {
    transit  <- transitGenerator(withDefaultCrossingId)
    metadata <- transitMetadataGenerator
  } yield toTransitSubmission(transit, metadata)

  private def transitMetadataGenerator: Gen[TransitMetadata] = for {
    userId   <- Gen.const(BSONObjectID.generate().stringify)
    deviceId <- Gen.const(BSONObjectID.generate().stringify)
  } yield TransitMetadata(userId, deviceId)

  private def transitIdGenerator: Gen[String] = Gen.const(BSONObjectID.generate().stringify)

  private def movementReferenceNumberGenerator: Gen[MovementReferenceNumber] =
    RegexpGen.from("\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}").map(mrn => Json.toJson(mrn).as[MovementReferenceNumber])

  private def vehicleReferenceNumberGenerator: Gen[Option[VehicleReferenceNumber]] =
    Gen.option(RegexpGen.from(s"""([A-Z0-9]|[\\s])+""").map(vrn => Json.toJson(vrn).as[VehicleReferenceNumber]))

  private def crossingIdGenerator(withDefaultCrossingId: Option[String]): Gen[String] = {
    Gen.const(withDefaultCrossingId.fold(BSONObjectID.generate().stringify)(id => id))
  }

  private def captureMethodGenerator: Gen[MrnCaptureMethod] = {
    Gen.oneOf[MrnCaptureMethod]("SCAN", "MANUAL")
  }
}
