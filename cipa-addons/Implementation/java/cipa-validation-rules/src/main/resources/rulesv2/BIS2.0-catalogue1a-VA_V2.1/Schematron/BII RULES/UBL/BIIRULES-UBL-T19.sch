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
<!-- Data binding to UBL syntax for T19 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T19" id="UBL-T19">
  <param name="BII2-T19-R001" value="(cbc:CustomizationID)"/>
  <param name="BII2-T19-R002" value="(cbc:ProfileID)"/>
  <param name="BII2-T19-R003" value="(cbc:IssueDate)"/>
  <param name="BII2-T19-R004" value="(cbc:ID)"/>
  <param name="BII2-T19-R006" value="(number(translate(cbc:StartDate,'-','')) &lt;= number(translate(cbc:EndDate,'-',''))) or (not(cbc:StartDate)) or (not(cbc:EndDate))"/>
  <param name="BII2-T19-R007" value="(cac:ProviderParty)"/>
  <param name="BII2-T19-R008" value="(cac:ReceiverParty)"/>
  <param name="BII2-T19-R009" value="count(cac:SellerSupplierParty) &lt;= 1"/>
  <param name="BII2-T19-R010" value="(cac:PartyName/cbc:Name) or (cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T19-R011" value="(cac:PartyName/cbc:Name) or (cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T19-R012" value="(cac:Party/cac:PartyName/cbc:Name) or (cac:Party/cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T19-R013" value="(cac:Party/cac:PartyName/cbc:Name) or (cac:Party/cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T19-R015" value="number(cbc:PriceAmount) &gt;=0"/>
  <param name="BII2-T19-R017" value="not(cac:LineValidityPeriod) or ((cac:LineValidityPeriod/cbc:StartDate and cac:LineValidityPeriod/cbc:EndDate) and (number(translate(cac:LineValidityPeriod/cbc:StartDate,'-','')) &gt;= number(translate(/ubl:Catalogue/cac:ValidityPeriod/cbc:StartDate,'-',''))) and  (number(translate(cac:LineValidityPeriod/cbc:EndDate,'-','')) &lt;= number(translate(/ubl:Catalogue/cac:ValidityPeriod/cbc:EndDate,'-',''))))"/>
  <param name="BII2-T19-R018" value="not (cac:UsabilityPeriod) or ((//cac:UsabilityPeriod/cbc:StartDate and //cac:UsabilityPeriod/cbc:EndDate) and (number(translate(//cac:UsabilityPeriod/cbc:StartDate,'-','')) &gt;= number(translate(//cac:LineValidityPeriod/cbc:StartDate,'-',''))) and  (number(translate(//cac:UsabilityPeriod/cbc:EndDate,'-','')) &lt;= number(translate(//cac:LineValidityPeriod/cbc:EndDate,'-',''))))"/>
  <param name="BII2-T19-R019" value="(cbc:Name)"/>
  <param name="BII2-T19-R020" value="(cac:StandardItemIdentification/cbc:ID) or  (cac:SellersItemIdentification/cbc:ID)"/>
  <param name="BII2-T19-R021" value="(cbc:ID/@schemeID)"/>
  <param name="BII2-T19-R022" value="(@listID)"/>
  <param name="BII2-T19-R023" value="(cac:CatalogueLine)"/>
  <param name="BII2-T19-R024" value="(cbc:ID)"/>
  <param name="BII2-T19-R026" value="not(cbc:MinimumOrderQuantity) or number(cbc:MinimumOrderQuantity) &gt;= 0"/>
  <param name="BII2-T19-R027" value="(cbc:Value)"/>
  <param name="BII2-T19-R029" value="not(cbc:MaximumOrderQuantity) or number(cbc:MaximumOrderQuantity) &gt;= 0"/>
  <param name="BII2-T19-R030" value="not(cbc:MinimumOrderQuantity) or number(cbc:MinimumOrderQuantity) &gt;= 0"/>
  <param name="BII2-T19-R031" value="not(cbc:MaximumOrderQuantity) or not(cbc:MinimumOrderQuantity) or number(cbc:MaximumOrderQuantity) &gt;= number(cbc:MinimumOrderQuantity)"/>
  <param name="Catalogue_Provider" value="//cac:ProviderParty"/>
  <param name="Catalogue_Receiver" value="//cac:ReceiverParty"/>
  <param name="Catalogue_Customer" value="//cac:ContractorCustomerParty"/>
  <param name="Catalogue_Supplier_Address" value="//cac:SellerSupplierParty/cac:Party/cac:PostalAddress"/>
  <param name="Item_Price" value="//cac:RequiredItemLocationQuantity/cac:Price"/>
  <param name="Catalogue_Line" value="//cac:CatalogueLine"/>
  <param name="Item_Property" value="//cac:AdditionalItemProperty"/>
  <param name="Validity_Period" value="//cac:ValidityPeriod"/>
  <param name="Catalogue_Supplier" value="//cac:SellerSupplierParty"/>
  <param name="Catalogue" value="/ubl:Catalogue"/>
  <param name="Item_Standard" value="//cac:StandardItemIdentification"/>
  <param name="Item_Commodity" value="//cac:CommodityClassification/cbc:ItemClassificationCode"/>
  <param name="Item" value="//cac:Item"/>
</pattern>
