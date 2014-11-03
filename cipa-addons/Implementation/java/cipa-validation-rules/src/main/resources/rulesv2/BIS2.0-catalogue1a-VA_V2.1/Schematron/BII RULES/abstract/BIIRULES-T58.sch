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
<!-- Abstract rules for T58 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T58">
  <rule context="$catalogue_rejection">
    <assert test="$BII2-T58-R001" flag="fatal" id="BII2-T58-R001">[BII2-T58-R001]-A catalogue rejection MUST have a profile identifier</assert>
    <assert test="$BII2-T58-R002" flag="fatal" id="BII2-T58-R002">[BII2-T58-R002]-A catalogue rejection MUST have a customization identifier</assert>
    <assert test="$BII2-T58-R003" flag="fatal" id="BII2-T58-R003">[BII2-T58-R003]-A catalogue rejection MUST contain the date of issue</assert>
    <assert test="$BII2-T58-R004" flag="fatal" id="BII2-T58-R004">[BII2-T58-R004]-A catalogue rejection MUST contain the response identifier</assert>
    <assert test="$BII2-T58-R005" flag="fatal" id="BII2-T58-R005">[BII2-T58-R005]-The party sending the catalogue rejection MUST be specified</assert>
    <assert test="$BII2-T58-R006" flag="fatal" id="BII2-T58-R006">[BII2-T58-R006]-The party receiving the catalogue rejection MUST be specified</assert>
    <assert test="$BII2-T58-R009" flag="fatal" id="BII2-T58-R009">[BII2-T58-R009]-A catalogue rejection MUST contain a response decision</assert>
    <assert test="$BII2-T58-R010" flag="fatal" id="BII2-T58-R010">[BII2-T58-R010]-A catalogue rejection MUST contain a document reference</assert>
  </rule>
  <rule context="$Receiving_Party">
    <assert test="$BII2-T58-R008" flag="fatal" id="BII2-T58-R008">[BII2-T58-R008]-A catalogue rejection receiving party MUST contain the full name or an identifier</assert>
  </rule>
  <rule context="$Sending_Party">
    <assert test="$BII2-T58-R007" flag="fatal" id="BII2-T58-R007">[BII2-T58-R007]-A catalogue rejection sending party MUST contain the full name or an identifier</assert>
  </rule>
</pattern>
