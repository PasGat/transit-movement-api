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

package uk.gov.hmrc.transitmovementapi.utils

import java.time.Instant

import org.scalacheck.Gen
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.api.CrossingSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Crossing
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

trait CrossingGenerator {
  dataTransformer: DataTransformer =>

  private[utils] def getRandomCrossing: Crossing                     = crossingGenerator.sample.get
  private[utils] def getRandomCrossingSubmission: CrossingSubmission = crossingGenerator.map(c => toCrossingSubmission(c)).sample.get

  private def crossingGenerator: Gen[Crossing] = for {
    id              <- crossingIdGenerator
    departureTime   <- Gen.const(Instant.now)
    departurePort   <- departurePortGenerator
    destinationPort <- destinationPortGenerator
    duration        <- Gen.choose(0, Int.MaxValue)
    carrier         <- carrierGenerator
    createdTime     <- Gen.const(Instant.now)
  } yield Crossing(id, departureTime, departurePort, destinationPort, duration, carrier, createdTime)

  private def crossingIdGenerator: Gen[String] = Gen.const(BSONObjectID.generate().stringify)

  private def departurePortGenerator: Gen[DeparturePort] =
    Gen.oneOf("Calais","Coquelles","Dublin", "Dunkirk")
      .map(port => Json.toJson(port).as[DeparturePort])

  private def destinationPortGenerator: Gen[DestinationPort] =
    Gen.oneOf("Dover", "Folkestone", "Holyhead")
    .map(port => Json.toJson(port).as[DestinationPort])

  private def carrierGenerator: Gen[Carrier] =
    Gen.oneOf( "Brittany Ferries", "DFDS", "Eurotunnel", "P&O", "Stena Line")
    .map(c => Json.toJson(c).as[Carrier])
}