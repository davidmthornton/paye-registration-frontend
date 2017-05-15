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

import models.Address
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class AreaOfIndustry(
                           sicCode: String,
                           description: String
                           )

object AreaOfIndustry {

  val r =
    (__ \ "sic_code").read[String] and
      (__ \ "description").read[String]

  val w =
    (__ \ "sic_code").write[String] and
      (__ \ "description").write[String]

  val apiReads: Reads[AreaOfIndustry] = (r)(AreaOfIndustry.apply _)
  val apiWrites: Writes[AreaOfIndustry] = (w)(unlift(AreaOfIndustry.unapply))

  implicit val format = Format(apiReads, apiWrites)
}


case class CoHoCompanyDetailsModel(
                                  companyName: String,
                                  roAddress: Address
                                ) {
}

object CoHoCompanyDetailsModel {

  val r =
    (__ \ "company_name").read[String] and
    (__ \ "registered_office_address").read[Address]

  val w =
    (__ \ "company_name").write[String] and
    (__ \ "registered_office_address").write[Address]

  val apiReads: Reads[CoHoCompanyDetailsModel] = (r)(CoHoCompanyDetailsModel.apply _)
  val apiWrites: Writes[CoHoCompanyDetailsModel] = (w)(unlift(CoHoCompanyDetailsModel.unapply))
  val incorpInfoReads = (
      (__ \ "company_name").read[String] and
      (__ \ "registered_office_address").read[Address](Address.incorpInfoReads)
    )(CoHoCompanyDetailsModel.apply _)

  implicit val format = Format(apiReads, apiWrites)
}
