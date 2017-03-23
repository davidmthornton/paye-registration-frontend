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

package controllers.errors

import javax.inject.{Inject, Singleton}

import auth.PAYERegime
import config.FrontendAuthConnector
import connectors.{KeystoreConnect, KeystoreConnector}
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.frontend.auth.Actions
import uk.gov.hmrc.play.frontend.controller.FrontendController
import utils.SessionProfile

import scala.concurrent.Future

@Singleton
class ErrorController @Inject()(injMessagesApi: MessagesApi,
                                injKeystoreConnector: KeystoreConnector)
                                extends ErrorCtrl{
  val authConnector = FrontendAuthConnector
  val messagesApi = injMessagesApi
  val keystoreConnector = injKeystoreConnector
}

trait ErrorCtrl extends FrontendController with Actions with I18nSupport with SessionProfile {

  val keystoreConnector : KeystoreConnect

  val ineligible = AuthorisedFor(taxRegime = new PAYERegime, pageVisibility = GGConfidence).async {
    implicit user => implicit request =>
    withCurrentProfile { _ =>
      Future.successful(Ok(views.html.pages.error.ineligible()))
    }
  }

}