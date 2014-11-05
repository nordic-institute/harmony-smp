<?xml version="1.0" encoding="UTF-8"?>
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
<!--This file is generated automatically! Do NOT edit!-->
<!--Schematron tests for binding UBL and transaction T10-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T10" id="UBL-T10">
  <param name="BIICORE-T10-R000" value="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0')" />
  <param name="BIICORE-T10-R001" value="not(count(//*[not(text())]) > 0)" />
  <param name="BIICORE-T10-R002" value="(not(cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R003" value="(not(cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R004" value="(not(cbc:IssueTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R005" value="(not(cbc:TaxCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R006" value="(not(cbc:PricingCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R007" value="(not(cbc:PaymentCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R008" value="(not(cbc:PaymentAlternativeCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R009" value="(not(cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R010" value="(not(cbc:LineCountNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R011" value="(not(cac:BillingReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R012" value="(not(cac:DespatchDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R013" value="(not(cac:ReceiptDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R014" value="(not(cac:OriginatorDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R015" value="(not(cac:Signature) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R016" value="(not(cac:BuyerCustomerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R017" value="(not(cac:SellerSupplierParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R018" value="(not(cac:TaxRepresentativeParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R019" value="(not(cac:DeliveryTerms) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R020" value="(not(cac:PrepaidPayment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R021" value="(not(cac:TaxExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R022" value="(not(cac:PricingExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R023" value="(not(cac:PaymentExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R024" value="(not(cac:PaymentAlternativeExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R025" value="(not(cac:InvoicePeriod/cbc:StartTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R026" value="(not(cac:InvoicePeriod/cbc:EndTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R027" value="(not(cac:InvoicePeriod/cbc:DurationMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R028" value="(not(cac:InvoicePeriod/cbc:Description) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R029" value="(not(cac:InvoicePeriod/cbc:DescriptionCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R030" value="(not(cac:OrderReference/cbc:SalesOrderID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R031" value="(not(cac:OrderReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R032" value="(not(cac:OrderReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R033" value="(not(cac:OrderReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R034" value="(not(cac:OrderReference/cbc:IssueTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R035" value="(not(cac:OrderReference/cbc:CustomerReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R036" value="(not(cac:OrderReference/cac:DocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R037" value="(not(cac:ContractDocumentReference/cbc:CooyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R038" value="(not(cac:ContractDocumentReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R039" value="(not(cac:ContractDocumentReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R040" value="(not(cac:ContractDocumentReference/cbc:DocumentTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R041" value="(not(cac:ContractDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R042" value="(not(cac:ContractDocumentReference/cac:Attachment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R043" value="(not(cac:AdditionalDocumentReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R044" value="(not(cac:AdditionalDocumentReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R045" value="(not(cac:AdditionalDocumentReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R046" value="(not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R047" value="(not(cac:AdditionalDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R048" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R049" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R050" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R051" value="(not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R052" value="(not(cac:AccountingSupplierParty/cbc:AdditionalAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R053" value="(not(cac:AccountingSupplierParty/cbc:DataSendingCapability) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R054" value="(not(cac:AccountingSupplierParty/cac:DespatchContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R055" value="(not(cac:AccountingSupplierParty/cac:AccountingContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R056" value="(not(cac:AccountingSupplierParty/cac:SellerContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R057" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R058" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R059" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R060" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R061" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R062" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R063" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R064" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R065" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R066" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R067" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R068" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R069" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R070" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R071" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R072" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R073" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R074" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R075" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R076" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R077" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R078" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R079" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R080" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R081" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R082" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R083" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R084" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R085" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R086" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R087" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R088" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R089" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R090" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R091" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R092" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R093" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R094" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R095" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R096" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R097" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R098" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R099" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R100" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R101" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R102" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R103" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R104" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R105" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R106" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R107" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R108" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R109" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R110" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R111" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R112" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R113" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R114" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R115" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R116" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R117" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R118" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R119" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R120" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R121" value="(not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R122" value="(not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R123" value="(not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R124" value="(not(cac:AccountingCustomerParty/cbc:AdditionalAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R125" value="(not(cac:AccountingCustomerParty/cac:DeliveryContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R126" value="(not(cac:AccountingCustomerParty/cac:AccountingContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R127" value="(not(cac:AccountingCustomerParty/cac:BuyerContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R128" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R129" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R130" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R131" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R132" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R133" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R134" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R135" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R136" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R137" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R138" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R139" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R140" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R141" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R142" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R143" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R144" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R145" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R146" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R147" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R148" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R149" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R150" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R151" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R152" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R153" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R154" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R155" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R156" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R157" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R158" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R159" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R160" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R161" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R162" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R163" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R164" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R165" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R166" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R167" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R168" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R169" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R170" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R171" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R172" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R173" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R174" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R175" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R176" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R177" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R178" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R179" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R180" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R181" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R182" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R183" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R184" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R185" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R186" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R187" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R188" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R189" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R190" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R191" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R192" value="(not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R193" value="(not(cac:PayeeParty/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R194" value="(not(cac:PayeeParty/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R195" value="(not(cac:PayeeParty/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R196" value="(not(cac:PayeeParty/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R197" value="(not(cac:PayeeParty/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R198" value="(not(cac:PayeeParty/cac:PostalAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R199" value="(not(cac:PayeeParty/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R200" value="(not(cac:PayeeParty/cac:PartyTaxScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R201" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R202" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R203" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R204" value="(not(cac:PayeeParty/cac:Contact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R205" value="(not(cac:PayeeParty/cac:Person) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R206" value="(not(cac:PayeeParty/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R207" value="(not(cac:Delivery/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R208" value="(not(cac:Delivery/cbc:Quantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R209" value="(not(cac:Delivery/cbc:MinimumQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R210" value="(not(cac:Delivery/cbc:MaximumQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R211" value="(not(cac:Delivery/cbc:ActualDeliveryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R212" value="(not(cac:Delivery/cbc:LatestDeliveryDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R213" value="(not(cac:Delivery/cbc:LatestDeliveryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R214" value="(not(cac:Delivery/cbc:TrackingID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R215" value="(not(cac:Delivery/cac:DeliveryAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R216" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:Description) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R217" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R218" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R219" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R220" value="(not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R221" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R222" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R223" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R224" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R225" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R226" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R227" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R228" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R229" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R230" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R231" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R232" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R233" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R234" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R235" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R236" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R237" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R238" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R239" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R240" value="(not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R241" value="(not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R242" value="(not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R243" value="(not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R244" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Despatch) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R245" value="(not(cac:PaymentMeans/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R246" value="(not(cac:PaymentMeans/cbc:InstructionID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R247" value="(not(cac:PaymentMeans/cbc:InstructionNote) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R248" value="(not(cac:PaymentMeans/cac:CardAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R249" value="(not(cac:PaymentMeans/cac:PayerFinancialAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R250" value="(not(cac:PaymentMeans/cac:CreditAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R251" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R252" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R253" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R254" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R255" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R256" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R257" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R258" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R259" value="(not(cac:PaymentTerms/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R260" value="(not(cac:PaymentTerms/cbc:PaymentMeansID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R261" value="(not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R262" value="(not(cac:PaymentTerms/cbc:ReferenceEventCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R263" value="(not(cac:PaymentTerms/cbc:SettlementDiscountPercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R264" value="(not(cac:PaymentTerms/cbc:PenaltySurchargePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R265" value="(not(cac:PaymentTerms/cbc:Amount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R266" value="(not(cac:PaymentTerms/cac:SettlementPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R267" value="(not(cac:PaymentTerms/cac:PenaltyPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R268" value="(not(cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R269" value="(not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R270" value="(not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R271" value="(not(cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R272" value="(not(cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R273" value="(not(cac:AllowanceCharge/cbc:BaseAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R274" value="(not(cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R275" value="(not(cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R276" value="(not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R277" value="(not(cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R278" value="(not(cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R279" value="(not(cac:TaxTotal/cbc:RoundingAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R280" value="(not(cac:TaxTotal/cbc:TaxEvidenceIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R281" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R282" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R283" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R284" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R285" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R286" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R287" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R288" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R289" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R290" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R291" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R292" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R293" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R294" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R295" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R296" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R297" value="(not(cac:InvoiceLine/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R298" value="(not(cac:InvoiceLine/cbc:TaxPointDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R299" value="(not(cac:InvoiceLine/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R300" value="(not(cac:InvoiceLine/cbc:FreeOfChargeIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R301" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R302" value="(not(cac:InvoiceLine/cac:DespatchLineReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R303" value="(not(cac:InvoiceLine/cac:ReceiptLineReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R304" value="(not(cac:InvoiceLine/cac:BillingReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R305" value="(not(cac:InvoiceLine/cac:DocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R306" value="(not(cac:InvoiceLine/cac:PricingReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R307" value="(not(cac:InvoiceLine/cac:OriginatorParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R308" value="(not(cac:InvoiceLine/cac:Delivery) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R309" value="(not(cac:InvoiceLine/cac:PaymentTerms) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R310" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R311" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R312" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R313" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R314" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R315" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R316" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R317" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R318" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R319" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R320" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R321" value="(not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R322" value="(not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R323" value="(not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R324" value="(not(cac:InvoiceLine/cac:Item/cbc:PackQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R325" value="(not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R326" value="(not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R327" value="(not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R328" value="(not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R329" value="(not(cac:InvoiceLine/cac:Item/cbc:Keyword) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R330" value="(not(cac:InvoiceLine/cac:Item/cbc:BrandName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R331" value="(not(cac:InvoiceLine/cac:Item/cbc:ModelName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R332" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R333" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R334" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R335" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R336" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R337" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R338" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R339" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R340" value="(not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R341" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R342" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R343" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R344" value="(not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R345" value="(not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R346" value="(not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R347" value="(not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R348" value="(not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R349" value="(not(cac:InvoiceLine/cac:Item/cac:OriginCountry) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R350" value="(not(cac:InvoiceLine/cac:Item/cac:TransactionConditions) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R351" value="(not(cac:InvoiceLine/cac:Item/cac:HazardousItem) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R352" value="(not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R353" value="(not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R354" value="(not(cac:InvoiceLine/cac:Item/cac:OriginAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R355" value="(not(cac:InvoiceLine/cac:Item/cac:ItemInstance) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R356" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R357" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R358" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R359" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R360" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R361" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R362" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R363" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R364" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R365" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R366" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R367" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R368" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R369" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R370" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R371" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceType) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R372" value="(not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R373" value="(not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R374" value="(not(cac:InvoiceLine/cac:Price/cac:PriceList) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R375" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R376" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R377" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R378" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R379" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R380" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R381" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)&#10; and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R382" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R383" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R384" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R385" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R386" value="(not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T10-R387" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R388" value="(count(cac:PartyName)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R389" value="(count(cac:PartyTaxScheme)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R390" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R391" value="(count(cac:PartyName)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R392" value="(count(cac:PostalAddress)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R393" value="(count(cac:PartyTaxScheme)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R394" value="(count(cac:TaxTotal)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R395" value="(count(cac:Price)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R396" value="(count(cbc:Description)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R397" value="(count(cbc:Name)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R398" value="(count(cac:ClassifiedTaxCategory)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R399" value="(count(cac:AllowanceCharge)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R400" value="(count(cbc:TaxExclusiveAmount)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R401" value="(count(cbc:TaxInclusiveAmount)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R402" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R403" value="(count(cac:PartyName)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T10-R404" value="(count(cbc:ID)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="Invoice" value="/ubl:Invoice" />
  <param name="Customer" value="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party" />
  <param name="Supplier" value="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party" />
  <param name="InvoiceLine" value="/ubl:Invoice/cac:InvoiceLine" />
  <param name="Item" value="/ubl:Invoice/cac:InvoiceLine/cac:Item" />
  <param name="Price" value="/ubl:Invoice/cac:InvoiceLine/cac:Price" />
  <param name="Monetary_Total" value="/ubl:Invoice/cac:LegalMonetaryTotal" />
  <param name="Payee" value="/ubl:Invoice/cac:PayeeParty" />
  <param name="Financial_Account" value="/ubl:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount" />
</pattern>
