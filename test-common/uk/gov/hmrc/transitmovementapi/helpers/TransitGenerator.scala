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
import org.scalacheck.Gen
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingDetails, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._
import wolfendale.scalacheck.regexp.RegexpGen

trait TransitGenerator {
  case class TransitSubmissionWithId(id: String, submission: TransitSubmission)

  private[helpers] def getRandomTransitSubmission: TransitSubmissionWithId =
    transitSubmissionWithIdGenerator().sample.get

  private def transitSubmissionGenerator(): Gen[TransitSubmission] = {
    for {
      mrn                <- movementReferenceNumberGenerator
      vrn                <- vehicleReferenceNumberGenerator
      transitMetadata    <- transitMetadataGenerator()
      crossingDetails    <- crossingGenerator()
    } yield TransitSubmission(mrn, vrn, transitMetadata, crossingDetails)
  }

  private def transitMetadataGenerator(): Gen[TransitMetadata] = {
    for {
      userId             <- Gen.const(BSONObjectID.generate().stringify)
      deviceId           <- Gen.const(BSONObjectID.generate().stringify)
      captureMethod      <- captureMethodGenerator
      captureDateTime    <- Gen.const(Instant.now)
    } yield TransitMetadata(userId, deviceId, captureMethod, captureDateTime)
  }

  private def crossingGenerator(): Gen[CrossingDetails] = {
    for {
      departureDateTime  <- Gen.const(Instant.now)
      departurePort      <- departurePortGenerator
      destinationPort    <- destinationPortGenerator
      duration           <- Gen.choose(0, 1000).map(i => Json.toJson(i).as[Duration])
      carrier            <- carrierGenerator
      vesselName         <- vesselNameGenerator
    } yield CrossingDetails(carrier, vesselName, departurePort, destinationPort, departureDateTime, duration)
  }

  private def transitSubmissionWithIdGenerator(): Gen[TransitSubmissionWithId] = for {
    transit  <- transitSubmissionGenerator()
    id       <- Gen.const(BSONObjectID.generate().stringify)
  } yield TransitSubmissionWithId(id, transit)

  private def vesselNameGenerator: Gen[Option[String]] =
    Gen.option(RegexpGen.from(s"""[A-Za-z0-9]+"""))

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

  private def departurePortGenerator: Gen[DeparturePort] =
    Gen.oneOf[DeparturePort]("Calais", "Coquelles", "Dublin", "Dunkirk")

  private def destinationPortGenerator: Gen[DestinationPort] =
    Gen.oneOf[DestinationPort]("Dover", "Folkestone", "Holyhead")

  private def carrierGenerator: Gen[Carrier] =
    Gen.oneOf[Carrier]("Irish Ferries","DFDS","Eurotunnel", "P&O", "Stena Line")
}
