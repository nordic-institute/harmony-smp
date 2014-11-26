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
package eu.europa.ec.cipa.validation.utils.createrules.sch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.multimap.IMultiMapListBased;
import com.helger.commons.collections.multimap.MultiHashMapArrayListBased;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.xml.serialize.XMLWriterSettings;
import com.helger.schematron.CSchematron;
import com.helger.ubl.CUBL20;

import eu.europa.ec.cipa.validation.utils.createrules.RuleSourceItem;
import eu.europa.ec.cipa.validation.utils.createrules.utils.ODFUtils;
import eu.europa.ec.cipa.validation.utils.createrules.utils.Utils;

@Immutable
public final class SchematronCreator {
  private static final class PrerequesiteCache {
    private int m_nLastID = 0;
    // Map from String to ID
    private final Map <String, String> m_aMap = new HashMap <String, String> ();

    @Nonnull
    @Nonempty
    private String _createNextPrerequisiteVarName () {
      return "Prerequisite" + (++m_nLastID);
    }

    @Nullable
    public String getPrerequisiteVarName (@Nullable final String sPrerequisite) {
      if (StringHelper.hasNoText (sPrerequisite))
        return null;

      String sID = m_aMap.get (sPrerequisite);
      if (sID == null) {
        sID = _createNextPrerequisiteVarName ();
        m_aMap.put (sPrerequisite, sID);
      }
      return sID;
    }

    @Nonnull
    @ReturnsMutableCopy
    public Map <String, String> getAllSortedByID () {
      return ContainerHelper.getSortedByValue (m_aMap);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronCreator.class);

  private static final String NS_SCHEMATRON = CSchematron.NAMESPACE_SCHEMATRON;
  private static final boolean USE_LETS = false;

  private final PrerequesiteCache m_aPrereqCache = new PrerequesiteCache ();

  // Map from transaction to Map from context to list of assertions
  final Map <String, IMultiMapListBased <String, RuleAssertion>> m_aAbstractRules = new HashMap <String, IMultiMapListBased <String, RuleAssertion>> ();

  private SchematronCreator () {}

  private void _readAbstractRules (@Nonnull final SpreadsheetDocument aSpreadSheet) {
    final Table aFirstSheet = aSpreadSheet.getSheetByIndex (0);
    int nRow = 1;
    while (!ODFUtils.isEmpty (aFirstSheet, 0, nRow)) {
      // Read ODF data of the current row
      final String sRuleID = ODFUtils.getText (aFirstSheet, 0, nRow);
      final String sMessage = ODFUtils.getText (aFirstSheet, 1, nRow);
      final String sContext = ODFUtils.getText (aFirstSheet, 2, nRow);
      final String sSeverity = ODFUtils.getText (aFirstSheet, 3, nRow);
      final String sTransaction = ODFUtils.getText (aFirstSheet, 4, nRow);
      final String sIsObsolete = ODFUtils.getText (aFirstSheet, 5, nRow);
      if (StringParser.parseBool (sIsObsolete)) {
        s_aLogger.info ("Skipping obsolete rule '" + sRuleID + "'");
      }
      else {
        // Save in nested maps
        IMultiMapListBased <String, RuleAssertion> aTransactionRules = m_aAbstractRules.get (sTransaction);
        if (aTransactionRules == null) {
          aTransactionRules = new MultiHashMapArrayListBased <String, RuleAssertion> ();
          m_aAbstractRules.put (sTransaction, aTransactionRules);
        }
        aTransactionRules.putSingle (sContext, new RuleAssertion (sRuleID, sMessage, sSeverity));
      }

      // Next row
      ++nRow;
    }

    if (m_aAbstractRules.isEmpty ())
      throw new IllegalStateException ("No abstract rules found!");
  }

  private void _writeAbstractRules (@Nonnull final RuleSourceBusinessRule aBusinessRule) {
    // Now iterate and assemble abstract Schematron
    for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aRuleEntry : m_aAbstractRules.entrySet ()) {
      final String sTransaction = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRule.getSchematronAbstractFile (sTransaction);
      Utils.log ("    Writing abstract Schematron file " +
                 aSCHFile.getName () +
                 " with " +
                 aRuleEntry.getValue ().getTotalValueCount () +
                 " rule(s)");

      // Create the XML content
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Abstract Schematron rules for " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      ePattern.setAttribute ("abstract", "true");
      ePattern.setAttribute ("id", sTransaction);

      // For all assertions for the current transaction
      for (final Map.Entry <String, List <RuleAssertion>> aPatternEntry : aRuleEntry.getValue ().entrySet ()) {
        // The element context to use
        final String sContextRef = '$' + Utils.makeID (aPatternEntry.getKey ());
        final IMicroElement eRule = ePattern.appendElement (NS_SCHEMATRON, "rule");
        eRule.setAttribute ("context", sContextRef);

        for (final RuleAssertion aRuleAssertion : aPatternEntry.getValue ()) {
          final String sTestID = aRuleAssertion.getRuleID ();
          final IMicroElement eAssert = eRule.appendElement (NS_SCHEMATRON, "assert");
          eAssert.setAttribute ("flag", aRuleAssertion.getSeverity ());
          eAssert.setAttribute ("test", "$" + sTestID);
          eAssert.appendText ("[" + sTestID + "]-" + aRuleAssertion.getMessage ());
        }
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);
    }
  }

  private static boolean _containsRuleID (@Nonnull final List <RuleParam> aRuleParams, @Nullable final String sRuleID) {
    for (final RuleParam aRuleParam : aRuleParams)
      if (aRuleParam.getRuleID ().equals (sRuleID))
        return true;
    return false;
  }

  @Nonnull
  @Nonempty
  private IMultiMapListBased <String, RuleParam> _readBindingTests (@Nonnull final Table aSheet,
                                                                    @Nonnull final String sBindingName) {
    Utils.log ("    Handling sheet for binding '" + sBindingName + "'");
    int nRow = 1;
    final IMultiMapListBased <String, RuleParam> aRules = new MultiHashMapArrayListBased <String, RuleParam> ();
    while (!ODFUtils.isEmpty (aSheet, 0, nRow)) {
      final String sTransaction = ODFUtils.getText (aSheet, 0, nRow);
      final String sRuleID = ODFUtils.getText (aSheet, 1, nRow);
      final String sTest = ODFUtils.getText (aSheet, 2, nRow);
      final String sPrerequisite = ODFUtils.getText (aSheet, 3, nRow);

      aRules.putSingle (sTransaction,
                        new RuleParam (sRuleID,
                                       sTest,
                                       sPrerequisite,
                                       m_aPrereqCache.getPrerequisiteVarName (sPrerequisite)));
      nRow++;
    }
    return aRules;
  }

  private void _extractBindingTests (@Nonnull final RuleSourceBusinessRule aBusinessRule,
                                     @Nonnull final String sBindingName,
                                     @Nonnull final IMultiMapListBased <String, RuleParam> aRules) {
    // Check if all required rules derived from the abstract rules are present
    for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aEntryTransaction : m_aAbstractRules.entrySet ()) {
      final String sTransaction = aEntryTransaction.getKey ();
      final List <RuleParam> aFoundRules = aRules.get (sTransaction);
      if (aFoundRules == null)
        throw new IllegalStateException ("Found no rules for transaction " +
                                         sTransaction +
                                         " and binding " +
                                         sBindingName);
      for (final Map.Entry <String, List <RuleAssertion>> aEntryContext : aEntryTransaction.getValue ().entrySet ()) {
        final String sContext = aEntryContext.getKey ();
        if (!_containsRuleID (aFoundRules, Utils.makeID (sContext))) {
          // Create an invalid context
          Utils.warn ("      Missing parameter for context '" + sContext + "'");
          aRules.putSingle (sTransaction, new RuleParam (sContext, "//NonExistingDummyNode"));
        }
        for (final RuleAssertion aRuleAssertion : aEntryContext.getValue ()) {
          final String sRuleID = aRuleAssertion.getRuleID ();
          if (!_containsRuleID (aFoundRules, sRuleID)) {
            // No test needed
            Utils.warn ("      Missing parameter for rule '" + sRuleID + "'");
            aRules.putSingle (sTransaction, new RuleParam (sRuleID, "./false"));
          }
        }
      }
    }

    // Now iterate rules and assemble Schematron
    for (final Map.Entry <String, List <RuleParam>> aRuleEntry : aRules.entrySet ()) {
      final String sTransaction = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction);
      Utils.log ("      Writing " +
                 sBindingName +
                 " Schematron file " +
                 aSCHFile.getName () +
                 " for transaction " +
                 sTransaction +
                 " with " +
                 aRuleEntry.getValue ().size () +
                 " test(s)");

      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Schematron tests for binding " + sBindingName + " and transaction " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      // Assign to the global pattern
      ePattern.setAttribute ("is-a", sTransaction);
      ePattern.setAttribute ("id", sBindingName.toUpperCase (Locale.US) + "-" + sTransaction);
      for (final RuleParam aRuleParam : aRuleEntry.getValue ()) {
        final IMicroElement eParam = ePattern.appendElement (NS_SCHEMATRON, "param");
        eParam.setAttribute ("name", aRuleParam.getRuleID ());
        if (USE_LETS)
          eParam.setAttribute ("value", aRuleParam.getTestWithPrerequisiteParameter ());
        else
          eParam.setAttribute ("value", aRuleParam.getTestWithPrerequisiteInline ());
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);
    }
  }

  private void _createAssemblyFiles (@Nonnull final RuleSourceBusinessRule aBusinessRule,
                                     @Nonnull final SpreadsheetDocument aSpreadSheet) {
    // Create assembled Schematron
    Utils.log ("    Creating assembly Schematron file(s)");

    // Last sheet
    final Table aLastSheet = aSpreadSheet.getSheetByIndex (aSpreadSheet.getSheetCount () - 1);
    int nRow = 1;
    // cell 0 (profile) is optional!
    while (!ODFUtils.isEmpty (aLastSheet, 1, nRow)) {
      final String sProfile = ODFUtils.getText (aLastSheet, 0, nRow);
      if (StringHelper.hasText (sProfile))
        throw new IllegalStateException ("Profile currently not supported! Found '" + sProfile + "'");
      final String sTransaction = ODFUtils.getText (aLastSheet, 1, nRow);
      final String sBindingName = ODFUtils.getText (aLastSheet, 2, nRow);
      final String sNamespace = ODFUtils.getText (aLastSheet, 3, nRow);

      final File aSCHFile = aBusinessRule.getSchematronAssemblyFile (sBindingName, sTransaction);
      Utils.log ("      Writing " + sBindingName + " Schematron assembly file " + aSCHFile.getName ());

      final String sBindingPrefix = sBindingName.toLowerCase (Locale.US);
      final String sBindingUC = sBindingName.toUpperCase (Locale.US);

      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Schematron assembly for binding " + sBindingName + " and transaction " + sTransaction);
      final IMicroElement eSchema = aDoc.appendElement (NS_SCHEMATRON, "schema");
      eSchema.setAttribute ("queryBinding", "xslt");
      eSchema.appendElement (NS_SCHEMATRON, "title").appendText (aBusinessRule.getID () +
                                                                 " " +
                                                                 sTransaction +
                                                                 " bound to " +
                                                                 sBindingName);

      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", "cbc")
             .setAttribute ("uri", CUBL20.XML_SCHEMA_CBC_NAMESPACE_URL);
      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", "cac")
             .setAttribute ("uri", CUBL20.XML_SCHEMA_CAC_NAMESPACE_URL);
      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", sBindingPrefix)
             .setAttribute ("uri", sNamespace);

      if (USE_LETS) {
        // Print all global lets
        for (final Map.Entry <String, String> aEntry : m_aPrereqCache.getAllSortedByID ().entrySet ()) {
          final IMicroElement eLet = eSchema.appendElement (NS_SCHEMATRON, "let");
          eLet.setAttribute ("name", aEntry.getValue ());
          eLet.setAttribute ("value", aEntry.getKey ());
        }
      }

      // Phases
      IMicroElement ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
      ePhase.setAttribute ("id", aBusinessRule.getID () + "_" + sTransaction + "_phase");
      ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", sBindingUC + "-" + sTransaction);
      if (aBusinessRule.hasCodeList ()) {
        // Codelist phase
        ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
        ePhase.setAttribute ("id", "codelist_phase");
        ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", "Codes-" + sTransaction);
      }

      // Includes
      IMicroElement eInclude;
      eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", "include/" + aBusinessRule.getSchematronAbstractFile (sTransaction).getName ());
      if (aBusinessRule.hasCodeList ()) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", "include/" + aBusinessRule.getSchematronCodeListFile ().getName ());
      }
      eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", "include/" +
                                     aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction).getName ());

      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write file " + aSCHFile);

      // Remember file for XSLT creation
      aBusinessRule.addResultSchematronFile (aSCHFile);

      ++nRow;
    }
  }

  public static void createSchematrons (@Nonnull final List <RuleSourceItem> aRuleSourceItems) throws Exception {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Creating Schematron files for " + aRuleSourceItem.getID ());

      // Process all business rule files
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRules ()) {
        // Read ODS file
        Utils.log ("  Reading business rule source file " + aBusinessRule.getSourceFile ());
        final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aBusinessRule.getSourceFile ());

        final SchematronCreator aSC = new SchematronCreator ();

        // Read the binding files first, to fill the prerequisite cache
        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          final String sBindingName = aSheet.getTableName ();
          final IMultiMapListBased <String, RuleParam> aRules = aSC._readBindingTests (aSheet, sBindingName);
          aSC._extractBindingTests (aBusinessRule, sBindingName, aRules);
        }

        // Read abstract rules
        aSC._readAbstractRules (aSpreadSheet);
        aSC._writeAbstractRules (aBusinessRule);

        aSC._createAssemblyFiles (aBusinessRule, aSpreadSheet);
      }
    }
  }
}
