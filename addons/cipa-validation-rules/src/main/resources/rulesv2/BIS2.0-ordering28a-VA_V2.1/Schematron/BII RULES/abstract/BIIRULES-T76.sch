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
<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for T76 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T76">
  <rule context="$order_response">
    <assert test="$BII2-T76-R001" flag="fatal" id="BII2-T76-R001">[BII2-T76-R001]-A order response MUST have a customization identifier</assert>
    <assert test="$BII2-T76-R002" flag="fatal" id="BII2-T76-R002">[BII2-T76-R002]-A order response MUST have a profile identifier</assert>
  </rule>
  <rule context="$order_response_line">
    <assert test="$BII2-T76-R003" flag="fatal" id="BII2-T76-R003">[BII2-T76-R003]-Each order response line MUST have a document line identifier that is unique within the order response</assert>
  </rule>
  <rule context="$order_response">
    <assert test="$BII2-T76-R004" flag="fatal" id="BII2-T76-R004">[BII2-T76-R004]-A order response MUST have a document issue date</assert>
    <assert test="$BII2-T76-R006" flag="fatal" id="BII2-T76-R006">[BII2-T76-R006]-A order response MUST have a document identifier</assert>
  </rule>
  <rule context="$Buyer">
    <assert test="$BII2-T76-R021" flag="fatal" id="BII2-T76-R021">[BII2-T76-R021]-A order response MUST have the buyer party name or a buyer party identifier</assert>
  </rule>
  <rule context="$Seller">
    <assert test="$BII2-T76-R022" flag="fatal" id="BII2-T76-R022">[BII2-T76-R022]-A order response MUST have the seller party name or a seller party identifier</assert>
  </rule>
  <rule context="$order_response">
    <assert test="$BII2-T76-R032" flag="fatal" id="BII2-T76-R032">[BII2-T76-R032]-A order response MUST have a reference to an order</assert>
    <assert test="$BII2-T76-R033" flag="fatal" id="BII2-T76-R033">[BII2-T76-R033]-A order response MUST have a response code</assert>
  </rule>
  <rule context="$order_response_line">
    <assert test="$BII2-T76-R034" flag="fatal" id="BII2-T76-R034">[BII2-T76-R034]-An order response line MUST contain a reference to its corresponding order line.</assert>
  </rule>
</pattern>
