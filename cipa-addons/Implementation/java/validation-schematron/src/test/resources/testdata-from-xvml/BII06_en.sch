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
<pattern id="BII06" name="BII06 - Procurement">
	
		
		<rule context="//cbc:UBLVersionID">
			<assert test=". = '2.0'" flag="fatal">BII-P06-001: UBL VersionID MUST define a supported syntaxbinding</assert>
		</rule>
		
		
		<rule context="//cbc:CustomizationID">
			<let value="translate('urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0 urn:www.cenbii.eu:transaction:biifulltrdm001:ver1.0      urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0 urn:www.cenbii.eu:transaction:biifulltrdm002:ver1.0     urn:www.cenbii.eu:transaction:biicoretrdm003:ver1.0 urn:www.cenbii.eu:transaction:biifulltrdm003:ver1.0', ':.', '__') " name="transactionList"/>
<assert test="contains($transactionList, translate(., ':.', '__'))" flag="fatal">BII-P06-002: CustomizationID MUST comply with CEN/BII transactions definitions</assert>
		</rule>
		
		
		
		<rule context="//cbc:ProfileID">
			<let value="translate('urn:www.cenbii.eu:profile:bii06:ver1.0', ':.', '__') " name="profileList"/>
<assert test="contains($profileList, translate(., ':.', '__'))" flag="fatal">BII-P06-003: The profile ID is dependent on the profile in which the transaction is being used</assert>
		</rule>
	</pattern>
</schema>
