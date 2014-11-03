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
  <param name="EUGEN-T19-R043" value="@schemeID"/>
  <param name="EUGEN-T19-R044" value="@schemeID"/>
  <param name="EUGEN-T19-R045" value="@listID = 'ACTIONCODE:BII2'"/>
  <param name="EUGEN-T19-R046" value="@listID = 'ACTIONCODE:PEPPOL'"/>
  <param name="EUGEN-T19-R047" value="@listID = 'ISO3166-1:Alpha2'"/>
  <param name="EUGEN-T19-R048" value="not(attribute::unitCode) or (attribute::unitCode and attribute::unitCodeListID = 'UNECERec20')"/>
  <param name="EUGEN-T19-R049" value="@schemeID  = 'UNCL5305'"/>
  <param name="EUGEN-T19-R050" value="@listID = 'GS17009:PEPPOL'"/>
  <param name="EUGEN-T19-R051" value="@listID = 'UNCL8273'"/>
  <param name="EUGEN-T19-R053" value="@schemeID  = 'UNCL6313'"/>
  <param name="Endpoint" value="//cbc:EndpointID"/>
  <param name="Party_Identifier" value="//cac:PartyIdentification/cbc:ID"/>
  <param name="Line_level_action_code" value="//cac:CatalogueLine/cbc:ActionCode"/>
  <param name="Header_level_action_code" value="/ubl:Catalogue/cbc:ActionCode"/>
  <param name="Country_Identification_Code" value="//cac:Country/cbc:IdentificationCode"/>
  <param name="Unit_Code" value="//*[contains(name(),'Quantity')]"/>
  <param name="Classified_Tax_Category_Identifier" value="//cac:ClassifiedTaxCategory/cbc:ID"/>
  <param name="Package_Level_Code" value="//cbc:PackageLevelCode"/>
  <param name="UNDG_Code" value="//cbc:UNDGCode"/>
  <param name="Attribute_identifier_scheme" value="//cbc:AttributeID"/>
</pattern>
