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
<!-- Abstract rules for T71 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T71">
  <rule context="$Document_Type_Code">
    <assert test="$EUGEN-T71-R001" flag="fatal" id="EUGEN-T71-R001">[EUGEN-T71-R001]-An document type code MUST have a list identifier attribute 'UNCL1001'.</assert>
  </rule>
  <rule context="$Response_Code">
    <assert test="$EUGEN-T71-R002" flag="fatal" id="EUGEN-T71-R002">[EUGEN-T71-R002]-A response code MUST have a list identifier attribute 'UNCL4343'.</assert>
  </rule>
  <rule context="$Issue_Type_Code">
    <assert test="$EUGEN-T71-R003" flag="fatal" id="EUGEN-T71-R003">[EUGEN-T71-R003]-A status reason code MUST have a list identifier attribute &#8216;PEPPOLSTATUS'</assert>
  </rule>
  <rule context="$Endpoint">
    <assert test="$EUGEN-T71-R004" flag="fatal" id="EUGEN-T71-R004">[EUGEN-T71-R004]-An endpoint identifier MUST have a scheme identifier attribute.</assert>
  </rule>
  <rule context="$Party_Identifier">
    <assert test="$EUGEN-T71-R005" flag="fatal" id="EUGEN-T71-R005">[EUGEN-T71-R005]-A party identifier MUST have a scheme identifier attribute.</assert>
  </rule>
</pattern>