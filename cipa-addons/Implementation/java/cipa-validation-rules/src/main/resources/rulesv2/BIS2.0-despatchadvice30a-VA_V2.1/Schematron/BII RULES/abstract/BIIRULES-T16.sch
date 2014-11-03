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
<!-- Abstract rules for T16 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T16">
  <rule context="$Consignee_Party">
    <assert test="$BII2-T16-R009" flag="warning" id="BII2-T16-R009">[BII2-T16-R009]-A consignee party SHOULD have the party name or a party identifier</assert>
  </rule>
  <rule context="$despatch_advice">
    <assert test="$BII2-T16-R001" flag="fatal" id="BII2-T16-R001">[BII2-T16-R001]-A despatch advice MUST have a customization identifier</assert>
    <assert test="$BII2-T16-R002" flag="fatal" id="BII2-T16-R002">[BII2-T16-R002]-A despatch advice MUST have a profile identifier</assert>
    <assert test="$BII2-T16-R003" flag="fatal" id="BII2-T16-R003">[BII2-T16-R003]-A despatch advice MUST have a document identifier</assert>
    <assert test="$BII2-T16-R004" flag="fatal" id="BII2-T16-R004">[BII2-T16-R004]-A despatch advice MUST have a document issue date</assert>
    <assert test="$BII2-T16-R005" flag="warning" id="BII2-T16-R005">[BII2-T16-R005]-A despatch advice SHOULD have an order identifier</assert>
    <assert test="$BII2-T16-R006" flag="fatal" id="BII2-T16-R006">[BII2-T16-R006]-A despatch advice MUST have a despatching party</assert>
    <assert test="$BII2-T16-R008" flag="fatal" id="BII2-T16-R008">[BII2-T16-R008]-A despatch advice MUST have a consignee party</assert>
    <assert test="$BII2-T16-R012" flag="fatal" id="BII2-T16-R012">[BII2-T16-R012]-A despatch advice MUST have at least one despatch advice line</assert>
  </rule>
  <rule context="$despatch_advice_Line">
    <assert test="$BII2-T16-R013" flag="fatal" id="BII2-T16-R013">[BII2-T16-R013]-Each despatch advice line MUST have a despatch line identifier that is unique within the despatch advice</assert>
    <assert test="$BII2-T16-R014" flag="warning" id="BII2-T16-R014">[BII2-T16-R014]-Each despatch advice line SHOULD have an order line reference</assert>
    <assert test="$BII2-T16-R016" flag="fatal" id="BII2-T16-R016">[BII2-T16-R016]-Each despatch advice line MUST have an item identifier and/or an item name</assert>
    <assert test="$BII2-T16-R017" flag="warning" id="BII2-T16-R017">[BII2-T16-R017]-Each despatch advice line SHOULD have a delivered quantity</assert>
    <assert test="$BII2-T16-R019" flag="fatal" id="BII2-T16-R019">[BII2-T16-R019]-Each despatch advice line delivered quantity MUST not be negative</assert>
    <assert test="$BII2-T16-R020" flag="warning" id="BII2-T16-R020">[BII2-T16-R020]-Each despatch advice line delivered quantity  SHOULD have an associated unit of measure</assert>
    <assert test="$BII2-T16-R021" flag="warning" id="BII2-T16-R021">[BII2-T16-R021]-An outstanding quantity reason SHOULD be provided if the despatch line contains an outstanding quantity</assert>
  </rule>
  <rule context="$despatch_delivery_address">
    <assert test="$BII2-T16-R010" flag="warning" id="BII2-T16-R010">[BII2-T16-R010]-A despatch delivery address SHOULD have at least all of the following:
- Address line
- City
- Post code
- Country code</assert>
  </rule>
  <rule context="$despatched_shipment">
    <assert test="$BII2-T16-R011" flag="fatal" id="BII2-T16-R011">[BII2-T16-R011]-Shipment identifier MUST be provided if the despatch advice contains shipment information</assert>
  </rule>
  <rule context="$despatching_Party">
    <assert test="$BII2-T16-R007" flag="warning" id="BII2-T16-R007">[BII2-T16-R007]-A despatching party SHOULD have the despatching party name</assert>
  </rule>
  <rule context="$Item">
    <assert test="$BII2-T16-R018" flag="fatal" id="BII2-T16-R018">[BII2-T16-R018]-An item standard identifier MUST have an identification schema (e.g. GTIN)</assert>
  </rule>
</pattern>
