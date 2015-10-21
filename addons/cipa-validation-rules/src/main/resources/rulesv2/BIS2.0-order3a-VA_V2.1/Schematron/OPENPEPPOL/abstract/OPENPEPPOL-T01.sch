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
<!-- Abstract rules for T01 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T01">
  <rule context="$Country_identification">
    <assert test="$EUGEN-T01-R015" flag="fatal" id="EUGEN-T01-R015">[EUGEN-T01-R015]-A country identification code MUST have a list identifier attribute &#8220;ISO3166-1:Alpha2&#8221;</assert>
  </rule>
  <rule context="$Document_Currency">
    <assert test="$EUGEN-T01-R014" flag="fatal" id="EUGEN-T01-R014">[EUGEN-T01-R014]-A document currency code MUST have a list identifier attribute &#8220;ISO4217&#8221;</assert>
  </rule>
  <rule context="$Endpoint">
    <assert test="$EUGEN-T01-R011" flag="fatal" id="EUGEN-T01-R011">[EUGEN-T01-R011]-An endpoint identifier MUST have a scheme identifier attribute</assert>
  </rule>
  <rule context="$Order_Type">
    <assert test="$EUGEN-T01-R013" flag="fatal" id="EUGEN-T01-R013">[EUGEN-T01-R013]-An order type code MUST have a list identifier attribute &#8220;UNCL1001&#8221;</assert>
  </rule>
  <rule context="$Party_Identifier">
    <assert test="$EUGEN-T01-R012" flag="fatal" id="EUGEN-T01-R012">[EUGEN-T01-R012]-A party identifier MUST have a scheme identifier attribute</assert>
  </rule>
  <rule context="$Tax_Category_Identifier">
    <assert test="$EUGEN-T01-R017" flag="fatal" id="EUGEN-T01-R017">[EUGEN-T01-R017]-A tax category identifier MUST have a scheme identifier attribute &#8220;UNCL5305&#8221;</assert>
  </rule>
  <rule context="$Unit_Code">
    <assert test="$EUGEN-T01-R016" flag="fatal" id="EUGEN-T01-R016">[EUGEN-T01-R016]-A unit code attribute MUST have a unit code list identifier attribute &#8220;UNECERec20&#8221;</assert>
  </rule>
</pattern>
