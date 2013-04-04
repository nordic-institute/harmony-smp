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
package eu.europa.ec.cipa.webgui.app.components.adapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

  public InvoiceTaxSubtotalAdapter () {
    super ();
    tableLineID = "";
    setTaxableAmount (new TaxableAmountType ());
    setTaxAmount (new TaxAmountType ());
    final TaxCategoryType tc = new TaxCategoryType ();
    tc.setID (new IDType ());
    tc.setPercent (new PercentType ());
    tc.setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());
    tc.setTaxExemptionReason (new TaxExemptionReasonType ());
    final TaxSchemeType ts = new TaxSchemeType ();
    ts.setID (new IDType ());
    tc.setTaxScheme (ts);
    setTaxCategory (tc);
  }

  public InvoiceTaxSubtotalAdapter (final TaxSubtotalType type) {
    super ();
    tableLineID = "";

    if (type.getTaxableAmount () != null)
      this.setTaxableAmount (type.getTaxableAmount ());
    else
      this.setTaxableAmount (new TaxableAmountType ());
    if (type.getTaxAmount () != null)
      this.setTaxAmount (type.getTaxAmount ());
    else
      this.setTaxAmount (new TaxAmountType ());
    if (type.getTaxCategory () != null) {
      final TaxCategoryType tc = type.getTaxCategory ();
      this.setTaxCategory (tc);
      if (tc.getID () == null)
        tc.setID (new IDType ());
      if (tc.getPercent () == null)
        tc.setPercent (new PercentType ());
      if (tc.getTaxExemptionReasonCode () == null)
        tc.setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());
      if (tc.getTaxExemptionReason () == null)
        tc.setTaxExemptionReason (new TaxExemptionReasonType ());
      if (tc.getTaxScheme () == null) {
        final TaxSchemeType ts = new TaxSchemeType ();
        ts.setID (new IDType ());
        tc.setTaxScheme (ts);
      }
      else {
        if (tc.getTaxScheme ().getID () == null)
          tc.getTaxScheme ().setID (new IDType ());
      }
    }
    else {
      final TaxCategoryType tc = new TaxCategoryType ();
      tc.setID (new IDType ());
      tc.setPercent (new PercentType ());
      tc.setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());
      tc.setTaxExemptionReason (new TaxExemptionReasonType ());
      final TaxSchemeType ts = new TaxSchemeType ();
      ts.setID (new IDType ());
      tc.setTaxScheme (ts);
      this.setTaxCategory (tc);
    }
  }

  public void setID (final IDType id) {/* dummy method */}

  @Override
  public int hashCode () {
    int ret = 0;
    if (getTableLineID () != null)
      ret += getTableLineID ().hashCode ();
    if (getTaxSubTotalTaxableAmount () != null)
      ret += getTaxSubTotalTaxableAmount ().hashCode ();
    if (getTaxSubTotalTaxAmount () != null)
      ret += getTaxSubTotalTaxAmount ().hashCode ();
    if (getTaxSubTotalCategoryID () != null)
      ret += getTaxSubTotalCategoryID ().hashCode ();
    if (getTaxSubTotalCategoryPercent () != null)
      ret += getTaxSubTotalCategoryPercent ().hashCode ();
    if (getTaxSubTotalCategoryExemptionReasonCode () != null)
      ret += getTaxSubTotalCategoryExemptionReasonCode ().hashCode ();
    if (getTaxSubTotalCategoryExemptionReason () != null)
      ret += getTaxSubTotalCategoryExemptionReason ().hashCode ();
    if (getTaxSubTotalCategoryTaxSchemeID () != null)
      ret += getTaxSubTotalCategoryTaxSchemeID ().hashCode ();

    return ret;
  }

  @Override
  public boolean equals (final Object obj) {
    if (obj == null)
      return false;

    if (this.hashCode () == obj.hashCode ())
      return true;

    return false;
  }

  @Override
  public void setIDAdapter (final String id) {
    // setTableLineID(id);
    tableLineID = id;
  }

  @Override
  public String getIDAdapter () {
    // return getTableLineID();
    return tableLineID;
  }

  public void setTableLineID (final String id) {
    tableLineID = id;
  }

  public String getTableLineID () {
    return tableLineID;
  }

  public void setTaxSubTotalTaxableAmount (BigDecimal v) {
    v = v.setScale (2, RoundingMode.HALF_UP);
    if (getTaxableAmount () == null)
      setTaxableAmount (new TaxableAmountType ());
    getTaxableAmount ().setValue (v);
  }

  public BigDecimal getTaxSubTotalTaxableAmount () {
    if (getTaxableAmount () != null)
      return getTaxableAmount ().getValue ();
    return null;
  }

  public void setTaxSubTotalTaxAmount (BigDecimal v) {
    v = v.setScale (2, RoundingMode.HALF_UP);

    if (getTaxAmount () == null)
      setTaxAmount (new TaxAmountType ());

    getTaxAmount ().setValue (v);
  }

  public BigDecimal getTaxSubTotalTaxAmount () {
    if (getTaxAmount () != null)
      return getTaxAmount ().getValue ();
    return null;
  }

  public void setTaxSubTotalCategoryID (final String v) {
    if (getTaxCategory ().getID () == null)
      getTaxCategory ().setID (new IDType ());

    getTaxCategory ().getID ().setValue (v);
  }

  public String getTaxSubTotalCategoryID () {
    if (getTaxCategory ().getID () != null)
      return getTaxCategory ().getID ().getValue ();
    return null;
  }

  public void setTaxSubTotalCategoryPercent (BigDecimal v) {
    v = v.setScale (2, RoundingMode.HALF_UP);
    if (getTaxCategory ().getPercent () == null)
      getTaxCategory ().setPercent (new PercentType ());

    getTaxCategory ().getPercent ().setValue (v);
  }

  public BigDecimal getTaxSubTotalCategoryPercent () {
    if (getTaxCategory ().getPercent () != null)
      return getTaxCategory ().getPercent ().getValue ();
    return null;
  }

  public void setTaxSubTotalCategoryExemptionReasonCode (final String v) {
    if (getTaxCategory ().getTaxExemptionReasonCode () == null)
      getTaxCategory ().setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());

    getTaxCategory ().getTaxExemptionReasonCode ().setValue (v);
  }

  public String getTaxSubTotalCategoryExemptionReasonCode () {
    if (getTaxCategory ().getTaxExemptionReasonCode () != null)
      return getTaxCategory ().getTaxExemptionReasonCode ().getValue ();
    return null;
  }

  public void setTaxSubTotalCategoryExemptionReason (final String v) {
    if (getTaxCategory ().getTaxExemptionReason () == null)
      getTaxCategory ().setTaxExemptionReason (new TaxExemptionReasonType ());

    getTaxCategory ().getTaxExemptionReason ().setValue (v);
  }

  public String getTaxSubTotalCategoryExemptionReason () {
    if (getTaxCategory ().getTaxExemptionReason () != null)
      return getTaxCategory ().getTaxExemptionReason ().getValue ();
    return null;
  }

  public void setTaxSubTotalCategoryTaxSchemeID (final String v) {
    if (getTaxCategory ().getTaxScheme ().getID () == null)
      getTaxCategory ().getTaxScheme ().setID (new IDType ());

    getTaxCategory ().getTaxScheme ().getID ().setValue (v);

  }

  public String getTaxSubTotalCategoryTaxSchemeID () {
    if (getTaxCategory ().getTaxScheme ().getID () != null)
      return getTaxCategory ().getTaxScheme ().getID ().getValue ();
    return null;
  }

  public void setEmptyAsNull () {
    if (getTaxSubTotalCategoryExemptionReason () != null) {
      if (getTaxSubTotalCategoryExemptionReason ().trim ().equals (""))
        getTaxCategory ().setTaxExemptionReason (null);
    }
    else
      getTaxCategory ().setTaxExemptionReason (null);

    if (getTaxSubTotalCategoryExemptionReasonCode () != null) {
      if (getTaxSubTotalCategoryExemptionReasonCode ().trim ().equals (""))
        getTaxCategory ().setTaxExemptionReasonCode (null);
    }
    else
      getTaxCategory ().setTaxExemptionReasonCode (null);

    if (this.getTaxSubTotalCategoryPercent () == BigDecimal.ZERO)
      getTaxCategory ().setPercent (null);
  }
}
