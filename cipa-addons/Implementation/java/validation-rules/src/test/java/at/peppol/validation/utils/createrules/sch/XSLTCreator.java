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
package at.peppol.validation.utils.createrules.sch;

import java.io.File;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;

import at.peppol.validation.schematron.xslt.ISchematronXSLTProvider;
import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;
import at.peppol.validation.utils.createrules.RuleSourceItem;
import at.peppol.validation.utils.createrules.utils.Utils;

import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

@Immutable
public final class XSLTCreator {
  private XSLTCreator () {}

  public static void createXSLTs (final List <RuleSourceItem> aRuleSourceItems) {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Creating XSLT files for " + aRuleSourceItem.getID ());
      // Process all business rules
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRules ())
        for (final File aSCHFile : aBusinessRule.getAllResultSchematronFiles ()) {
          Utils.log ("  Creating XSLT for " + aSCHFile.getName ());

          final ISchematronXSLTProvider aXSLTProvider = SchematronResourceSCHCache.createSchematronXSLTProvider (new FileSystemResource (aSCHFile));
          if (aXSLTProvider == null) {
            // Error message already emitted!
            continue;
          }
          final Document aXSLTDoc = aXSLTProvider.getXSLTDocument ();

          final File aXSLTFile = new File (FilenameHelper.getWithoutExtension (aSCHFile.getPath ()) + ".xslt");
          if (SimpleFileIO.writeFile (aXSLTFile,
                                      XMLWriter.getXMLString (aXSLTDoc),
                                      XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ).isFailure ())
            throw new IllegalStateException ("Failed to write " + aXSLTFile);
        }
    }
  }

}
