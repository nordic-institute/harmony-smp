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
package at.peppol.webgui.app.components.adapters;

import java.math.BigDecimal;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PercentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmountType;

@SuppressWarnings ("serial")
public class InvoiceTaxSubtotalAdapter extends TaxSubtotalType implements Adapter {
  private String tableLineID;
  
  public InvoiceTaxSubtotalAdapter() {
    tableLineID = "";
    setTaxableAmount (new TaxableAmountType ());
    setTaxAmount (new TaxAmountType ());
    TaxCategoryType tc = new TaxCategoryType ();
    tc.setID (new IDType ());
    tc.setPercent (new PercentType ());
    tc.setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());
    tc.setTaxExemptionReason (new TaxExemptionReasonType ());
    TaxSchemeType ts = new TaxSchemeType ();
    ts.setID (new IDType ());
    tc.setTaxScheme (ts);
    setTaxCategory (tc);
  }
  
  public void setID(IDType id) {/*dummy method*/}
  
  @Override
  public void setIDAdapter(String id) {
	  setTableLineID(id);
  }
  
  @Override
  public String getIDAdapter() {
	  return getTableLineID();
  }
  
  public void setTableLineID(String v) {
    tableLineID = v;
  }
  
  public String getTableLineID () {
    return tableLineID;
  }

  public void setTaxSubTotalTaxableAmount(BigDecimal v) {
    getTaxableAmount ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalTaxableAmount() {
    return getTaxableAmount ().getValue ();
  } 
  
  public void setTaxSubTotalTaxAmount(BigDecimal v) {
    getTaxAmount ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalTaxAmount() {
    return getTaxAmount ().getValue ();
  }    
  
  public void setTaxSubTotalCategoryID(String v) {
    getTaxCategory ().getID ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryID() {
    return getTaxCategory ().getID ().getValue ();
  }
  
  public void setTaxSubTotalCategoryPercent(BigDecimal v) {
    getTaxCategory ().getPercent ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalCategoryPercent() {
    return getTaxCategory ().getPercent ().getValue ();
  }   
  
  public void setTaxSubTotalCategoryExemptionReasonCode(String v) {
    getTaxCategory ().getTaxExemptionReasonCode ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryExemptionReasonCode() {
    return getTaxCategory ().getTaxExemptionReasonCode ().getValue ();
  }
  
  public void setTaxSubTotalCategoryExemptionReason(String v) {
    getTaxCategory ().getTaxExemptionReason ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryExemptionReason() {
    return getTaxCategory ().getTaxExemptionReason ().getValue ();
  }
  
  public void setTaxSubTotalCategoryTaxSchemeID(String v) {
    getTaxCategory ().getTaxScheme ().getID ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryTaxSchemeID() {
    return getTaxCategory ().getTaxScheme ().getID ().getValue ();
  }
}
