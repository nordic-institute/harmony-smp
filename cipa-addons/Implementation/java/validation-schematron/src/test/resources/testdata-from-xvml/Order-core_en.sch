<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (C) 2010 Bundesrechenzentrum GmbH
    http://www.brz.gv.at

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

<!--Order Validation Schematron-->
<schema xmlns:xvml="http://peppol.eu/schemas/xvml/1.0" xmlns:gc="http://docs.oasis-open.org/codelist/ns/genericode/1.0/" xmlns="http://purl.oclc.org/dsdl/schematron">
<title>Business rules for order</title>
<ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
<ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
<pattern id="BiiCoreTrdm001" name="BiiCoreTrdm001 - Order">
	
		
		<rule context="/*/cbc:Note">
			<assert test="@languageID" flag="warning">BII-T01-001: Language SHOULD be defined for Order Note field</assert>
		</rule>
	
		
		<rule context="//cac:OrderLine/cbc:Note">
			<assert test="@languageID" flag="warning">BII-T01-002: Language SHOULD be defined for OrderLine Note field</assert>
		</rule>
		
		
		<rule context="//cac:OriginatorDocumentReference">
			<assert test="(cbc:DocumentType) and (cbc:DocumentType != '')" flag="fatal">BII-T01-003: DocumentType text MUST be given for Originator Document Reference</assert>
		</rule>
		
		
		<rule context="//cac:AdditionalDocumentReference">
			<assert test="(cbc:DocumentType) and (cbc:DocumentType != '')" flag="fatal">BII-T01-004: DocumentType text MUST be given for Additional Document Reference</assert>
		</rule>
		
		
		
		
		
		<rule context="//cac:Contract">
			<assert test="((cbc:ID) and (cbc:ID != '' )) or ((cbc:ContractType) and (cbc:ContractType != '' ))" flag="warning">BII-T01-006: If Contract ID not specified Contract Type text SHOULD be used for Contract Reference</assert>
		</rule>
		
		
		
		<rule context="//cac:BuyerCustomerParty/cac:Party">
			<let value="cac:PartyIdentification/cbc:ID" name="id"/>
<let value="cac:PartyName/cbc:Name" name="name"/>
<assert test="(($id) and ($id != '' )) or (($name) and ($name != '' ))" flag="fatal">BII-T01-007: If buyer customer party ID is not specified, buyer party name is mandatory</assert>
			<let value="cac:PostalAddress/cbc:CityName" name="city"/>
<let value="cac:PostalAddress/cbc:PostalZone" name="zip"/>
<let value="cac:PostalAddress/AddressLine" name="addressline"/>
<assert test="($city and ($city != '' ) and $zip and ($zip != '')) or ($addressline and ($addressline != ''))" flag="warning">BII-T01-012: A customer address SHOULD contain at least city and zip code or address lines</assert>
		</rule>
		
		
		
		<rule context="//cac:SellerSupplierParty/cac:Party">
			<let value="cac:PartyIdentification/cbc:ID" name="id"/>
<let value="cac:PartyName/cbc:Name" name="name"/>
<assert test="(($id) and ($id != '' )) or (($name) and ($name != '' ))" flag="fatal">BII-T01-008: If seller supplier party ID is not specified, supplier party name is mandatory</assert>
			<let value="cac:PostalAddress/cbc:CityName" name="city"/>
<let value="cac:PostalAddress/cbc:PostalZone" name="zip"/>
<let value="cac:PostalAddress/AddressLine" name="addressline"/>
<assert test="($city and ($city != '' ) and $zip and ($zip != '')) or ($addressline and ($addressline != ''))" flag="warning">BII-T01-010: A seller address SHOULD contain at least city and zip code or address lines</assert>
		</rule>
		
		
		<rule context="//cac:Delivery/cac:RequestedDeliveryPeriod">
			<assert test="not((translate(cbc:EndDate, '-', '')) &lt; (translate(cbc:StartDate, '-', '')))" flag="warning">BII-T01-009: A delivery period end date SHOULD be later or equal to a start date</assert>
		</rule>
		
		
		<rule context="//cac:SellerSupplierParty//cac:TaxScheme">
			<let value="//cac:SellerSupplierParty//cac:Country/cbc:IdentificationCode" name="country"/>
<let value="//cac:BuyerCustomerParty//cac:Country/cbc:IdentificationCode" name="customerCountry"/>
<assert test="($country = '') or ($customerCountry = '') or ($country = $customerCountry) or (starts-with(cbc:CompanyID, $country))" flag="warning">BII-T01-011: In cross border trade the VAT identifier for the supplier SHOULD be prefixed with country code</assert>
		</rule>
		
		
		<rule context="//cac:BuyerCustomerParty//cac:TaxScheme">
			<let value="//cac:BuyerCustomerParty//cac:Country/cbc:IdentificationCode" name="country"/>
<let value="//cac:SellerSupplierParty//cac:Country/cbc:IdentificationCode" name="sellerCountry"/>
<assert test="($country = '') or ($sellerCountry = '') or ($country = $sellerCountry) or (starts-with(cbc:CompanyID, $country))" flag="warning">BII-T01-013: In cross border trade the VAT identifier for the customer SHOULD be prefixed with country code</assert>
		</rule>
		
		
		<rule context="//cac:AllowanceCharge">
			<assert test="cbc:AllowanceChargeReason and (cbc:AllowanceChargeReason != '' )" flag="fatal">BII-T01-014: AllowanceChargeReason text MUST be specified for all allowances and charges</assert>
		</rule>

		
		<rule context="//cac:TaxTotal">
			<let value="//cac:TaxSubTotal//cac:TaxScheme/cbc:ID" name="subTaxList"/>
<assert test="not($subTaxList) or ($subTaxList = $subTaxList[1])" flag="fatal">BII-T01-015: If an order has a tax total then each instance of a total MUST refer to a single tax schema</assert>
		</rule>

		
		<rule context="//cac:AnticipatedMonetaryTotal/cbc:LineExtensionAmount">
			<let value="sum(//cac:LineItem/cbc:LineExtensionAmount)" name="sum"/>
<assert test=". = $sum" flag="fatal">BII-T01-016: Order total line extension amount MUST equal the sum of the line totals</assert>
		</rule>
		
		
		<rule context="//cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount">
			<assert test=". = ../cbc:LineExtensionAmount + ../cbc:ChargeTotalAmount - ../cbc:AllowanceTotalAmount" flag="fatal">BII-T01-017: An order tax exclusive amount MUST equal the sum of lines plus allowances and charges on header level</assert>
		</rule>

		
		<rule context="//cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount">
			<assert test=". = ../cbc:TaxExclusiveAmount + sum(//cac:TaxTotal/cbc:TaxAmount) + sum(//cac:TaxTotal/cbc:PayableRoundingAmount)" flag="fatal">BII-T01-018: An order tax inclusive amount MUST equal the tax exclusive amount plus all tax total amounts and the rounding amount</assert>
		</rule>
		
		
		<rule context="//cbc:AllowanceTotalAmount">
			<assert test=". = sum(//cac:AllowanceCharge[cbc:ChargeIndicator = 'false']/cbc:Amount)" flag="fatal">BII-T01-019: Total allowance MUST be equal to the sum of allowances at document level</assert>
		</rule>
		
		
		<rule context="//cbc:ChargeTotalAmount">
			<assert test=". = sum(//cac:AllowanceCharge[cbc:ChargeIndicator = 'true']/cbc:Amount)" flag="fatal">BII-T01-020: Total charges MUST be equal to the sum of charges at document level</assert>
		</rule>
		
		
		<rule context="//cbc:PayableAmount">
			<assert test=". = sum(../cbc:LineExtensionAmount) + sum(../cbc:ChargeTotalAmount) - sum(../cbc:AllowanceTotalAmount) + sum(//cac:TaxTotal/cbc:TaxAmount) - sum(../cbc:PrepaidAmount) + sum(../cbc:PayableRoundingAmount)" flag="fatal">BII-T01-021: Payable amount MUST be total lineextension + total charge - total allowance + tax total - prepaid amount</assert>
		</rule>
		
		
		<rule context="//cac:OrderLine/cac:LineItem">
			<assert test="not(cac:Price) or (cbc:LineExtensionAmount = cbc:Quantity * cac:Price/cbc:PriceAmount)" flag="fatal">BII-T01-022: If price is specified Order line amount MUST be equal to the price amount multiplied by the quantity</assert>
		</rule>
		
		
		<rule context="//cac:OrderLine//cac:Item">
			<assert test="string-length(cbc:Name) &lt; 51" flag="warning">BII-T01-023: Product names SHOULD NOT exceed 50 characters long</assert>
		</rule>
		
		
		<rule context="//cac:OrderLine//cac:Item/cac:StandardItemIdentification">
			<let value="string(' GTIN9 ')" name="itemIDList"/>
<let value="concat(cbc:ID/@schemeID, cbc:ID/@schemeAgencyID)" name="itemID"/>
<assert test="not(cbc:ID) or (contains($itemIDList, $itemID))" flag="warning">BII-T01-024: If standard identifiers are provided within an item description, an Schema Identifier SHOULD be provided (e.g. GTIN)</assert>
		</rule>
		
		
		<rule context="//cac:OrderLine//cac:Item//cbc:ItemClassificationCode">
			<let value="string(' UNSPSC113 CPVZZZ ')" name="itemClassificationList"/>
<let value="concat(@listID, @listAgencyID)" name="itemClassification"/>
<assert test="not(@listID) or (contains($itemClassificationList, $itemClassification))" flag="warning">BII-T01-025: Classification codes within an item description SHOULD have a List Identifier attribute (e.g. CPV or UNSPSC)</assert>
		</rule>

		
		<rule context="//cac:Price">
			<assert test="not(cbc:PriceAmount &lt; 0)" flag="fatal">BII-T01-026: Prices of items MUST be positive or zero</assert>
		</rule>
		
	</pattern>
</schema>
