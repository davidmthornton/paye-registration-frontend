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

package forms.DirectorDetails

import models.view.{UserEnteredNino, Ninos}
import play.api.data.format.Formatter
import play.api.data.{Mapping, Forms, FormError, Form}
import play.api.data.Forms._
import utils.Validators.ninoValidation

object DirectorDetailsForm {


  implicit def userNinoFormatter: Formatter[UserEnteredNino] = new Formatter[UserEnteredNino] {

    def getIndex(key: String): String = {
      key.filter("0123456789".toSet)
    }

    def bind(key: String, data: Map[String, String]) = {
      if(getIndex(key) == "0" && data.map{case (k, v) => v}.forall( _ == "")) {
        //TODO: Add noFieldsCompleted as a val/constant/enum (used here and in OneOfManyForm/associated views)
        Left(Seq(FormError("noFieldsCompleted-nino[0]", "pages.directorDetails.errors.noneCompleted")))
      } else data.getOrElse(key,"") match {
        case ""   => Right(UserEnteredNino(getIndex(key), None))
        case nino => Right(UserEnteredNino(getIndex(key), Some(nino.toUpperCase)))
      }
    }

    def unbind(key: String, value: UserEnteredNino) = Map(key -> value.nino.getOrElse(""))
  }

  val userNino: Mapping[UserEnteredNino] = Forms.of[UserEnteredNino](userNinoFormatter)


  val form = Form(
    mapping(
      "nino" -> list(userNino.verifying(ninoValidation))
    )(Ninos.apply)(Ninos.unapply)
  )

}