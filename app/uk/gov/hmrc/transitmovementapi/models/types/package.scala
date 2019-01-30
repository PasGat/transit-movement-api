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

package uk.gov.hmrc.transitmovementapi.models

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads, Writes}

package object types {

  implicit def refinedReads[T, P](implicit reads: Reads[T], validate: Validate[T, P]): Reads[T Refined P] =
    Reads[T Refined P] { json =>
      reads.reads(json)
        .flatMap { t: T =>
          refineV[P](t) match {
            case Left(error) => JsError(error)
            case Right(value) => JsSuccess(value)
          }
        }
    }

  implicit def refinedWrites[T, P](implicit writes: Writes[T]): Writes[T Refined P] =
    writes.contramap(_.value)
}
