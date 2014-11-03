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
<!-- Abstract rules for T19 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T19">
  <rule context="$Attribute_identifier_scheme">
    <assert test="$EUGEN-T19-R053" flag="fatal" id="EUGEN-T19-R053">[EUGEN-T19-R053]-An attribute identifier MUST have an scheme identifier &#8220;UNCL6313&#8221; </assert>
  </rule>
  <rule context="$Classified_Tax_Category_Identifier">
    <assert test="$EUGEN-T19-R049" flag="fatal" id="EUGEN-T19-R049">[EUGEN-T19-R049]-A classified tax category identifier MUST have a scheme identifier attribute &#8220;UNCL5305&#8221;</assert>
  </rule>
  <rule context="$Country_Identification_Code">
    <assert test="$EUGEN-T19-R047" flag="fatal" id="EUGEN-T19-R047">[EUGEN-T19-R047]-A country identification code MUST have a list identifier attribute &#8220;ISO3166-1:Alpha2&#8221;</assert>
  </rule>
  <rule context="$Endpoint">
    <assert test="$EUGEN-T19-R043" flag="fatal" id="EUGEN-T19-R043">[EUGEN-T19-R043]-An endpoint identifier MUST have a scheme identifier attribute</assert>
  </rule>
  <rule context="$Header_level_action_code">
    <assert test="$EUGEN-T19-R046" flag="fatal" id="EUGEN-T19-R046">[EUGEN-T19-R046]-A catalogue header action code MUST have a list identifier attribute &#8220;ACTIONCODE:PEPPOL&#8221;</assert>
  </rule>
  <rule context="$Line_level_action_code">
    <assert test="$EUGEN-T19-R045" flag="fatal" id="EUGEN-T19-R045">[EUGEN-T19-R045]-A catalogue line action code MUST have a list identifier attribute &#8220;ACTIONCODE:BII2&#8221;</assert>
  </rule>
  <rule context="$Package_Level_Code">
    <assert test="$EUGEN-T19-R050" flag="fatal" id="EUGEN-T19-R050">[EUGEN-T19-R050]-A package level code MUST have a list identifier attribute &#8220;GS17009:PEPPOL&#8220;</assert>
  </rule>
  <rule context="$Party_Identifier">
    <assert test="$EUGEN-T19-R044" flag="fatal" id="EUGEN-T19-R044">[EUGEN-T19-R044]-A party identifier MUST have a scheme identifier attribute</assert>
  </rule>
  <rule context="$UNDG_Code">
    <assert test="$EUGEN-T19-R051" flag="fatal" id="EUGEN-T19-R051">[EUGEN-T19-R051]-A UNDG code MUST have a list identifier attribute &#8220;UNCL8273&#8221;</assert>
  </rule>
  <rule context="$Unit_Code">
    <assert test="$EUGEN-T19-R048" flag="fatal" id="EUGEN-T19-R048">[EUGEN-T19-R048]-A unit code attribute MUST have a unit code list identifier attribute &#8220;UNECERec20&#8221;</assert>
  </rule>
</pattern>
