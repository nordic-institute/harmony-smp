<!--

    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.

-->
<!-- Schematron binding rules generated automatically. -->
<!-- Data binding to UBL syntax for T10 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T10" id="UBL-T10">
  <param name="EUGEN-T10-R004" value="((cbc:PaymentMeansCode = '31') and (cac:PayeeFinancialAccount/cbc:ID/@schemeID and cac:PayeeFinancialAccount/cbc:ID/@schemeID = 'IBAN') and (cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID/@schemeID and cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID/@schemeID = 'BIC')) or (cbc:PaymentMeansCode != '31') or ((cbc:PaymentMeansCode = '31') and  (not(cac:PayeeFinancialAccount/cbc:ID/@schemeID) or (cac:PayeeFinancialAccount/cbc:ID/@schemeID != 'IBAN')))"/>
  <param name="EUGEN-T10-R008" value="(parent::cac:AllowanceCharge) or (cbc:ID and cbc:Percent) or (cbc:ID = 'AE')"/>
  <param name="EUGEN-T10-R012" value="not(cbc:MultiplierFactorNumeric) or number(cbc:MultiplierFactorNumeric) &gt;=0"/>
  <param name="EUGEN-T10-R022" value="number(cbc:Amount)&gt;=0"/>
  <param name="EUGEN-T10-R023" value="@schemeID"/>
  <param name="EUGEN-T10-R024" value="@schemeID"/>
  <param name="EUGEN-T10-R025" value="@listID = 'UNCL1001'"/>
  <param name="EUGEN-T10-R026" value="@listID =  'ISO4217'"/>
  <param name="EUGEN-T10-R027" value="@listID = 'ISO3166-1:Alpha2'"/>
  <param name="EUGEN-T10-R028" value="@listID = 'UNCL4461'"/>
  <param name="EUGEN-T10-R029" value="@listID = 'UNCL4465'"/>
  <param name="EUGEN-T10-R030" value="not(attribute::unitCode) or (attribute::unitCode and attribute::unitCodeListID = 'UNECERec20')"/>
  <param name="EUGEN-T10-R031" value="@schemeID"/>
  <param name="EUGEN-T10-R032" value="@schemeID = 'UNCL5305'"/>
  <param name="EUGEN-T10-R033" value="@listID = 'UNCL1001'"/>
  <param name="EUGEN-T10-R034" value="@schemeID"/>
  <param name="EUGEN-T10-R035" value="(cac:Party/cac:PartyName/cbc:Name)"/>
  <param name="EUGEN-T10-R036" value="(cac:Party/cac:PartyName/cbc:Name)"/>
  <param name="EUGEN-T10-R037" value="(cac:Party/cac:PostalAddress)"/>
  <param name="EUGEN-T10-R038" value="(cac:Party/cac:PostalAddress)"/>
  <param name="EUGEN-T10-R039" value="(cac:Party/cac:PartyLegalEntity)"/>
  <param name="EUGEN-T10-R040" value="(cac:Party/cac:PartyLegalEntity)"/>
  <param name="EUGEN-T10-R041" value="not(/ubl:Invoice/cac:TaxTotal/*/*/*/cbc:ID = 'VAT') or (starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))"/>
  <param name="EUGEN-T10-R042" value="((cbc:TaxableAmount) and (cac:TaxCategory/cbc:Percent) and (number(cbc:TaxAmount - 1) &lt; number(cbc:TaxableAmount * (cac:TaxCategory/cbc:Percent div 100))) and (number(cbc:TaxAmount + 1) &gt; number(cbc:TaxableAmount * (cac:TaxCategory/cbc:Percent div 100)))) or not(cac:TaxCategory/cbc:Percent) or not(cbc:TaxableAmount)"/>
  <param name="EUGEN-T10-R043" value="(number(child::cbc:TaxAmount)= round(number(sum(cac:TaxSubtotal/cbc:TaxAmount) * 10 * 10)) div 100) "/>
  <param name="EUGEN-T10-R044" value="not(//cbc:TaxCurrencyCode) or (//cac:TaxExchangeRate)"/>
  <param name="EUGEN-T10-R045" value="(cbc:CalculationRate) and (cbc:MathematicOperatorCode)"/>
  <param name="EUGEN-T10-R046" value="not(/ubl:Invoice/cbc:TaxCurrencyCode) or (cbc:TaxAmount and cbc:TransactionCurrencyTaxAmount)"/>
  <param name="VAT_category" value="//cac:TaxSubtotal[cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']"/>
  <param name="Unit_Code" value="//*[contains(name(),'Quantity')]"/>
  <param name="Total_Invoice" value="//cac:LegalMonetaryTotal"/>
  <param name="Tax_Category_Identifier" value="//cac:TaxCategory/cbc:ID"/>
  <param name="Tax_Category" value="//cac:TaxCategory"/>
  <param name="Payment_Means_Code" value="//cbc:PaymentMeansCode"/>
  <param name="Payment_Means" value="//cac:PaymentMeans"/>
  <param name="Party_Identifier" value="//cac:PartyIdentification/cbc:ID"/>
  <param name="Invoice_Type_Code" value="//cbc:InvoiceTypeCode"/>
  <param name="Invoice_Period_Information" value="//cac:InvoicePeriod"/>
  <param name="Invoice_Line" value="//cac:InvoiceLine"/>
  <param name="Invoice" value="/ubl:Invoice"/>
  <param name="Financial_Account_Identifier" value="//cac:PayeeFinancialAccount/cbc:ID"/>
  <param name="Endpoint" value="//cbc:EndpointID"/>
  <param name="Document_Type_Code" value="//cbc:DocumentTypeCode"/>
  <param name="Currency_Code" value="//*[contains(name(),'CurrencyCode')]"/>
  <param name="Delivery_Location_Identifier" value="//cac:DeliveryLocation/cbc:ID"/>
  <param name="Country_Identification_Code" value="//cac:Country/cbc:IdentificationCode"/>
  <param name="Allowance_Charge_Reason_Code" value="//cbc:AllowanceChargeReasonCode"/>
  <param name="Allowance_Charge" value="/ubl:Invoice/cac:AllowanceCharge"/>
  <param name="Supplier" value="//cac:AccountingSupplierParty"/>
  <param name="Customer" value="//cac:AccountingCustomerParty"/>
  <param name="Tax_Total" value="/ubl:Invoice/cac:TaxTotal"/>
  <param name="Tax_Exchange" value="//cac:TaxExchangeRate"/>
</pattern>
