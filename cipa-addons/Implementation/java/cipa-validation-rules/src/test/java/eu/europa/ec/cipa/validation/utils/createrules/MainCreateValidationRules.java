/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.validation.utils.createrules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.helger.commons.GlobalDebug;

import eu.europa.ec.cipa.validation.utils.createrules.codelist.CodeListCreator;
import eu.europa.ec.cipa.validation.utils.createrules.codelist.RuleSourceCodeList;
import eu.europa.ec.cipa.validation.utils.createrules.sch.SchematronCreator;
import eu.europa.ec.cipa.validation.utils.createrules.sch.XSLTCreator;
import eu.europa.ec.cipa.validation.utils.createrules.utils.Utils;

public final class MainCreateValidationRules {
  public static void main (final String [] args) throws Exception {
    if (false)
      GlobalDebug.setDebugModeDirect (true);

    // Base directory for source rules
    final File aRuleSource = new File ("src/test/resources/rule-source");
    final File aRuleTarget = new File ("src/main/resources/rules");

    // Add all base directories
    final List <RuleSourceItem> aRuleSourceItems = new ArrayList <RuleSourceItem> ();
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "atgov").addBussinessRule ("businessrules/atgov-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "atnat").addBussinessRule ("businessrules/atnat-T10-BusinessRules-v02.ods"));
    // XSLT creation of biicore takes forever (approx. 25-30 minutes)!!!
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "biicore").addBussinessRule ("businessrules/biicore-T01-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T10-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T14-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "biiprofiles").addBussinessRule ("businessrules/biiprofiles-T10-BusinessRules-v01.ods")
                                                                                      .addBussinessRule ("businessrules/biiprofiles-T14-BusinessRules-v01.ods")
                                                                                      .addBussinessRule ("businessrules/biiprofiles-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "biirules").addCodeList ("businessrules/biirules-CodeLists-v01.ods")
                                                                                   .addBussinessRule ("businessrules/biirules-T01-BusinessRules-v02.ods",
                                                                                                      "T01")
                                                                                   .addBussinessRule ("businessrules/biirules-T02-BusinessRules-v01.ods")
                                                                                   .addBussinessRule ("businessrules/biirules-T03-BusinessRules-v01.ods")
                                                                                   .addBussinessRule ("businessrules/biirules-T10-BusinessRules-v02.ods",
                                                                                                      "T10")
                                                                                   .addBussinessRule ("businessrules/biirules-T14-BusinessRules-v01.ods",
                                                                                                      "T14")
                                                                                   .addBussinessRule ("businessrules/biirules-T15-BusinessRules-v01.ods",
                                                                                                      "T15"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "dknat").addBussinessRule ("businessrules/dknat-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "eugen").addCodeList ("businessrules/eugen-CodeLists-v01.ods")
                                                                                .addBussinessRule ("businessrules/eugen-T01-BusinessRules-v02.ods")
                                                                                .addBussinessRule ("businessrules/eugen-T10-BusinessRules-v01.ods",
                                                                                                   "T10")
                                                                                .addBussinessRule ("businessrules/eugen-T14-BusinessRules-v01.ods",
                                                                                                   "T14")
                                                                                .addBussinessRule ("businessrules/eugen-T15-BusinessRules-v01.ods",
                                                                                                   "T15")
                                                                                .addBussinessRule ("businessrules/eugen-T19-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "itnat").addBussinessRule ("businessrules/itnat-T10-BusinessRules-v03.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "nogov").addBussinessRule ("businessrules/nogov-T10-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/nogov-T14-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/nogov-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (aRuleSource, aRuleTarget, "nonat").addCodeList ("businessrules/nonat-T17-CodeLists-v01.ods")
                                                                                .addBussinessRule ("businessrules/nonat-T10-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/nonat-T14-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/nonat-T15-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/nonat-T17-BusinessRules-v01.ods",
                                                                                                   "T17"));

    // Create all codelists (GC + CVA)
    // Processing time: quite quick
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      // Process all code lists
      for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ())
        new CodeListCreator ().createCodeLists (aCodeList);
    }

    if (true) {
      // Create Schematron
      // Processing time: quite OK
      SchematronCreator.createSchematrons (aRuleSourceItems);

      // Now create the validation XSLTs
      // Processing time: terribly slow for biicore
      XSLTCreator.createXSLTs (aRuleSourceItems);
    }

    Utils.log ("Finished building validation rules");
    Utils.log ("Now run 'mvn license:format' on the commandline to add all the file headers!");
  }
}
