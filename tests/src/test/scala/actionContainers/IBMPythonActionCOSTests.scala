/*
 * Copyright 2023 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package actionContainers

import common.{TestHelpers, WskActorSystem, WskProps, WskTestHelpers}
import common.rest.WskRestOperations
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io.File
import spray.json._

@RunWith(classOf[JUnitRunner])
class IBMPythonActionCOSTests extends TestHelpers with WskTestHelpers with WskActorSystem {

  lazy val defaultKind = Some("python:3.6")

  implicit val wskprops: WskProps = WskProps()
  val wsk = new WskRestOperations
  val datdir = "tests/dat/cos/"
  val actionName = "testCOSSDK"
  val actionFileName = "testCOSSDK.py"

  it should "Test whether or not COS package is accessible within a python action" in withAssetCleaner(wskprops) {
    (wp, assetHelper) =>
      val file = Some(new File(datdir, actionFileName).toString())

      assetHelper.withCleaner(wsk.action, actionName) { (action, _) =>
        action.create(actionName, file, main = Some("main"), kind = defaultKind)
      }

      withActivation(wsk.activation, wsk.action.invoke(actionName)) { activation =>
        val response = activation.response
        response.result.get.fields.get("error") shouldBe empty
        response.result.get.fields.get("message") should be(Some(JsString("ibm_boto3 loaded")))
        response.result.get.fields.get("meta-model") should be(Some(JsString("s3")))

      }

  }

}
