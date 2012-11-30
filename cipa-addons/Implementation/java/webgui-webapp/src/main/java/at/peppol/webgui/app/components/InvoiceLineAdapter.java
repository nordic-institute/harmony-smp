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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AccountingCostType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoicedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.MultiplierFactorNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PercentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PriceAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueType;

/**
 * An adapter class that ignores the list types found in InvoiceLineType and
 * wraps the lists with one element, that represents the first element of these
 * lists using simple Getters and Setters. This is useful for making table
 * representations.
 * 
 * @author Jerouris
 */
@SuppressWarnings ("serial")
public class InvoiceLineAdapter extends InvoiceLineType {
  // private final TaxSubtotalType VATTax;

  public InvoiceLineAdapter () {
    setID (new IDType ());
    setNote (new NoteType ());
    setInvoicedQuantity (new InvoicedQuantityType ());
    setLineExtensionAmount (new LineExtensionAmountType ());
    setAccountingCost (new AccountingCostType ());

    // --- +Invoice Line/Order Line Reference begins (0..N)
    final OrderLineReferenceType lr = new OrderLineReferenceType ();
    lr.setLineID (new LineIDType ());
    //getOrderLineReference ().add (lr);
    // --- +Invoice Line/Order Line Reference ends

    // --- +Invoice Line/Allowance Charge begins (0..N)
    // getAllowanceCharge ().add (new AllowanceChargeType ());
    // --- +Invoice Line/Allowance Charge ends

    // --- +Invoice Line/Tax Total begins
    final TaxTotalType tt = new TaxTotalType ();
    tt.setTaxAmount (new TaxAmountType ());
    getTaxTotal ().add (tt);
    // --- +Invoice Line/Tax Total ends

    // --- +Invoice Line/Item begins
    final ItemType item = new ItemType ();
    
    item.getDescription ().add (new DescriptionType ());
    
    item.setName (new NameType ());
    
    item.setSellersItemIdentification (new ItemIdentificationType ());
    item.getSellersItemIdentification ().setID (new IDType ());
    
    item.setStandardItemIdentification (new ItemIdentificationType ());
    item.getStandardItemIdentification ().setID (new IDType ());
    
    final TaxCategoryType ct = new TaxCategoryType ();
    ct.setID (new IDType ());
    ct.setPercent (new PercentType ());
    
    final TaxSchemeType tst = new TaxSchemeType ();
    tst.setID (new IDType ());
    ct.setTaxScheme (tst);
    
    item.getClassifiedTaxCategory ().add (ct);

    // --- +Item/Commodity Classification begins (0..N)
    // --- +Item/Commodity Classification ends

    // --- +Item/Additional Item Property begins (0..N)
    //final ItemPropertyType pt = new ItemPropertyType ();
    //pt.setName (new NameType ());
    //pt.setValue (new ValueType ());
    //item.getAdditionalItemProperty ().add (pt);
    // --- +Item/Additional Item Property ends

    setItem (item);
    // --- +Invoice Line/Item ends

    // --- +Invoice Line/Price begins
    setPrice (new PriceType ());
    getPrice ().setPriceAmount (new PriceAmountType ());
    getPrice ().setBaseQuantity (new BaseQuantityType ());

    final AllowanceChargeType ac = new AllowanceChargeType ();
    ac.setID (new IDType ());
    ac.setChargeIndicator (new ChargeIndicatorType ());
    ac.setAllowanceChargeReason (new AllowanceChargeReasonType ());
    ac.setMultiplierFactorNumeric (new MultiplierFactorNumericType ());
    ac.setAmount (new AmountType ());
    ac.setBaseAmount (new BaseAmountType ());
    getPrice ().getAllowanceCharge ().add (ac);
    // --- +Invoice Line/Price ends

    /*
     * jerry's old // Initialization of required fields setItem(new ItemType());
     * setID(new IDType()); NoteType nt = new NoteType(); nt.setValue ("");
     * setNote (nt); getItem().setName(new NameType());
     * getItem().setSellersItemIdentification(new ItemIdentificationType());
     * getItem().getSellersItemIdentification().setID(new IDType());
     * setInvoicedQuantity(new InvoicedQuantityType());
     * getInvoicedQuantity().setValue(BigDecimal.TEN); // Price defaults
     * setPrice(new PriceType()); getPrice().setPriceAmount(new
     * PriceAmountType()); // Tax totals and subtotals getTaxTotal().add(new
     * TaxTotalType()); setItemDescription(""); //0. is the VAT Tax VATTax = new
     * TaxSubtotalType(); getTaxTotal().get(0).getTaxSubtotal().add(VATTax);
     * VATTax.setTaxableAmount(new TaxableAmountType()); VATTax.setTaxAmount(new
     * TaxAmountType()); VATTax.setTaxCategory(new TaxCategoryType());
     * VATTax.getTaxCategory().setTaxScheme(new TaxSchemeType());
     * VATTax.getTaxCategory().getTaxScheme().setID(new IDType());
     * VATTax.getTaxCategory
     * ().getTaxScheme().getID().setSchemeID("UN/ECE 5153");
     * VATTax.getTaxCategory().getTaxScheme().getID().setSchemeAgencyID("6");
     * VATTax.getTaxCategory().getTaxScheme().getID().setValue("VAT");
     * VATTax.getTaxCategory().setPercent(new PercentType());
     */
  }

  // Pattern is: <set/get>InvLine<invoiceLineType-method name>(v)
  public void setInvLineID (final String v) {
    getID ().setValue (v);
  }

  public String getInvLineID () {
    return getID ().getValue ();
  }

  public void setInvLineNote (final String v) {
    getNote ().setValue (v);
  }

  public String getInvLineNote () {
    return getNote ().getValue ();
  }

  public void setInvLineInvoicedQuantity (final BigDecimal v) {
    getInvoicedQuantity ().setValue (v);
  }

  public BigDecimal getInvLineInvoicedQuantity () {
    return getInvoicedQuantity ().getValue ();
  }

  public void setInvLineLineExtensionAmount (final BigDecimal v) {
    getLineExtensionAmount ().setValue (v);
  }

  public BigDecimal getInvLineLineExtensionAmount () {
    return getLineExtensionAmount ().getValue ();
  }

  public void setInvLineAccountingCost (final String v) {
    getAccountingCost ().setValue (v);
  }

  public String getInvLineAccountingCost () {
    return getAccountingCost ().getValue ();
  }

  public void setInvLineTaxAmount (final BigDecimal v) {
    getTaxTotal ().get (0).getTaxAmount ().setValue (v);
  }

  public BigDecimal getInvLineTaxAmount () {
    return getTaxTotal ().get (0).getTaxAmount ().getValue ();
  }

  /* pattern: <set/get>InvLineItem<function-name> */
  public void setInvLineItemDescription (final String v) {
    getItem ().getDescription ().get (0).setValue (v);
  }

  public String getInvLineItemDescription () {
    return getItem ().getDescription ().get (0).getValue ();
  }

  public void setInvLineItemName (final String v) {
    getItem ().getName ().setValue (v);
  }

  public String getInvLineItemName () {
    return getItem ().getName ().getValue ();
  }

  public void setInvLineItemSellersItemID (final String v) {
    getItem ().getSellersItemIdentification ().getID ().setValue (v);
  }

  public String getInvLineItemSellersItemID () {
    return getItem ().getSellersItemIdentification ().getID ().getValue ();
  }

  public void setInvLineItemStandardItemID (final String v) {
    getItem ().getStandardItemIdentification ().getID ().setValue (v);
  }

  public String getInvLineItemStandardItemID () {
    return getItem ().getStandardItemIdentification ().getID ().getValue ();
  }

  public void setInvLineItemTaxCategoryID (final String v) {
    getItem ().getClassifiedTaxCategory ().get (0).getID ().setValue (v);
  }

  public String getInvLineItemTaxCategoryID () {
    return getItem ().getClassifiedTaxCategory ().get (0).getID ().getValue ();
  }

  public void setInvLineItemTaxCategoryPercent (final BigDecimal v) {
    getItem ().getClassifiedTaxCategory ().get (0).getPercent ().setValue (v);
  }

  public BigDecimal getInvLineItemTaxCategoryPercent () {
    return getItem ().getClassifiedTaxCategory ().get (0).getPercent ().getValue ();
  }

  public void setInvLineItemTaxCategoryTaxSchemeID (final String v) {
    getItem ().getClassifiedTaxCategory ().get (0).getTaxScheme ().getID ().setValue (v);
  }

  public String getInvLineItemTaxCategoryTaxSchemeID () {
    return getItem ().getClassifiedTaxCategory ().get (0).getTaxScheme ().getID ().getValue ();
  }

  /* pattern: <set/get>InvLine<function-name> */

  public void setInvLinePriceAmount (final BigDecimal v) {
    getPrice ().getPriceAmount ().setValue (v);
  }

  public BigDecimal getInvLinePriceAmount () {
    return getPrice ().getPriceAmount ().getValue ();
  }

  public void setInvLinePriceBaseQuantity (final BigDecimal v) {
    getPrice ().getBaseQuantity ().setValue (v);
  }

  public BigDecimal getInvLinePriceBaseQuantity () {
    return getPrice ().getBaseQuantity ().getValue ();
  }

  /* pattern: <set/get>InvLinePriceAllowanceCharge<function-name> */
  public void setInvLinePriceAllowanceChargeID (final String v) {
    getPrice ().getAllowanceCharge ().get (0).getID ().setValue (v);
  }

  public String getInvLinePriceAllowanceChargeID () {
    return getPrice ().getAllowanceCharge ().get (0).getID ().getValue ();
  }

  public void setInvLinePriceAllowanceChargeIndicator (final Boolean v) {
    getPrice ().getAllowanceCharge ().get (0).getChargeIndicator ().setValue (v.booleanValue ());
  }

  public Boolean getInvLinePriceAllowanceChargeIndicator () {
    if (getPrice ().getAllowanceCharge ().get (0).getChargeIndicator ().isValue ())
      return Boolean.TRUE;
    return Boolean.FALSE;
  }

  public void setInvLinePriceAllowanceChargeReason (final String v) {
    getPrice ().getAllowanceCharge ().get (0).getAllowanceChargeReason ().setValue (v);
  }

  public String getInvLinePriceAllowanceChargeReason () {
    return getPrice ().getAllowanceCharge ().get (0).getAllowanceChargeReason ().getValue ();
  }

  public void setInvLinePriceAllowanceChargeMultiplierFactorNumeric (final BigDecimal v) {
    getPrice ().getAllowanceCharge ().get (0).getMultiplierFactorNumeric ().setValue (v);
  }

  public BigDecimal getInvLinePriceAllowanceChargeMultiplierFactorNumeric () {
    return getPrice ().getAllowanceCharge ().get (0).getMultiplierFactorNumeric ().getValue ();
  }

  public void setInvLinePriceAllowanceChargeAmount (final BigDecimal v) {
    getPrice ().getAllowanceCharge ().get (0).getAmount ().setValue (v);
  }

  public BigDecimal getInvLinePriceAllowanceChargeAmount () {
    return getPrice ().getAllowanceCharge ().get (0).getAmount ().getValue ();
  }

  public void setInvLinePriceAllowanceChargeBaseAmount (final BigDecimal v) {
    getPrice ().getAllowanceCharge ().get (0).getBaseAmount ().setValue (v);
  }

  public BigDecimal getInvLinePriceAllowanceChargeBaseAmount () {
    return getPrice ().getAllowanceCharge ().get (0).getBaseAmount ().getValue ();
  }

  /*
   * public void setInvLineAdditionalItemPropertyList(List<ItemPropertyType> v)
   * { getItem ().getAdditionalItemProperty ().add (v.get (0)); }
   */

  public List <ItemPropertyType> getInvLineAdditionalItemPropertyList () {
    //if (getItem ().getAdditionalItemProperty ().isEmpty ()) {
    //  getItem ().getAdditionalItemProperty ().add (new ItemPropertyType ());
    //}
    return getItem ().getAdditionalItemProperty ();
  }
  
  public List<AllowanceChargeType> getInvLineAllowanceChargeList() {
	  return getAllowanceCharge();
  }
  
  public List<OrderLineReferenceType> getInvLineOrderList() {
	  return getOrderLineReference();
  }
  
  public List<CommodityClassificationType> getInvLineCommodityClassificationList() {
	  return getItem().getCommodityClassification();
  }

  /*
   * jerry's old public String getItemDescription() { if
   * (getItem().getDescription().isEmpty()) { return null; } else { return
   * getItem().getDescription().get(0).getValue(); } } public final void
   * setItemDescription(String description) { if
   * (getItem().getDescription().isEmpty()) { getItem().getDescription().add(new
   * DescriptionType()); }
   * getItem().getDescription().get(0).setValue(description); } public void
   * setSellersItemID(String id) {
   * getItem().getSellersItemIdentification().getID().setValue(id); } public
   * String getSellersItemID() { return
   * getItem().getSellersItemIdentification().getID().getValue(); } public void
   * setNotes(String n) { getNote().setValue (n); } public String getNotes() {
   * return getNote().getValue (); } public void setPriceAmount(long amount) {
   * getPrice().getPriceAmount().setValue(BigDecimal.valueOf(amount)); } public
   * long getPriceAmount() throws Exception { BigDecimal val =
   * getPrice().getPriceAmount().getValue(); if (val == null ) { //throw new
   * Exception("Value is null"); return 0; } return
   * getPrice().getPriceAmount().getValue().longValue(); } public void
   * setVatPercent(double percent) { getTaxTotal(); } public int getQuantity() {
   * return getInvoicedQuantity().getValue().intValue(); } public void
   * setQuantity(int q) { getInvoicedQuantity().setValue(BigDecimal.valueOf(q));
   * }
   */
}
