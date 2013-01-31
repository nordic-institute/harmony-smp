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
package eu.europa.ec.cipa.validation.utils.createrules.codelist;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.oasis.cva.v10.Context;
import org.oasis.cva.v10.ContextValueAssociation;
import org.oasis.cva.v10.Contexts;
import org.oasis.cva.v10.Message;
import org.oasis.cva.v10.ValueList;
import org.oasis.cva.v10.ValueLists;
import org.oasis.genericode.v10.CodeListDocument;
import org.oasis.genericode.v10.Column;
import org.oasis.genericode.v10.ColumnSet;
import org.oasis.genericode.v10.Identification;
import org.oasis.genericode.v10.Row;
import org.oasis.genericode.v10.SimpleCodeList;
import org.oasis.genericode.v10.UseType;
import org.oasis.genericode.v10.Value;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.Document;

import at.peppol.validation.schematron.CSchematron;

import com.phloc.commons.collections.multimap.IMultiMapSetBased;
import com.phloc.commons.collections.multimap.MultiTreeMapTreeSetBased;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.phloc.commons.xml.transform.DefaultTransformURIResolver;
import com.phloc.commons.xml.transform.TransformSourceFactory;
import com.phloc.commons.xml.transform.XMLTransformerFactory;
import com.phloc.cva.CVA10Marshaller;
import com.phloc.genericode.Genericode10CodeListMarshaller;
import com.phloc.genericode.Genericode10Utils;

import eu.europa.ec.cipa.validation.utils.createrules.utils.ODFUtils;
import eu.europa.ec.cipa.validation.utils.createrules.utils.Utils;

@Immutable
public final class CodeListCreator {
  private static final String NS_SCHEMATRON = CSchematron.NAMESPACE_SCHEMATRON;

  private static Templates s_aCVA2SCH;
  // From transaction to CVAData
  private final Map <String, CVAData> m_aCVAs = new TreeMap <String, CVAData> ();
  // From code list name to Set<Code>
  private final IMultiMapSetBased <String, String> m_aAllCodes = new MultiTreeMapTreeSetBased <String, String> ();

  public CodeListCreator () {}

  /**
   * @param aCodeList
   *        code list information
   * @param aSpreadSheet
   *        ODS spreadsheet
   * @return A set with all required code list names, references from the CVA
   *         sheet
   */
  @Nonnull
  private Set <String> _readCVAData (@Nonnull final RuleSourceCodeList aCodeList,
                                     @Nonnull final SpreadsheetDocument aSpreadSheet) {
    final Set <String> aAllReferencedCodeListNames = new HashSet <String> ();
    final Table aCVASheet = aSpreadSheet.getSheetByName ("CVA");
    if (aCVASheet == null)
      throw new IllegalStateException ("No CVA sheet found!");

    Utils.log ("  Reading CVA data");
    int nRow = 2;
    while (!ODFUtils.isEmpty (aCVASheet, 0, nRow)) {
      final String sTransaction = ODFUtils.getText (aCVASheet, 0, nRow);
      final String sID = ODFUtils.getText (aCVASheet, 1, nRow);
      String sItem = ODFUtils.getText (aCVASheet, 2, nRow);
      final String sScope = ODFUtils.getText (aCVASheet, 3, nRow);
      final String sCodeListName = ODFUtils.getText (aCVASheet, 4, nRow);
      final String sMessage = ODFUtils.getText (aCVASheet, 5, nRow);
      final String sSeverity = ODFUtils.getText (aCVASheet, 6, nRow);

      if (StringHelper.hasText (sScope))
        sItem = sScope + "//" + sItem;

      // Save context per transaction
      CVAData aCVAData = m_aCVAs.get (sTransaction);
      if (aCVAData == null) {
        aCVAData = new CVAData (sTransaction);
        m_aCVAs.put (sTransaction, aCVAData);
      }
      aCVAData.addContext (sID, sItem, sCodeListName, sSeverity, sMessage);

      // Remember that we require a codelist
      aAllReferencedCodeListNames.add (sCodeListName);

      ++nRow;
    }

    // Start creating CVA files (for each transaction)
    for (final CVAData aCVAData : m_aCVAs.values ()) {
      final File aCVAFile = aCodeList.getCVAFile (aCVAData.getTransaction ());
      Utils.log ("    Creating " + aCVAFile.getName ());

      final org.oasis.cva.v10.ObjectFactory aFactory = new org.oasis.cva.v10.ObjectFactory ();
      final ContextValueAssociation aCVA = aFactory.createContextValueAssociation ();
      aCVA.setName (FilenameHelper.getBaseName (aCVAFile));

      // Create ValueLists
      final Map <String, ValueList> aValueListMap = new HashMap <String, ValueList> ();
      final ValueLists aValueLists = aFactory.createValueLists ();
      // Emit only the code lists, that are used in the contexts
      for (final String sCodeListName : aCVAData.getAllUsedCodeListNames ()) {
        final ValueList aValueList = aFactory.createValueList ();
        aValueList.setId (sCodeListName);
        aValueList.setUri (aCodeList.getGCFile (sCodeListName).getName ());
        aValueLists.getValueList ().add (aValueList);
        aValueListMap.put (aValueList.getId (), aValueList);
      }
      aCVA.setValueLists (aValueLists);

      // Create Contexts
      final Contexts aContexts = aFactory.createContexts ();
      for (final CVAContextData aCVAContextData : aCVAData.getAllContexts ()) {
        final Context aContext = aFactory.createContext ();
        aContext.setAddress (aCVAContextData.getItem ());
        aContext.getValues ().add (aValueListMap.get (aCVAContextData.getCodeListName ()));
        aContext.setMark (aCVAContextData.getSeverity ());
        final Message aMessage = aFactory.createMessage ();
        aMessage.getContent ().add ("[" + aCVAContextData.getID () + "]-" + aCVAContextData.getMessage ());
        aContext.getMessage ().add (aMessage);
        aContexts.getContext ().add (aContext);
      }
      aCVA.setContexts (aContexts);

      // to XML
      final Document aXML = new CVA10Marshaller ().write (aCVA);
      if (aXML == null)
        throw new IllegalStateException ("Failed to convert CVA to XML");

      // to File
      if (SimpleFileIO.writeFile (aCVAFile, XMLWriter.getXMLString (aXML), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aCVAFile);
    }
    return aAllReferencedCodeListNames;
  }

  @Nonnull
  private void _createCVAandGC (final RuleSourceCodeList aCodeList) throws Exception {
    Utils.log ("Reading code list file " + aCodeList.getSourceFile ());
    final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aCodeList.getSourceFile ());

    // Handle CVA sheets
    final Set <String> aAllReferencedCodeListNames = _readCVAData (aCodeList, aSpreadSheet);
    if (aAllReferencedCodeListNames.isEmpty ())
      throw new IllegalStateException ("CVA was not referencing any code list!");

    // Create only the GC files that are referenced from the CVA sheet
    Utils.log ("  Reading codelists");
    for (final String sCodeListName : aAllReferencedCodeListNames) {
      final Table aSheet = aSpreadSheet.getSheetByName (sCodeListName);
      if (aSheet == null)
        throw new IllegalStateException ("Failed to resolve sheet with name '" + sCodeListName + "'");

      final File aGCFile = aCodeList.getGCFile (sCodeListName);
      Utils.log ("    Creating " + aGCFile.getName ());

      // Read data
      final String sShortname = ODFUtils.getText (aSheet, 0, 1);
      final String sVersion = ODFUtils.getText (aSheet, 1, 1);
      final String sAgency = ODFUtils.getText (aSheet, 2, 1);
      final String sLocationURI = ODFUtils.getText (aSheet, 3, 1);

      // Start creating Genericode
      final org.oasis.genericode.v10.ObjectFactory aFactory = new org.oasis.genericode.v10.ObjectFactory ();
      final CodeListDocument aGC = aFactory.createCodeListDocument ();

      // create identification
      final Identification aIdentification = aFactory.createIdentification ();
      aIdentification.setShortName (Genericode10Utils.createShortName (sShortname));
      aIdentification.setVersion (sVersion);
      aIdentification.setCanonicalUri (sAgency);
      aIdentification.setCanonicalVersionUri (sAgency + "-" + sVersion);
      aIdentification.getLocationUri ().add (sLocationURI);
      aGC.setIdentification (aIdentification);

      // Build column set
      final ColumnSet aColumnSet = aFactory.createColumnSet ();
      final Column aCodeColumn = Genericode10Utils.createColumn ("code",
                                                                 UseType.REQUIRED,
                                                                 "Code",
                                                                 null,
                                                                 "normalizedString");
      final Column aNameColumn = Genericode10Utils.createColumn ("name", UseType.OPTIONAL, "Name", null, "string");
      aColumnSet.getColumnChoice ().add (aCodeColumn);
      aColumnSet.getColumnChoice ().add (aNameColumn);
      aColumnSet.getKeyChoice ().add (Genericode10Utils.createKey ("codeKey", "CodeKey", null, aCodeColumn));
      aGC.setColumnSet (aColumnSet);

      // Add values
      final SimpleCodeList aSimpleCodeList = aFactory.createSimpleCodeList ();
      int nRow = 4;
      while (!ODFUtils.isEmpty (aSheet, 0, nRow)) {
        final String sCode = ODFUtils.getText (aSheet, 0, nRow);
        final String sValue = ODFUtils.getText (aSheet, 1, nRow);

        final Row aRow = aFactory.createRow ();
        Value aValue = aFactory.createValue ();
        aValue.setColumnRef (aCodeColumn);
        aValue.setSimpleValue (Genericode10Utils.createSimpleValue (sCode));
        aRow.getValue ().add (aValue);

        aValue = aFactory.createValue ();
        aValue.setColumnRef (aNameColumn);
        aValue.setSimpleValue (Genericode10Utils.createSimpleValue (sValue));
        aRow.getValue ().add (aValue);

        aSimpleCodeList.getRow ().add (aRow);

        // In code list name, a code is used
        if (m_aAllCodes.putSingle (sCodeListName, sCode).isUnchanged ())
          throw new IllegalStateException ("Found duplicate value '" + sCode + "' in code list " + sCodeListName);

        ++nRow;
      }
      aGC.setSimpleCodeList (aSimpleCodeList);

      // to XML
      final Document aXML = new Genericode10CodeListMarshaller ().write (aGC);
      if (aXML == null)
        throw new IllegalStateException ("Failed to convert CVA to XML");

      // to File
      if (SimpleFileIO.writeFile (aGCFile, XMLWriter.getXMLString (aXML), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aGCFile);
    }
  }

  private void _createCodelistSchematron (final RuleSourceCodeList aCodeList) {
    Utils.log ("  Writing Schematron code lists");
    // For all transactions
    for (final Map.Entry <String, CVAData> aEntry : m_aCVAs.entrySet ()) {
      final String sTransaction = aEntry.getKey ();
      final CVAData aCVAData = aEntry.getValue ();

      final File aSCHFile = aCodeList.getSchematronFile (sTransaction);
      Utils.log ("    Creating " + aSCHFile.getName ());

      // Create the XML document
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Code list Schematron rules for " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      ePattern.setAttribute ("id", "Codes-" + sTransaction);

      for (final CVAContextData aCVAContextData : aCVAData.getAllContexts ()) {
        final IMicroElement eRule = ePattern.appendElement (NS_SCHEMATRON, "rule");
        eRule.setAttribute ("context", aCVAContextData.getItem ());

        final IMicroElement eAssert = eRule.appendElement (NS_SCHEMATRON, "assert");
        eAssert.setAttribute ("flag", aCVAContextData.getSeverity ());
        final Set <String> aMatchingCodes = m_aAllCodes.get (aCVAContextData.getCodeListName ());
        // Previously used 007f is an invalid XML character, so we cannot use it
        // safely!
        final char cSep = '\ufffd';
        final String sTest = "contains('" +
                             cSep +
                             StringHelper.getImploded (cSep, aMatchingCodes) +
                             cSep +
                             "',concat('" +
                             cSep +
                             "',.,'" +
                             cSep +
                             "'))";
        eAssert.setAttribute ("test", sTest);
        eAssert.appendText ("[" + aCVAContextData.getID () + "]-" + aCVAContextData.getMessage ());
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);
    }
  }

  private void _createSchematronXSLTs (final RuleSourceCodeList aCodeList) throws TransformerException {
    Utils.log ("  Converting CVA files to Schematron XSLT");
    // Create only once (caching)
    if (s_aCVA2SCH == null) {
      final TransformerFactory aTF = XMLTransformerFactory.createTransformerFactory (null,
                                                                                     new DefaultTransformURIResolver ());
      s_aCVA2SCH = aTF.newTemplates (TransformSourceFactory.create (new File ("src/test/resources/rule-utils/Crane-cva2schXSLT.xsl")));
    }
    // Convert the CVA files for all transactions
    for (final String sTransaction : m_aCVAs.keySet ()) {
      final File aCVAFile = aCodeList.getCVAFile (sTransaction);
      final File aResultXSLT = aCodeList.getXSLTFile (sTransaction);
      Utils.log ("    Creating " + aResultXSLT.getName ());
      final Transformer aTransformer = s_aCVA2SCH.newTransformer ();
      final Document aSCHDoc = XMLFactory.newDocument ();
      aTransformer.transform (TransformSourceFactory.create (aCVAFile), new DOMResult (aSCHDoc));
      SimpleFileIO.writeFile (aResultXSLT, XMLWriter.getXMLString (aSCHDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
    }
  }

  public void createCodeLists (final RuleSourceCodeList aCodeList) throws Exception {
    // Create .CVA and .GC files
    _createCVAandGC (aCodeList);

    // Create Schematron code list files
    _createCodelistSchematron (aCodeList);

    // Convert CVAs to Schematron XSLTs
    // Currently disabled because there is no real sense in it, as we're
    // creating the Schematrons manually
    if (false)
      _createSchematronXSLTs (aCodeList);
  }
}
