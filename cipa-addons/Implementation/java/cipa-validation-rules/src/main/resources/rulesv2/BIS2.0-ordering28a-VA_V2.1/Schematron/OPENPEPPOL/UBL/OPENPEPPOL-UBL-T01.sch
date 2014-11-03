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
<!-- Data binding to UBL syntax for T01 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T01" id="UBL-T01">
  <param name="EUGEN-T01-R011" value="@schemeID"/>
  <param name="EUGEN-T01-R012" value="@schemeID"/>
  <param name="EUGEN-T01-R013" value="@listID='UNCLD1001'"/>
  <param name="EUGEN-T01-R014" value="@listID='ISO4217'"/>
  <param name="EUGEN-T01-R015" value="@listID = 'ISO3166-1:Alpha2'"/>
  <param name="EUGEN-T01-R016" value="not(attribute::unitCode) or (attribute::unitCode and attribute::unitCodeListID = 'UNECERec20')"/>
  <param name="EUGEN-T01-R017" value="@schemeID  = 'UNCL5305'"/>
  <param name="Order_Line" value="//cac:OrderLine"/>
  <param name="Order_Type" value="//cbc:OrderTypeCode"/>
  <param name="Order" value="/ubl:Order"/>
  <param name="Allowance_Charges" value="//cac:AllowanceCharge"/>
  <param name="Expected_totals" value="//cac:AnticipatedMonetaryTotal"/>
  <param name="Item_property" value="//cac:AdditionalItemProperty"/>
  <param name="Seller" value="//cac:SellerSupplierParty"/>
  <param name="Buyer" value="//cac:BuyerCustomerParty"/>
  <param name="Delivery_Address" value="//cac:DeliveryLocation"/>
  <param name="Item" value="//cac:Item"/>
  <param name="Endpoint" value="//cbc:EndpointID"/>
  <param name="Party_Identifier" value="//cac:PartyIdentification/cbc:ID"/>
  <param name="Document_Currency" value="//cbc:DocumentCurrencyCode"/>
  <param name="Country_identification" value="//cac:Country/cbc:IdentificationCode"/>
  <param name="Unit_Code" value="//*[contains(name(),'Quantity')]"/>
  <param name="Tax_Category_Identifier" value="//cac:ClassifiedTaxCategory/cbc:ID"/>
</pattern>
