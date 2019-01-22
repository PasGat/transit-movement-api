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

package uk.gov.hmrc.transitmovementapi.models.types

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not, Or}
import eu.timepit.refined.char.{Digit, UpperCase, Whitespace}
import eu.timepit.refined.collection.{Empty, Forall}
import eu.timepit.refined.string.MatchesRegex

object ModelTypes {
  // since there are no hard-defined implicit values for Refined's special types,
  // a custom one had to be created
  // in order to be able to serialize/deserialize these types, use the import in the `types` package object

  type ID                      = String
  type MovementReferenceNumber = String Refined ValidMovementReferenceNumber
  type VehicleReferenceNumber  = String Refined ValidVehicleReferenceNumber
  type DeparturePort           = String Refined ValidDeparturePort
  type DestinationPort         = String Refined ValidDestinationPort
  type Carrier                 = String Refined ValidCarrier

  private type ValidMovementReferenceNumber = MatchesRegex[W.`"""\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}"""`.T]
  private type ValidVehicleReferenceNumber  = And[Not[Empty],Forall[Or[Whitespace, Or[UpperCase, Digit]]]]
  private type ValidDeparturePort           = MatchesRegex[W.`"""(Calais)|(Coquelles)|(Dublin)|(Dunkirk)"""`.T]
  private type ValidDestinationPort         = MatchesRegex[W.`"(Dover)|(Folkestone)|(Holyhead)"`.T]
  private type ValidCarrier                 = MatchesRegex[W.`"""(Brittany Ferries)|(DFDS)|(Eurotunnel)|(P&O)|(Stena Line)"""`.T]
}