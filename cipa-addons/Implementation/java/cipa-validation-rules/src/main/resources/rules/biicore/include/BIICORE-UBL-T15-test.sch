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
<!--Schematron tests for binding UBL and transaction T15-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T15" id="UBL-T15">
  <param name="BIICORE-T15-R000" value="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0')" />
  <param name="BIICORE-T15-R001" value="not(count(//*[not(text())]) > 0)" />
  <param name="BIICORE-T15-R002" value="(not(cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R003" value="(not(cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R004" value="(not(cbc:IssueTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R005" value="(not(cbc:TaxCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R006" value="(not(cbc:PricingCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R007" value="(not(cbc:PaymentCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R008" value="(not(cbc:PaymentAlternativeCurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R009" value="(not(cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R010" value="(not(cbc:LineCountNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R011" value="(not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R012" value="(not(cac:DespatchDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R013" value="(not(cac:ReceiptDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R014" value="(not(cac:OriginatorDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R015" value="(not(cac:Signature) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R016" value="(not(cac:BuyerCustomerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R017" value="(not(cac:SellerSupplierParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R018" value="(not(cac:TaxRepresentativeParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R019" value="(not(cac:DeliveryTerms) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R020" value="(not(cac:PrepaidPayment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R021" value="(not(cac:TaxExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R022" value="(not(cac:PricingExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R023" value="(not(cac:PaymentExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R024" value="(not(cac:PaymentAlternativeExchangeRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R025" value="(not(cac:InvoicePeriod/cbc:StartTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R026" value="(not(cac:InvoicePeriod/cbc:EndTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R027" value="(not(cac:InvoicePeriod/cbc:DurationMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R028" value="(not(cac:InvoicePeriod/cbc:Description) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R029" value="(not(cac:InvoicePeriod/cbc:DescriptionCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R030" value="(not(cac:OrderReference/cbc:SalesOrderID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R031" value="(not(cac:OrderReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R032" value="(not(cac:OrderReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R033" value="(not(cac:OrderReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R034" value="(not(cac:OrderReference/cbc:IssueTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R035" value="(not(cac:OrderReference/cbc:CustomerReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R036" value="(not(cac:OrderReference/cac:DocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R037" value="(not(cac:ContractDocumentReference/cbc:CooyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R038" value="(not(cac:ContractDocumentReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R039" value="(not(cac:ContractDocumentReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R040" value="(not(cac:ContractDocumentReference/cbc:DocumentTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R041" value="(not(cac:ContractDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R042" value="(not(cac:ContractDocumentReference/cac:Attachment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R043" value="(not(cac:AdditionalDocumentReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R044" value="(not(cac:AdditionalDocumentReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R045" value="(not(cac:AdditionalDocumentReference/cbc:IssueDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R046" value="(not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R047" value="(not(cac:AdditionalDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R048" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R049" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R050" value="(not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R051" value="(not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R052" value="(not(cac:AccountingSupplierParty/cbc:AdditionalAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R053" value="(not(cac:AccountingSupplierParty/cbc:DataSendingCapability) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R054" value="(not(cac:AccountingSupplierParty/cac:DespatchContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R055" value="(not(cac:AccountingSupplierParty/cac:AccountingContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R056" value="(not(cac:AccountingSupplierParty/cac:SellerContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R057" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R058" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R059" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R060" value="(not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R061" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R062" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R063" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R064" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R065" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R066" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R067" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R068" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R069" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R070" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R071" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R072" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R073" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R074" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R075" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R076" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R077" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R078" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R079" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R080" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R081" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R082" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R083" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R084" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R085" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R086" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R087" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R088" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R089" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R090" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R091" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R092" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R093" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R094" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R095" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R096" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R097" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R098" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R099" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R100" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R101" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R102" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R103" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R104" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R105" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R106" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R107" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R108" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R109" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R110" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R111" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R112" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R113" value="(not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R114" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R115" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R116" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R117" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R118" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R119" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R120" value="(not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R121" value="(not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R122" value="(not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R123" value="(not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R124" value="(not(cac:AccountingCustomerParty/cbc:AdditionalAccountID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R125" value="(not(cac:AccountingCustomerParty/cac:DeliveryContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R126" value="(not(cac:AccountingCustomerParty/cac:AccountingContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R127" value="(not(cac:AccountingCustomerParty/cac:BuyerContact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R128" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R129" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R130" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R131" value="(not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R132" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R133" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R134" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R135" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R136" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R137" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R138" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R139" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R140" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R141" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R142" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R143" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R144" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R145" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R146" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R147" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R148" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R149" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R150" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R151" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R152" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R153" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R154" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R155" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R156" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R157" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R158" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R159" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R160" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R161" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R162" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R163" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R164" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R165" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R166" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R167" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R168" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R169" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R170" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R171" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R172" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R173" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R174" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R175" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R176" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R177" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R178" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R179" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R180" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R181" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R182" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R183" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R184" value="(not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R185" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R186" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R187" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R188" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R189" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R190" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R191" value="(not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R192" value="(not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R193" value="(not(cac:PayeeParty/cbc:MarkCareIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R194" value="(not(cac:PayeeParty/cbc:MarkAttentionIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R195" value="(not(cac:PayeeParty/cbc:WebsiteURI) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R196" value="(not(cac:PayeeParty/cbc:LogoReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R197" value="(not(cac:PayeeParty/cac:Language) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R198" value="(not(cac:PayeeParty/cac:PostalAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R199" value="(not(cac:PayeeParty/cac:PhysicalLocation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R200" value="(not(cac:PayeeParty/cac:PartyTaxScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R201" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R202" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R203" value="(not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R204" value="(not(cac:PayeeParty/cac:Contact) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R205" value="(not(cac:PayeeParty/cac:Person) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R206" value="(not(cac:PayeeParty/cac:AgentParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R207" value="(not(cac:Delivery/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R208" value="(not(cac:Delivery/cbc:Quantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R209" value="(not(cac:Delivery/cbc:MinimumQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R210" value="(not(cac:Delivery/cbc:MaximumQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R211" value="(not(cac:Delivery/cbc:ActualDeliveryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R212" value="(not(cac:Delivery/cbc:LatestDeliveryDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R213" value="(not(cac:Delivery/cbc:LatestDeliveryTime) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R214" value="(not(cac:Delivery/cbc:TrackingID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R215" value="(not(cac:Delivery/cac:DeliveryAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R216" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:Description) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R217" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R218" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R219" value="(not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R220" value="(not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R221" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R222" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R223" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R224" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R225" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R226" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R227" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R228" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R229" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R230" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R231" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R232" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R233" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R234" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R235" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R236" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R237" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R238" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R239" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R240" value="(not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R241" value="(not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R242" value="(not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R243" value="(not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R244" value="(not(cac:Delivery/cac:DeliveryLocation/cac:Despatch) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R245" value="(not(cac:PaymentMeans/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R246" value="(not(cac:PaymentMeans/cbc:InstructionID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R247" value="(not(cac:PaymentMeans/cbc:InstructionNote) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R248" value="(not(cac:PaymentMeans/cac:CardAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R249" value="(not(cac:PaymentMeans/cac:PayerFinancialAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R250" value="(not(cac:PaymentMeans/cac:CreditAccount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R251" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R252" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R253" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R254" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R255" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R256" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R257" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R258" value="(not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R259" value="(not(cac:PaymentTerms/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R260" value="(not(cac:PaymentTerms/cbc:PaymentMeansID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R261" value="(not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R262" value="(not(cac:PaymentTerms/cbc:ReferenceEventCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R263" value="(not(cac:PaymentTerms/cbc:SettlementDiscountPercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R264" value="(not(cac:PaymentTerms/cbc:PenaltySurchargePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R265" value="(not(cac:PaymentTerms/cbc:Amount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R266" value="(not(cac:PaymentTerms/cac:SettlementPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R267" value="(not(cac:PaymentTerms/cac:PenaltyPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R268" value="(not(cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R269" value="(not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R270" value="(not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R271" value="(not(cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R272" value="(not(cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R273" value="(not(cac:AllowanceCharge/cbc:BaseAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R274" value="(not(cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R275" value="(not(cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R276" value="(not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R277" value="(not(cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R278" value="(not(cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R279" value="(not(cac:TaxTotal/cbc:RoundingAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R280" value="(not(cac:TaxTotal/cbc:TaxEvidenceIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R281" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R282" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R283" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R284" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R285" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R286" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R287" value="(not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R288" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R289" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R290" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R291" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R292" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R293" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R294" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R295" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R296" value="(not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R297" value="(not(cac:InvoiceLine/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R298" value="(not(cac:InvoiceLine/cbc:TaxPointDate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R299" value="(not(cac:InvoiceLine/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R300" value="(not(cac:InvoiceLine/cbc:FreeOfChargeIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R301" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R302" value="(not(cac:InvoiceLine/cac:DespatchLineReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R303" value="(not(cac:InvoiceLine/cac:ReceiptLineReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R304" value="(not(cac:InvoiceLine/cac:BillingReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R305" value="(not(cac:InvoiceLine/cac:DocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R306" value="(not(cac:InvoiceLine/cac:PricingReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R307" value="(not(cac:InvoiceLine/cac:OriginatorParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R308" value="(not(cac:InvoiceLine/cac:Delivery) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R309" value="(not(cac:InvoiceLine/cac:PaymentTerms) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R310" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R311" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R312" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R313" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R314" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R315" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R316" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R317" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R318" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R319" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R320" value="(not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R321" value="(not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R322" value="(not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R323" value="(not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R324" value="(not(cac:InvoiceLine/cac:Item/cbc:PackQuantity) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R325" value="(not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R326" value="(not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R327" value="(not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R328" value="(not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R329" value="(not(cac:InvoiceLine/cac:Item/cbc:Keyword) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R330" value="(not(cac:InvoiceLine/cac:Item/cbc:BrandName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R331" value="(not(cac:InvoiceLine/cac:Item/cbc:ModelName) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R332" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R333" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R334" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R335" value="(not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R336" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R337" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R338" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R339" value="(not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R340" value="(not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R341" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R342" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R343" value="(not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R344" value="(not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R345" value="(not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R346" value="(not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R347" value="(not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R348" value="(not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R349" value="(not(cac:InvoiceLine/cac:Item/cac:OriginCountry) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R350" value="(not(cac:InvoiceLine/cac:Item/cac:TransactionConditions) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R351" value="(not(cac:InvoiceLine/cac:Item/cac:HazardousItem) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R352" value="(not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R353" value="(not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R354" value="(not(cac:InvoiceLine/cac:Item/cac:OriginAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R355" value="(not(cac:InvoiceLine/cac:Item/cac:ItemInstance) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R356" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R357" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R358" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R359" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R360" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R361" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R362" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R363" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R364" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R365" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R366" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R367" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R368" value="(not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R369" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R370" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R371" value="(not(cac:InvoiceLine/cac:Price/cbc:PriceType) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R372" value="(not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R373" value="(not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R374" value="(not(cac:InvoiceLine/cac:Price/cac:PriceList) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R375" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R376" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R377" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R378" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R379" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R380" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R381" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)&#10; and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R382" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R383" value="(not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R384" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R385" value="(not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R386" value="(not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R387" value="(not(cac:BillingReference/cac:SelfBilledCreditNoteDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R388" value="(not(cac:BillingReference/cac:DebitNoteDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R389" value="(not(cac:BillingReference/cac:ReminderDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R390" value="(not(cac:BillingReference/cac:AdditionalDocumentReference) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R391" value="(not(cac:BillingReference/cac:BillingReferenceLine) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R392" value="(not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R393" value="(not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R394" value="(not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R395" value="(not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:CopyIndicator) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R396" value="(not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:XPath) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R397" value="(not(cac:BillingReference/cac:CreditNoteDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and $Prerequisite1) or not ($Prerequisite1)" />
  <param name="BIICORE-T15-R398" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R399" value="(count(cac:PartyName)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R400" value="(count(cac:PartyTaxScheme)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R401" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R402" value="(count(cac:PartyName)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R403" value="(count(cac:PostalAddress)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R404" value="(count(cac:PartyTaxScheme)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R405" value="(count(cac:TaxTotal)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R406" value="(count(cac:Price)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R407" value="(count(cbc:Description)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R408" value="(count(cbc:Name)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R409" value="(count(cac:ClassifiedTaxCategory)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R410" value="(count(cac:AllowanceCharge)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R411" value="(count(cbc:TaxExclusiveAmount)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R412" value="(count(cbc:TaxInclusiveAmount)=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R413" value="(count(cac:PartyIdentification)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R414" value="(count(cac:PartyName)&lt;=1 and $Prerequisite2) or not ($Prerequisite2)" />
  <param name="BIICORE-T15-R415" value="(count(cbc:ID)=1 and $Prerequisite2) or not ($Prerequisite2)" />
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
