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
package eu.europa.ec.cipa.validation.web.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.mime.CMimeType;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.url.ISimpleURL;
import com.phloc.commons.url.SimpleURL;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.html.css.DefaultCSSClassProvider;
import com.phloc.html.css.ICSSClassProvider;
import com.phloc.html.hc.CHCParam;
import com.phloc.html.hc.IHCCell;
import com.phloc.html.hc.conversion.HCSettings;
import com.phloc.html.hc.html.HCBody;
import com.phloc.html.hc.html.HCButton_Submit;
import com.phloc.html.hc.html.HCCheckBox;
import com.phloc.html.hc.html.HCCol;
import com.phloc.html.hc.html.HCDiv;
import com.phloc.html.hc.html.HCForm;
import com.phloc.html.hc.html.HCH1;
import com.phloc.html.hc.html.HCH2;
import com.phloc.html.hc.html.HCHead;
import com.phloc.html.hc.html.HCHiddenField;
import com.phloc.html.hc.html.HCHtml;
import com.phloc.html.hc.html.HCLink;
import com.phloc.html.hc.html.HCSpan;
import com.phloc.html.hc.html.HCTable;
import com.phloc.html.hc.html.HCTextArea;
import com.phloc.html.hc.html.HCUL;
import com.phloc.web.servlet.response.UnifiedResponse;
import com.phloc.webscopes.domain.IRequestWebScopeWithoutResponse;
import com.phloc.webscopes.servlets.AbstractUnifiedResponseServlet;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramid;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResult;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationSyntaxBinding;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;
import eu.europa.ec.cipa.validation.web.ctrl.CountrySelect;
import eu.europa.ec.cipa.validation.web.ctrl.DocumentTypeSelect;
import eu.europa.ec.cipa.validation.web.ctrl.SyntaxBindingSelect;
import eu.europa.ec.cipa.validation.web.ctrl.TransactionSelect;

public final class ValidationServlet extends AbstractUnifiedResponseServlet
{
  private static final String FIELD_SYNTAX_BINDING = "syntaxbinding";
  private static final String FIELD_DOCTYPE = "doctype";
  private static final String FIELD_TRANSACTION = "transaction";
  private static final String FIELD_COUNTRY = "country";
  private static final String FIELD_INDUSTRY_SPECIFIC = "industryspecific";
  private static final String FIELD_XML = "inline";

  private static final boolean DEFAULT_INDUSTRY_SPECIFIC = false;
  private static final ICSSClassProvider CSS_CLASS_ERROR = DefaultCSSClassProvider.create ("error");
  private static final ICSSClassProvider CSS_CLASS_WARN = DefaultCSSClassProvider.create ("warn");
  private static final ICSSClassProvider CSS_CLASS_SUCCESS = DefaultCSSClassProvider.create ("success");
  private static final ICSSClassProvider CSS_CLASS_FIELDNAME = DefaultCSSClassProvider.create ("fieldname");
  private static final ICSSClassProvider CSS_CLASS_FIELDCTRL = DefaultCSSClassProvider.create ("fieldctrl");
  private static final ICSSClassProvider CSS_CLASS_BUTTONBAR = DefaultCSSClassProvider.create ("buttonbar");

  @Nonnull
  private static ISimpleURL _makeContextAwareURL (@Nonnull final IRequestWebScopeWithoutResponse aHttpRequest,
                                                  @Nonnull final String sPath)
  {
    return new SimpleURL (aHttpRequest.getFullContextPath () + sPath);
  }

  @Nonnull
  private static HCDiv _createError (final String s)
  {
    return HCDiv.create (s).addClass (CSS_CLASS_ERROR);
  }

  private static boolean _containsTransaction (@Nullable final EValidationDocumentType eDocType,
                                               @Nonnull final ETransaction eTransaction)
  {
    for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null, eDocType, null))
      if (eArtefact.getAllTransactions ().contains (eTransaction))
        return true;
    return false;
  }

  /**
   * GET and POST handler
   * 
   * @param aRequestScope
   *        request scope
   * @param aUnifiedResponse
   *        response object
   * @throws Exception
   */
  @Override
  protected void handleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final Locale aDisplayLocale = Locale.US;

    // Request parameter values
    final String sSelectedSyntax = aRequestScope.getAttributeAsString (FIELD_SYNTAX_BINDING);
    final EValidationSyntaxBinding eSelectedSyntax = EValidationSyntaxBinding.getFromIDOrNull (sSelectedSyntax);
    final String sSelectedDocType = aRequestScope.getAttributeAsString (FIELD_DOCTYPE);
    final EValidationDocumentType eSelectedDocType = EValidationDocumentType.getFromIDOrNull (sSelectedDocType);
    final String sSelectedTransaction = aRequestScope.getAttributeAsString (FIELD_TRANSACTION);
    final ETransaction eSelectedTransaction = ETransaction.getFromIDOrNull (sSelectedTransaction);
    final String sSelectedCountry = aRequestScope.getAttributeAsString (FIELD_COUNTRY);
    final Locale aSelectedCountry = LocaleCache.getLocale (sSelectedCountry);
    final String sSelectedIndustryLevel = aRequestScope.getAttributeAsString (FIELD_INDUSTRY_SPECIFIC);
    final boolean bSelectedIndustryLevel = sSelectedIndustryLevel != null ? Boolean.parseBoolean (sSelectedIndustryLevel)
                                                                         : aRequestScope.getAttributeAsString (HCCheckBox.getHiddenFieldName (FIELD_INDUSTRY_SPECIFIC)) != null ? false
                                                                                                                                                                               : DEFAULT_INDUSTRY_SPECIFIC;
    final String sSelectedXML = aRequestScope.getAttributeAsString (FIELD_XML);

    // Base layout
    final HCHtml aHtml = new HCHtml ();
    final HCHead aHead = aHtml.getHead ();
    aHead.setPageTitle ("PEPPOL document validation");
    aHead.addCSS (HCLink.createCSSLink (_makeContextAwareURL (aRequestScope, "/css/normalize.min.css")));
    aHead.addCSS (HCLink.createCSSLink (_makeContextAwareURL (aRequestScope, "/css/main.css")));
    final HCBody aBody = aHtml.getBody ();

    boolean bShowForm = true;
    if (CHCParam.STATE_SUBMITTED.equals (aRequestScope.getAttributeAsString (CHCParam.PARAM_STATE)))
    {
      // User pressed submit
      final List <String> aErrors = new ArrayList <String> ();
      if (eSelectedSyntax == null)
        aErrors.add ("No syntax binding selected");
      if (eSelectedDocType == null)
        aErrors.add ("No document type selected");
      if (eSelectedTransaction == null)
        aErrors.add ("No transaction selected");
      if (eSelectedDocType != null &&
          eSelectedTransaction != null &&
          !_containsTransaction (eSelectedDocType, eSelectedTransaction))
        aErrors.add ("The passed transaction is not valid for the passed document type");

      Source aXMLSource = null;
      if (StringHelper.hasNoText (sSelectedXML))
        aErrors.add ("No XML content supplied");
      else
      {
        try
        {
          final Document aDoc = XMLReader.readXMLDOM (sSelectedXML);
          if (aDoc == null)
            aErrors.add ("Failed to parse passed XML");
          else
            aXMLSource = new DOMSource (aDoc);
        }
        catch (final SAXException e)
        {
          aErrors.add ("Failed to parse passed XML: " + e.getMessage ());
        }
      }

      if (aErrors.isEmpty ())
      {
        // No form errors
        final ValidationPyramid aVP = new ValidationPyramid (eSelectedDocType,
                                                             new ValidationTransaction (eSelectedSyntax,
                                                                                        eSelectedTransaction),
                                                             aSelectedCountry);
        // Do validation
        final ValidationPyramidResult aVPR = aVP.applyValidation (aXMLSource);
        final IResourceErrorGroup aREG = aVPR.getAggregatedResults ();
        aBody.addChild (HCH1.create ("PEPPOL document validation result"));
        if (aREG.containsAtLeastOneError ())
          aBody.addChild (HCH2.create ("The document is invalid!").addClass (CSS_CLASS_ERROR));
        else
          if (aREG.containsAtLeastOneFailure ())
            aBody.addChild (HCH2.create ("The document is valid but contains warnings!").addClass (CSS_CLASS_WARN));
          else
            aBody.addChild (HCH2.create ("The document is valid without warnings!").addClass (CSS_CLASS_SUCCESS));

        // List all failures
        final IResourceErrorGroup aFailures = aREG.getAllFailures ();
        if (!aFailures.isEmpty ())
        {
          final HCUL aUL = aBody.addAndReturnChild (new HCUL ());
          for (final IResourceError aResError : aREG)
            aUL.addItem (_createError (aResError.getAsString (aDisplayLocale)));
        }

        // Add link to main page
        final HCForm aForm = aBody.addAndReturnChild (new HCForm (_makeContextAwareURL (aRequestScope, "/validation/")));
        final HCDiv aToolbar = aForm.addAndReturnChild (new HCDiv ());
        aToolbar.addChild (new HCHiddenField (FIELD_SYNTAX_BINDING, sSelectedSyntax));
        aToolbar.addChild (new HCHiddenField (FIELD_DOCTYPE, sSelectedDocType));
        aToolbar.addChild (new HCHiddenField (FIELD_TRANSACTION, sSelectedTransaction));
        aToolbar.addChild (new HCHiddenField (FIELD_COUNTRY, sSelectedCountry));
        aToolbar.addChild (new HCHiddenField (FIELD_INDUSTRY_SPECIFIC, bSelectedIndustryLevel ? CHCParam.VALUE_CHECKED
                                                                                             : CHCParam.VALUE_UNCHECKED));
        aToolbar.addChild (new HCHiddenField (FIELD_XML, sSelectedXML));
        aToolbar.addChild (new HCButton_Submit ("Validate another document"));

        bShowForm = false;
      }
      else
      {
        // Show all error messages
        final HCUL aUL = aBody.addAndReturnChild (new HCUL ());
        for (final String sErrMsg : aErrors)
          aUL.addItem (_createError (sErrMsg));
      }
    }

    if (bShowForm)
    {
      aBody.addChild (HCH1.create ("PEPPOL document validation"));
      final HCForm aForm = aBody.addAndReturnChild (new HCForm (_makeContextAwareURL (aRequestScope, "/validation/")));

      final HCTable aTable = aForm.addAndReturnChild (new HCTable (new HCCol (150), HCCol.star ()));

      // Syntax binding
      aTable.addBodyRow ()
            .addCells (HCSpan.create ("Syntax:").addClass (CSS_CLASS_FIELDNAME),
                       new SyntaxBindingSelect (FIELD_SYNTAX_BINDING,
                                                eSelectedSyntax != null ? eSelectedSyntax.getID () : null).addClass (CSS_CLASS_FIELDCTRL));

      // Document type
      aTable.addBodyRow ()
            .addCells (HCSpan.create ("Document type:").addClass (CSS_CLASS_FIELDNAME),
                       new DocumentTypeSelect (FIELD_DOCTYPE, eSelectedDocType != null ? eSelectedDocType.getID ()
                                                                                      : null).addClass (CSS_CLASS_FIELDCTRL));

      // Transaction
      aTable.addBodyRow ()
            .addCells (HCSpan.create ("Transaction:").addClass (CSS_CLASS_FIELDNAME),
                       new TransactionSelect (FIELD_TRANSACTION,
                                              null,
                                              eSelectedTransaction != null ? eSelectedTransaction.getID () : null).addClass (CSS_CLASS_FIELDCTRL));

      // Country
      aTable.addBodyRow ()
            .addCells (HCSpan.create ("Country:").addClass (CSS_CLASS_FIELDNAME),
                       new CountrySelect (FIELD_COUNTRY, sSelectedCountry).addClass (CSS_CLASS_FIELDCTRL));

      // Industry specific?
      aTable.addBodyRow ().addCells (HCSpan.create ("Industry level:").addClass (CSS_CLASS_FIELDNAME),
                                     HCDiv.create (new HCCheckBox (FIELD_INDUSTRY_SPECIFIC, bSelectedIndustryLevel))
                                          .addClass (CSS_CLASS_FIELDCTRL));

      // XML row
      aTable.addBodyRow ().addCells (HCSpan.create ("XML content:").addClass (CSS_CLASS_FIELDNAME),
                                     new HCTextArea (FIELD_XML, sSelectedXML).setRows (20)
                                                                             .setCols (50)
                                                                             .addClass (CSS_CLASS_FIELDCTRL));

      // Toolbar
      final IHCCell <?> aCell = aTable.addBodyRow ().addCell ().setColspan (aTable.getColumnCount ());
      final HCDiv aToolbar = aCell.addAndReturnChild (new HCDiv ());
      aToolbar.addClass (CSS_CLASS_BUTTONBAR);
      aToolbar.addChild (new HCHiddenField (CHCParam.PARAM_STATE, CHCParam.STATE_SUBMITTED));
      aToolbar.addChild (new HCButton_Submit ("Validate"));
    }
    // Write HTML
    final String sHTML = HCSettings.getAsHTMLString (aHtml, false);
    aUnifiedResponse.disableCaching ()
                    .setContentAndCharset (sHTML, HCSettings.getHTMLCharset (false))
                    .setMimeType (CMimeType.TEXT_HTML);
  }
}
