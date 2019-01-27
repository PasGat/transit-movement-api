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
import uk.gov.hmrc.transitmovementapi.models.api.CrossingSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Crossing
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

trait CrossingGenerator {
  dataTransformer: DataTransformer =>

  private[helpers] def getRandomCrossing: Crossing                     = crossingGenerator.sample.get
  private[helpers] def getRandomCrossingSubmission: CrossingSubmission = crossingGenerator.map(c => toCrossingSubmission(c)).sample.get

  private def crossingGenerator: Gen[Crossing] = for {
    id              <- crossingIdGenerator
    departureTime   <- Gen.const(Instant.now)
    departurePort   <- departurePortGenerator
    destinationPort <- destinationPortGenerator
    duration        <- Gen.choose(0, Int.MaxValue).map(d => Json.toJson(d).as[Duration])
    carrier         <- carrierGenerator
    createdTime     <- Gen.const(Instant.now)
    captureDateTime <- Gen.const(Instant.now)
  } yield Crossing(id, departureTime, departurePort, destinationPort, duration, carrier, createdTime, captureDateTime)

  private def crossingIdGenerator: Gen[String] = Gen.const(BSONObjectID.generate().stringify)

  private def departurePortGenerator: Gen[DeparturePort] =
    Gen.oneOf[DeparturePort]("Calais","Coquelles","Dublin", "Dunkirk")

  private def destinationPortGenerator: Gen[DestinationPort] =
    Gen.oneOf[DestinationPort]("Dover", "Folkestone", "Holyhead")

  private def carrierGenerator: Gen[Carrier] =
    Gen.oneOf[Carrier]( "Irish Ferries", "DFDS", "Eurotunnel", "P&O", "Stena Line")

}
