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
<!-- Data binding to UBL syntax for T16 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T16" id="UBL-T16">
  <param name="BII2-T16-R001" value="(cbc:CustomizationID)"/>
  <param name="BII2-T16-R002" value="(cbc:ProfileID)"/>
  <param name="BII2-T16-R003" value="(cbc:ID)"/>
  <param name="BII2-T16-R004" value="(cbc:IssueDate)"/>
  <param name="BII2-T16-R005" value="(cac:OrderReference/cbc:ID)"/>
  <param name="BII2-T16-R006" value="(cac:DespatchSupplierParty)"/>
  <param name="BII2-T16-R007" value="(cac:Party/cac:PartyName/cbc:Name)"/>
  <param name="BII2-T16-R008" value="(cac:DeliveryCustomerParty)"/>
  <param name="BII2-T16-R009" value="(cac:Party/cac:PartyName/cbc:Name) or (cac:Party/cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T16-R010" value="(cbc:StreetName) and (cbc:CityName) and (cbc:PostalZone) and (cac:Country/cbc:IdentificationCode)"/>
  <param name="BII2-T16-R011" value="(cbc:ID)"/>
  <param name="BII2-T16-R012" value="(cac:DespatchLine)"/>
  <param name="BII2-T16-R013" value="(cbc:ID)"/>
  <param name="BII2-T16-R014" value="(cac:OrderLineReference/cbc:LineID)"/>
  <param name="BII2-T16-R016" value="(cac:Item/cbc:Name) or (cac:Item/cac:StandardItemIdentification/cbc:ID) or  (cac:Item/cac:SellersItemIdentification/cbc:ID)"/>
  <param name="BII2-T16-R017" value="(cbc:DeliveredQuantity)"/>
  <param name="BII2-T16-R018" value="(cac:StandardItemIdentification/cbc:ID/@schemeID) or not(cac:StandardItemIdentification)"/>
  <param name="BII2-T16-R019" value="number(cbc:DeliveredQuantity) &gt;= 0"/>
  <param name="BII2-T16-R020" value="(cbc:DeliveredQuantity/@unitCode)"/>
  <param name="BII2-T16-R021" value="((cbc:OutstandingQuantity) and (cbc:OutstandingReason)) or not(cbc:OutstandingQuantity)"/>
  <param name="despatching_Party" value="//cac:DespatchSupplierParty"/>
  <param name="Consignee_Party" value="//cac:DeliveryCustomerParty"/>
  <param name="despatch_delivery_address" value="//cac:DeliveryCustomerParty/cac:Party/cac:PostalAddress"/>
  <param name="despatched_shipment" value="//cac:Shipment"/>
  <param name="despatch_advice_Line" value="//cac:DespatchLine"/>
  <param name="Item" value="//cac:Item"/>
  <param name="despatch_advice" value="/ubl:DespatchAdvice"/>
</pattern>
