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

package controllers.userJourney

import javax.inject.{Inject, Singleton}

import auth.PAYERegime
import config.FrontendAuthConnector
import play.api.i18n.{I18nSupport, MessagesApi}
import views.html.pages.companyDetails.ConfirmROAddress
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.auth.Actions
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class RegisteredOfficeAddressController @Inject()(injMessagesApi: MessagesApi) extends RegisteredOfficeAddressCtrl{
  val authConnector = FrontendAuthConnector
  val messagesApi = injMessagesApi
}

trait RegisteredOfficeAddressCtrl extends FrontendController with Actions with I18nSupport{

  val messagesApi : MessagesApi

  val roAddress : Action[AnyContent] = AuthorisedFor(taxRegime = new PAYERegime, pageVisibility = GGConfidence).async {
    implicit user =>
      implicit request =>
        Future.successful(Ok(ConfirmROAddress("<INSERT NAME HERE>")))
  }

  val confirmRO : Action[AnyContent] = AuthorisedFor(taxRegime = new PAYERegime, pageVisibility = GGConfidence).async {
    implicit user =>
      implicit request =>
        Future.successful(Redirect(controllers.userJourney.routes.AddressLookupController.redirectToLookup()))
  }
}