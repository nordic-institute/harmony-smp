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
<!-- Data binding to UBL syntax for T76 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T76" id="UBL-T76">
  <param name="BII2-T76-R001" value="(cbc:CustomizationID)"/>
  <param name="BII2-T76-R002" value="(cbc:ProfileID)"/>
  <param name="BII2-T76-R003" value="(cac:LineItem/cbc:ID)"/>
  <param name="BII2-T76-R004" value="(cbc:IssueDate)"/>
  <param name="BII2-T76-R006" value="(cbc:ID)"/>
  <param name="BII2-T76-R021" value="(cac:Party/cac:PartyName/cbc:Name) or (cac:Party/cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T76-R022" value="(cac:Party/cac:PartyName/cbc:Name) or (cac:Party/cac:PartyIdentification/cbc:ID)"/>
  <param name="BII2-T76-R032" value="(cac:OrderDocumentReference/cbc:ID)"/>
  <param name="BII2-T76-R033" value="(//cbc:ResponseCode)"/>
  <param name="BII2-T76-R034" value="(//cac:OrderLineReference/cbc:LineID)"/>
  <param name="order_response_line" value="//cac:OrderLine"/>
  <param name="order_response" value="/ubl:OrderResponse"/>
  <param name="Buyer" value="//cac:BuyerCustomerParty"/>
  <param name="Seller" value="//cac:SellerSupplierParty"/>
</pattern>
