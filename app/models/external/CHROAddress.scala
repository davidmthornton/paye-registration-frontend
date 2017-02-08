/*
 * Copyright 2017 HM Revenue & Customs
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

package models.external

import models.view.Address
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

case class CHROAddress(
                        premises: String,
                        addressLine1: String,
                        addressLine2: Option[String],
                        locality: String,
                        country: Option[String],
                        poBox: Option[String],
                        postalCode: Option[String],
                        region: Option[String]
                      ) {
}

object CHROAddress {
  implicit val formatModel: Reads[CHROAddress] = (
      (__ \ "premises").read[String] and
      (__ \ "address_line_1").read[String] and
      (__ \ "address_line_2").readNullable[String] and
      (__ \ "locality").read[String] and
      (__ \ "country").readNullable[String] and
      (__ \ "po_box").readNullable[String] and
      (__ \ "postal_code").readNullable[String] and
      (__ \ "region").readNullable[String]
    )(CHROAddress.apply _)

  import scala.language.implicitConversions

  implicit def convertToAddress(address: CHROAddress): Address = Address(
    s"${address.premises} ${address.addressLine1}",
    address.locality,
    address.addressLine2,
    address.poBox,
    address.postalCode,
    address.country
  )
}