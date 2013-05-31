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
<!-- Útgáfa 1.00, 6. október 2011 -->
<!-- Þorkell Pétursson, thorkell.petursson@fjs.is -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:clm54217="urn:un:unece:uncefact:codelist:specification:54217:2001" xmlns:clm5639="urn:un:unece:uncefact:codelist:specification:5639:1988" xmlns:clm66411="urn:un:unece:uncefact:codelist:specification:66411:2001" xmlns:clmIANAMIMEMediaType="urn:un:unece:uncefact:codelist:specification:IANAMIMEMediaType:2003" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:n1="urn:oasis:names:specification:ubl:schema:xsd:Order-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output version="4.0" method="html" indent="no" encoding="ISO-8859-1" media-type="text/html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
	<xsl:decimal-format name="IcelandicNumber" decimal-separator="," grouping-separator="."/>
	<xsl:template match="n1:Order">
		<html>
			<head>
				<link href="style.css" title="default" type="text/css" media="screen,projection,print" rel="stylesheet"/>
				<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
				<title>Order</title>
			</head>
			<body>
				<xsl:call-template name="Sida"/>
				<xsl:call-template name="Annad"/>
				<div style="margin-top: 2em;"/>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="Sida">
		<div id="sida" class="sida">
					<div id="haus">
						<div id="hausseljandi">
							<!-- seljandi -->
							<div id="hausreikningur" class="righthausreikningur">
								<xsl:call-template name="Reikningur"/>
							</div>
							<!-- haus reikningur -->
						</div>
						<div id="hausefri">
						<div class="vinstrihaus">
							<div id="greidandi" class="leftgreidandi">
								<xsl:call-template name="Greidandi"/>
							</div>
							<div id="greidandi" class="leftgreidandi">
								<xsl:call-template name="Birgi"/>
							</div>
							<!-- Greiðandi -->
						</div>
						<div id="haussummur" class="righthaus">
							<xsl:call-template name="Summur"/>
							<br/>
							<div id="lysing" class="rightlysing">
								<xsl:call-template name="Lysing"/>
							</div>
						</div>
						<p class="clear" />
							<!-- haussummur -->
					</div>
						<!-- hausefri -->
					</div>
					<!-- haus -->
					<p class="clear" />
					<xsl:call-template name="Linur"/>
					<xsl:call-template name="Samtolur"/>
					<p class="clear" />
		</div>
		<!-- sida -->
	</xsl:template>
	<xsl:template name="Seljandi">
		<div class="dalkfyrirsogn3">Seller:</div>
		<div>
			<div class="dalkfyrirsogn2">
				<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name"/>
			</div>
		</div>
		<div style="margin-top: 0.0em;">
			<div class="left60prc">
				<div>ID.&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
				</div>
				<div>
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber"/>
					<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox[.!='']">,&#160;Po.Box:&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox"/>
					</xsl:if>
				</div>
				<xsl:choose>
					<xsl:when test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
						<div>
							<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<div>
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
					<xsl:choose>
						<xsl:when test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
							<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
			</xsl:when>
					</xsl:choose>
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
				</div>
			</div>
			<div>
<div>				
				<xsl:choose>
					<xsl:when test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Department">
							<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Department"/>&#160;
					</xsl:when>
				</xsl:choose>
					<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<xsl:for-each select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID">
					(<xsl:value-of select="@schemeID"/>&#160;
					</xsl:for-each>
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID"/>)
				</xsl:if>
				</div>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>Tel.:&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>Fax:&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID[.!='']">
					<div>VAT nr.&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>
	<!-- Seljandi -->
	<xsl:template name="Reikningur">
		<div>
			<div class="letur2">&#160;</div>
			<div class="letur2">&#160;</div>
			<div class="letur8">ORDER</div>
			<div class="letur7">Order nr.&#160;<xsl:value-of select="cbc:ID"/>
			</div>
		</div>
	</xsl:template>
	<!-- Reikningur -->
	<xsl:template name="Greidandi">
		<div class="dalkfyrirsogn">Buyer / Customer Party:</div>
		<div style="margin-top: 0.8em; margin-left: 1.2em; line-height: 120%">
			<div class="ListItem">
				<b><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyName/cbc:Name"/></b>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber"/>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox[.!='']">,&#160;Po.Box:&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox"/>
				</xsl:if>
			</div>
			<xsl:choose>
				<xsl:when test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
					<div class="ListItem">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
					</div>
				</xsl:when>
			</xsl:choose>
<div class="ListItem">			
			<xsl:choose>
				<xsl:when test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Department">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Department"/>&#160;
				</xsl:when>
</xsl:choose>				
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<xsl:for-each select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID">
					(<xsl:value-of select="@schemeID"/>&#160;
					</xsl:for-each>
					<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID"/>)
				</xsl:if>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
			<xsl:choose>
					<xsl:when test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
			</xsl:when>
				</xsl:choose>
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
			</div>
			<div class="ListItem">
				<div class="letur1b" style="display: inline">Legal Comp.ID: </div>
            			&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
			</div>
			
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID[.!='']">
					<div class="ListItem">										
					<div class="letur1b" style="display: inline">VAT nr.: </div>
					&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
					</div>
				</xsl:if>			
			
		</div>
	</xsl:template>
	<!-- Greiðandi -->
	<xsl:template name="Birgi">
		<div class="dalkfyrirsogn">Seller / Supplier Party:</div>
		<div style="margin-top: 0.8em; margin-left: 1.2em; line-height: 120%">
			<div class="ListItem">
				<b><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyName/cbc:Name"/></b>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber"/>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox[.!='']">,&#160;Po.Box:&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox"/>
				</xsl:if>
			</div>
			<xsl:choose>
				<xsl:when test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
					<div class="ListItem">
						<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
					</div>
				</xsl:when>
			</xsl:choose>
<div class="ListItem">			
			<xsl:choose>
				<xsl:when test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Department">
						<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Department"/>&#160;
				</xsl:when>
</xsl:choose>				
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<xsl:for-each select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID">
					(<xsl:value-of select="@schemeID"/>&#160;
					</xsl:for-each>
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID"/>)
				</xsl:if>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
			<xsl:choose>
					<xsl:when test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
						<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
			</xsl:when>
				</xsl:choose>
				<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
			</div>
			<div class="ListItem">
				<div class="letur1b" style="display: inline">Legal Comp.ID: </div>
           			&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
			</div>
			
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID[.!='']">
					<div class="ListItem">					
					<div class="letur1b" style="display: inline">VAT nr.: </div>
					&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
					</div>
				</xsl:if>			
			
		</div>
	</xsl:template>
	<xsl:template name="Summur">
		<div id="utgafudagur" class="utgafudagur">
			<div class="dalkfyrirsognbold">Issue Date</div>
			<div class="letur9" style="text-align: center;">
				<xsl:call-template name="icedate">
					<xsl:with-param name="text" select="cbc:IssueDate"/>
				</xsl:call-template>
			</div>
		</div>
		<!-- Gjalddagi -->
		<div id="heildarupphaed" class="heildarupphaed">
			<div class="dalkfyrirsognbold">Payable Amount</div>
			<div class="letur9" style="text-align: center;">
				<xsl:call-template name="icenumberdecdef">
					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PayableAmount"/>
				</xsl:call-template>
			</div>
		</div>
		<!-- Eindagi -->
		<div id="utgafutimi" class="utgafutimi">
			<div class="letur1b" style="display: inline">Issue Time: </div>
			<div class="letur1" style="display: inline">
					<xsl:value-of select="cbc:IssueTime"/>
			</div>
		</div>
		<div id="gjaldmidill" class="gjaldmidill">
			<div class="letur1" style="text-align: right; display: inline">Order Currency:&#160;<xsl:value-of select="cbc:DocumentCurrencyCode"/>&#160;</div>
		</div>
	</xsl:template>
	<!-- Summur -->
	<!-- Greidsluupplysingar -->
	<xsl:template name="Lysing">
		<div class="letur1b" style="display: inline">Order.Note: </div>
		<div class="letur1" style="display: inline">
			<xsl:value-of select="cbc:Note"/>
		</div>
		<br/><br/>
		<xsl:if test="cbc:AccountingCost[.!='']">
			<div class="letur1b" style="display: inline">AccountingCostCode: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cbc:AccountingCost"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">Buyer PartyID: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:ValidityPeriod/cbc:EndDate[.!='']">
			<br/>
			<div class="letur1b" style="display: inline"> ValidityPeriod: </div>
			<div class="letur1" style="display: inline">
			<xsl:call-template name="icedate">
					<xsl:with-param name="text" select="cac:ValidityPeriod/cbc:EndDate"/>
			</xsl:call-template>
			</div>
		</xsl:if>
				<xsl:if test="cac:QuotationDocumentReference/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">QuotationDocumentReference: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:QuotationDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:OrderDocumentReference/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">OrderDocumentReference: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:OrderDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:OriginatorDocumentReference/cbc:DocumentType[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">OriginatorDocumentReference: </div>
			<div class="letur1" style="display: inline">
					<xsl:value-of select="cac:OriginatorDocumentReference/cbc:DocumentType"/>&#160;<xsl:value-of select="cac:OriginatorDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:Contract/cbc:ContractType[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">Contract: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:Contract/cbc:ContractType"/>&#160;<xsl:value-of select="cac:Contract/cbc:ID"/>
			</div>
		</xsl:if>
	</xsl:template>
	<!-- Lýsing -->
	<xsl:template name="Linur">
		<div>&#160;</div>
		<div id="linur" class="linur">
			<table width="100%" cellspacing="0" cellpadding="3" class="ntable" summary="Línur á reikningi">
				<tr height="25">
					<td width="4%" class="hdrcol22" align="right" nowrap="nowrap">LineID</td>
					<td width="6%" class="hdrcol22" align="left" nowrap="nowrap">Item.ID.</td>
					<td width="33%" class="hdrcol22" align="left">SellerID</td>
					<td width="6%" class="hdrcol22" align="right" style="padding-right: 0.4em;" nowrap="nowrap">Quantity</td>
					<td width="6%" class="hdrcol22" align="center" nowrap="nowrap">UOM.</td>
					<td width="6%" class="hdrcol22" align="right" nowrap="nowrap">Unit Price*</td>
					<td width="17%" class="hdrcol22" align="center" nowrap="nowrap">Partial Deliv?</td>
					<td width="11%" class="hdrcol22" align="right" nowrap="nowrap">LineAmount</td>
					<td width="11%" class="hdrcol22" align="right" nowrap="nowrap">Amount+VAT&#160;</td>
				</tr>
				<xsl:for-each select="cac:OrderLine">
					<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #0a4a2a; border-right: 0.1em solid #0a4a2a;">
						<td width="4%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- linu nr -->
							<xsl:value-of select="cac:LineItem/cbc:ID"/>.&#160;
						</td>
						<td width="6%" valign="top" align="left" class="hdrcol23" nowrap="nowrap">
							<!-- vörunúmer -->
							<xsl:value-of select="cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ID"/>
						</td>
						<td width="33%" valign="top" align="left" class="hdrcol23">
							<!-- Lýsing -->
							<xsl:value-of select="cac:LineItem/cac:Item/cbc:Name"/>
						</td>
						<td width="6%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Magn -->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity"/>
							</xsl:call-template>&#160;
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Ein -->
							<xsl:call-template name="Einingar">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity/@unitCode"/>
							</xsl:call-template>
						</td>
						<td width="6%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Ein.verð*-->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:PriceAmount"/>
							</xsl:call-template>
						</td>
						<td width="15%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Afsl. kr.-->
							<xsl:call-template name="Partialdelivery">
								<xsl:with-param name="text" select="cac:LineItem/cbc:PartialDeliveryIndicator"/>
							</xsl:call-template>
						</td>
						<td width="11%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Upphæð -->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cbc:LineExtensionAmount"/>
							</xsl:call-template>
						</td>
						<td width="13%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Upphæð m.vsk-->
							<xsl:call-template name="upphaedmvsk2"/>&#160;
						</td>
					</tr>
				</xsl:for-each>
			</table>
			</div>
</xsl:template>
		<!-- Linur -->
<xsl:template name="Samtolur">
            <table width="100%" cellspacing="0" cellpadding="0" summary="Samtölur á pöntun">
				<tr>
				  <td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				  <td width="52%" class="hdrcol23" align="left"><div class="hdrcolSmall" align="left" nowrap="nowrap">No.of lines: <xsl:value-of select='format-number(count(cac:OrderLine/cac:LineItem/cbc:ID), "###.###", "IcelandicNumber")'/>&#160;&#160;&#160;&#160;&#160;&#160;&#160;*Unit Price is excluding VAT.</div></td>
				  <td width="20%" class="hdrcol23" align="right" nowrap="nowrap">LineExtensionAmount:</td>
					<td width="11%" class="hdrcol23" align="right" nowrap="nowrap"><xsl:value-of select='format-number(sum(cac:AnticipatedMonetaryTotal/cbc:LineExtensionAmount), "###.###,##", "IcelandicNumber")'/>
</td>
					<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
			  </tr>
			  	<xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='true']">				
					<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #0a4a2a; border-right: 0.1em solid #0a4a2a;">
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					  <td valign="top" align="left" class="hdrcol23">&#160;</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><xsl:value-of select="cbc:AllowanceChargeReason"/>:</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:Amount"/>
							</xsl:call-template>
						</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
			  </tr>
              	</xsl:for-each>
              				  	<xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='false']">				
					<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #0a4a2a; border-right: 0.1em solid #0a4a2a;">
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"> &#160;</td>
					  <td valign="top" align="left" class="hdrcol23">&#160;</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><xsl:value-of select="cbc:AllowanceChargeReason"/>:</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">-
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:Amount"/>
							</xsl:call-template>
						</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
			  </tr>
              	</xsl:for-each>
              			  
					<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #0a4a2a; border-right: 0.1em solid #0a4a2a;">
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					  <td valign="top" align="left" class="hdrcol23">&#160;</td>
					  <td valign="top" align="right" class="hdrcol234" nowrap="nowrap">TaxAmount:</td>
					  <td valign="top" align="right" class="hdrcol234" nowrap="nowrap">
					  		<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:TaxTotal/cbc:TaxAmount"/>
							</xsl:call-template></td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
			  </tr>
			  	<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #0a4a2a; border-right: 0.1em solid #0a4a2a;">
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					  <td valign="top" align="left" class="hdrcol23">&#160;</td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><b>PayableAmount:</b></td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><b>
					  		<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PayableAmount"/>
							</xsl:call-template></b></td>
					  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
			  </tr>
		</table>
	</xsl:template>

	
	<!-- template Linur -->
	
	<xsl:template name="Annad">
		<div id="sidaannad" class="sida">
			
		
				<!-- Comment Content Start -->
				
					<div id="annad" class="annad">
							<xsl:call-template name="AnnadAfhendingarstadur"/>
						<div class="floatkassi">
							<xsl:call-template name="TengilidurKaupanda"/>
							<xsl:call-template name="TengilidurSeljanda"/>
							<xsl:call-template name="Tengilidurpantanda"/>
						</div><p class="clear" />
						<xsl:call-template name="AnnadItarupplLinu"/>
						<xsl:call-template name="AnnadVidhengi2"/>
					</div>
					<!-- annad -->
				
		

		</div>
		<!-- sidaannad -->
	</xsl:template>
	<!-- template Annad -->
	<xsl:template name="AnnadAfhendingarstadur">
		<xsl:choose>
			<xsl:when test="(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName) or (cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingNumber) or (cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName) or (cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department) or 
(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone) or
(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity) or
(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode) or
(cac:Delivery/cac:DeliveryLocation/cbc:ID) [.!='']">
						<div class="itarupplBig">Delivery information:</div>				
				<div class="afhending">
						<div class="afhendingvinstri">
							<div class="ListItem">
								<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name[.!='']">
									<b>Name: <xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name"/></b>
								</xsl:if>
							</div>
							<div class="ListItem">
								<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PartyIdentification/cbc:ID[.!='']">
									<b>DeliveryPartyID: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PartyIdentification/cbc:ID"/>
								</xsl:if>
							</div>
							<div class="ListItem"><xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName"/>&#160;<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingNumber"/>
							</div>
							<div class="ListItem">
								<xsl:choose>
									<xsl:when test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName">
										<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/>
									</xsl:when>
								</xsl:choose>
							</div>
						<div class="ListItem">
						<xsl:choose>
							<xsl:when test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department">
								<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department"/>
							</xsl:when>
						</xsl:choose>
						<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:ID[.!='']">
							<xsl:for-each select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:ID">
								(<xsl:value-of select="@schemeID"/>&#160;
							</xsl:for-each>
							<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:ID"/>)
						</xsl:if>
						</div>
						<div class="ListItem"><xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/>&#160;<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName"/>,
							<xsl:choose>
								<xsl:when test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity">
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity"/>,
								</xsl:when>
							</xsl:choose>
							<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/>
							<div class="ListItem">
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cbc:ID[.!='']">
									DeliveryTerms.Location: 
									<xsl:for-each select="cac:DeliveryTerms/cac:DeliveryLocation/cbc:ID">
										<xsl:value-of select="@schemeID"/>&#160;
									</xsl:for-each>
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cbc:ID"/>
								</xsl:if>
							</div>
							</div>
						</div>
					<div class="afhendinghaegri">
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:RequestedDeliveryPeriod[.!='']">
								<b>RequestedDeliveryPeriod.: </b>
								<xsl:call-template name="icedate">
									<xsl:with-param name="text" select="cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartDate"/>
								</xsl:call-template>
								- 
								<xsl:call-template name="icedate">
									<xsl:with-param name="text" select="cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndDate"/>
								</xsl:call-template>
							</xsl:if>
						</div>
						<div class="ListItem">
						<b>DeliveryTerms: </b>
							<xsl:if test="cac:DeliveryTerms/cbc:ID[.!='']">
								<xsl:value-of select="cac:DeliveryTerms/cbc:ID"/>
							</xsl:if>
							<xsl:if test="(cac:DeliveryTerms/cbc:ID) and (cac:DeliveryTerms/cbc:SpecialTerms) [.!='']">
							- 
							</xsl:if>
							<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms[.!='']">
								<xsl:value-of select="cac:DeliveryTerms/cbc:SpecialTerms"/>
							</xsl:if>						
						</div>
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Name[.!='']">
								<b>DeliveryParty.ContactName: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Name"/>
							</xsl:if>
						</div>
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telephone[.!='']">
								<b>Tel: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telephone"/>
							</xsl:if>
						</div>
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telefax[.!='']">
								<b>Fax: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telefax"/>
							</xsl:if>
						</div>
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ElectronicMail[.!='']">
								<b>Email: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ElectronicMail"/>
							</xsl:if>
						</div>
					</div>
					<p class="clear" />
				</div>
			</xsl:when>
		</xsl:choose>
		<br/>
	</xsl:template>
	<!-- template AnnadGreidsluuppl -->
	<xsl:template name="TengilidurSeljanda">	
		<xsl:if test="(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FirstName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:JobTitle) or (cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone) or
(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telefax) or
(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or
(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or
(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID) [.!='']">
			<div class="tengilidirseljanda">	
			<div class="itarupplBig">Additional seller information:</div>			
			<div class="minifloat">	
				<xsl:choose>
					<xsl:when test="(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FirstName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:SellerSupplierParty/cac:Party/cac:Person/cbc:JobTitle) [.!='']">
						<div><b>SupplierPerson:</b>
						</div>
						<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FirstName"/>&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Person/cbc:MiddleName"/>&#160;<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Person/cbc:FamilyName"/>
						</div>
						<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Person/cbc:JobTitle"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail[.!='']">
					<div>E-mail: <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>Tel: <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>Fax: <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or (cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or (cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) [.!='']">
						<div>
							<b>PartyLegalEntity:</b>
						</div>
						<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
						</div>
						<xsl:choose>
							<xsl:when test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, <xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity[.!='']">
										<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity"/>, </xsl:if>
									<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<div>
						<b>PostalAdressID:</b>
					</div>
					<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID"/>
					</div>
				</xsl:if>
				

				<xsl:if test="cac:SellerSupplierParty/cac:Party/cbc:EndpointID[.!='']">
					<div>
						<b>EndpointID: </b><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cbc:EndpointID"/>
					</div>
				</xsl:if>
					
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
					<div>
						<b>SupplierPartyID:</b>
					</div>
					<div><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
			</div>
</div>
</xsl:if>
	</xsl:template>
	<!-- template tengiliður seljanda-->
	<xsl:template name="TengilidurKaupanda">
		<xsl:if test="(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FirstName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:JobTitle) or (cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone) or
(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telefax) or
(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or
(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or
(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID) [.!='']">		
			<div class="tengilidirkaupanda">
			<div class="itarupplBig">Additional buyer information:</div>			
			<div class="minifloat">
				
				
				<xsl:choose>
					<xsl:when test="(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FirstName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:JobTitle) [.!='']">
						<div><b>BuyerPerson:</b>
						</div>
						<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FirstName"/>&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:MiddleName"/>&#160;<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:FamilyName"/>
						</div>
						<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:JobTitle"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail[.!='']">
					<div>E-mail: <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>Tel: <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>Fax: <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or (cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or (cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) [.!='']">
						<div>
							<b>PartyLegalEntity:</b>
						</div>
						<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
						</div>
						<xsl:choose>
							<xsl:when test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, <xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity[.!='']">
										<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity"/>, </xsl:if>
									<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>

				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<div>
						<b>PostalAdressID:</b>
					</div>
					<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID[.!='']">
					<div>
						<b>EndpointID: </b><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID"/>
					</div>
				</xsl:if>								
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
					<div>
						<b>CustomerPartyID:</b>
					</div>
					<div><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Name) or (cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telephone) or (cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telefax) or (cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ElectronicMail) [.!='']">
						<div><b>SupplierDeliveryContact:</b></div>
						<div><xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Name"/></div>
						<div>Tel: <xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telephone"/></div>
						<div>Tel: <xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telefax"/></div>
						<div><xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ElectronicMail"/></div>
					</xsl:when>
				</xsl:choose>
			</div>
			</div>
			</xsl:if>
	</xsl:template>
	<!-- template tengiliður kaupanda-->
<xsl:template name="Tengilidurpantanda">	
			<div class="tengilidirpantanda">	
			<div class="itarupplBig">OriginatorCustomerParty:</div>			
			<div class="minifloat">	
				<xsl:choose>
					<xsl:when test="(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:FirstName) or (cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:FamilyName) [.!='']">
						<div><b>OriginatorPerson:</b>
						</div>
						<div><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:FirstName"/>&#160;<xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:MiddleName"/>&#160;<xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:FamilyName"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:JobTitle[.!='']">
					<div><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:JobTitle"/></div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:PartyName/cbc:Name[.!='']">
					<div><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:PartyName/cbc:Name"/></div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail[.!='']">
					<div>E-mail: <xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>Tel: <xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>Fax: <xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>

				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
					<div>
						<b>OriginatorPartyID:</b>
					</div>
					<div><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
	
				</xsl:if>
			</div>
</div>
	</xsl:template>	
	
	<xsl:template name="AnnadItarupplLinu">
		<br/>
		<div class="itarupplBig">Additional Line information:</div>
		<table class="ntable" cellpadding="3" cellspacing="0" width="100%" summary="Ítarupplýsingar á línum" border="0">
			<tr height="25">
				<td width="1%" class="hdrcol22" align="center" nowrap="nowrap">&#160;</td>
				<td width="3%" class="hdrcol22" align="left" nowrap="nowrap">&#160;</td>
				<td width="16%" class="hdrcol22" align="left" nowrap="nowrap">SellerItemID</td>
				<td width="16%" class="hdrcol22" align="left" nowrap="nowrap">StandardItemID</td>
				<td width="20%" class="hdrcol22" align="left" style="padding-right: 0.4em;" nowrap="nowrap">AccountingCostID</td>
				<td width="20%" class="hdrcol22" align="center" nowrap="nowrap">OriginatorParty</td>
				<td width="23%" class="hdrcol22" align="center" nowrap="nowrap">DeliveryPeriod</td>
				<td width="1%" class="hdrcol22" align="left" nowrap="nowrap">&#160;</td>
			</tr>
			<xsl:for-each select="cac:OrderLine">
				<tr>
					<td class="hdrcol23" align="left" nowrap="nowrap">&#160;</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- Línunúmer -->
						<xsl:value-of select="cac:LineItem/cbc:ID"/>.
					</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer seljanda-->
						<xsl:value-of select="cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ID"/>
					</td>
					<td valign="top" align="left" class="hdrcol23">
						<!-- Staðlað vörunúmer -->
						<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID"/>
					</td>
					<td valign="top" align="left" class="hdrcol23">
						<!-- Lýsing -->
						<xsl:value-of select="cac:LineItem/cbc:AccountingCost"/>
					</td>
					<td valign="top" align="center" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer -->
						<xsl:value-of select="cac:LineItem/cac:OriginatorParty/cac:PartyName/cbc:Name"/>&#160;
						<xsl:value-of select="cac:LineItem/cac:OriginatorParty/cac:PartyIdentification/cbc:ID"/>
					</td>
					<td valign="top" align="center" class="hdrcol23">
						<!-- Lýsing -->
						<xsl:call-template name="icedate">
							<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartDate"/>
						</xsl:call-template>
						- 
						<xsl:call-template name="icedate">
							<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndDate"/>
						</xsl:call-template>
					</td>
					<td valign="top" align="left" class="hdrcol23"/>
				</tr>
				<xsl:choose>
					<xsl:when test="cac:LineItem/cac:Item/cbc:Description[.!='']">
						<tr>
							<td/>
							<td colspan="6" class="hdrcol23">
								<b>Description: </b>
								<xsl:value-of select="cac:LineItem/cac:Item/cbc:Description"/>
							</td>
							<td/>
						</tr>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
				<xsl:if test="cac:LineItem/cac:Item/cac:AdditionalItemProperty[.!='']">
					<tr valign="top">
						<td/>
						<td colspan="6">
							<table summary="Lýsing vöru" border="0" width="100%" cellpadding="0" cellspacing="0">
								<xsl:for-each select="cac:LineItem/cac:Item/cac:AdditionalItemProperty">
									<tr>
										<td height="5px" valign="bottom" align="left" class="hdrcolimage"/>
										<td width="98%" valign="bottom" align="left" class="hdrcol23">
											<xsl:value-of select="cbc:Name"/> - <xsl:value-of select="cbc:Value"/>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</td>
					</tr>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="cbc:Note[.!='']">
						<tr>
							<td/>
							<td colspan="6" class="hdrcol23">
								<b>Note: </b>
								<xsl:value-of select="cbc:Note"/>
							</td>
							<td/>
						</tr>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!-- template AnnadGreidsluuppl -->
	<xsl:template name="VidbotarupplysingarVSKframhald">
	<div class="itarupplBig">Additional VAT information - extended</div>
<table class="ntable" cellpadding="3" cellspacing="0" width="100%" border="0">
  <tr height="25">
    <th scope="col" class="hdrcol22" align="center" >Tegund</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang tegund</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang hverfi</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang innanhús</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang fastanr.</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang póstnr.</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang tímamunur</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang land</th>
    <th scope="col" class="hdrcol22" align="center" >Heimilisfang hnit</th>
  </tr>
  <xsl:for-each select="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress">
  <tr>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="../cbc:ID"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:AddressTypeCode"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:BlockName"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:InhouseMail"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:PlotIdentification"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:CitySubdivisionName"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:TimezoneOffset"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:Country/cbc:IdentificationCode"/>, <xsl:value-of select="cac:Country/cbc:IdentificationCode"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:LocationCoordinate/cbc:CoordinateSystemCode"/>: 
    <xsl:value-of select="cac:LocationCoordinate/cbc:LatitudeDegreesMeasure"/>°
    <xsl:value-of select="cac:LocationCoordinate/cbc:LatitudeMinutesMeasure"/>&apos;
    <xsl:value-of select="cac:LocationCoordinate/cbc:LatitudeDirectionCode"/>,
    <xsl:value-of select="cac:LocationCoordinate/cbc:LongitudeDegreesMeasure"/>°
    <xsl:value-of select="cac:LocationCoordinate/cbc:LongitudeMinutesMeasure"/>&apos;
    <xsl:value-of select="cac:LocationCoordinate/cbc:LongitudeDirectionCode"/></td>
  </tr>
  </xsl:for-each>
</table>
	</xsl:template>
	
<xsl:template name="VidbotarupplysingarVSK">
<div class="itarupplBig">Additional VAT information</div>
<table class="ntable" cellpadding="3" cellspacing="0" width="100%" border="0">
  <tr height="25">
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">Tegund</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">Nafn</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">TaxTypeCode</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">Gjaldmiðill</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">VSKnr.</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">Lögskráð nafn</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">Lögskráð staðsetning</th>
    <th scope="col" class="hdrcol22" align="center" nowrap="nowrap">&#160;</th>
  </tr>
  <xsl:for-each select="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme">
  <tr>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:TaxScheme/cbc:ID"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:TaxScheme/cbc:Name"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:TaxScheme/cbc:TaxTypeCode"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:TaxScheme/cbc:CurrencyCode"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:CompanyID"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cbc:RegistrationName"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap"><xsl:value-of select="cac:RegistrationAddress/cbc:CityName"/>, <xsl:value-of select="cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/></td>
    <td class="hdrcol23" align="center" nowrap="nowrap">&#160;</td>
  </tr>
  </xsl:for-each>
</table>
</xsl:template>	
<xsl:template name="AnnadVidhengi2">
		<xsl:if test="cac:AdditionalDocumentReference/cbc:ID[.!='']">
			<table>
				<tr height="10">
					<td/>
				</tr>
			</table>
			<div class="itarupplBig">Attachment:</div>
			<table class="ntable" cellpadding="1" cellspacing="1" width="100%" summary="Viðhengi" border="0">
				<tr height="25">
					<td width="10%" class="hdrcol22" align="left" nowrap="nowrap">&#160;&#160;ID.</td>
					<td width="18%" class="hdrcol22" align="left" nowrap="nowrap">Type</td>
					<td width="72%" class="hdrcol22" align="left" nowrap="nowrap">URI</td>
				</tr>
				<xsl:for-each select="cac:AdditionalDocumentReference">
					<tr height="25">
						<td class="hdrcol23" align="left" nowrap="nowrap">&#160;&#160;<xsl:value-of select="cbc:ID"/>.</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:value-of select="cbc:DocumentType"/>
						</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:variable name="urlString" select="cac:Attachment/cac:ExternalReference/cbc:URI"/>
							<a href="{$urlString}">
								<xsl:value-of select="cac:Attachment/cac:ExternalReference/cbc:URI"/>
							</a>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>
	<!-- template icedate -->
	<xsl:template name="icedate">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select="concat(substring($text, 9, 2),'.', substring($text, 6, 2),'.', substring($text, 1, 4))"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icedate -->
	<xsl:template name="icedateshort">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select="concat(substring($text, 9, 2), substring($text, 6, 2), substring($text, 3, 2))"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icedate -->
	<xsl:template name="icenumberdec">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,00", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdec -->
	<xsl:template name="icenumberdecdef">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,00", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(0.00, "###.##0,00", "IcelandicNumber")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdec -->
	<xsl:template name="icenumberdecdef2">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,0", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(0.00, "###.##0,0", "IcelandicNumber")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdec -->
	<xsl:template name="icenumber">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.###", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumber -->
	<xsl:template name="icenumberdef">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.###", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdef -->
	<xsl:template name="afslatturlinu">
		<xsl:param name="afsl" select="cac:Price/cac:AllowanceCharge/cbc:ChargeIndicator"/>
		<xsl:param name="amount" select="cac:Price/cac:AllowanceCharge/cbc:Amount"/>
		<xsl:choose>
			<xsl:when test="$afsl[.='true']">
				<xsl:value-of select='format-number($amount, "###.##0,00", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:when test="$afsl[.='false']">
				<xsl:value-of select='format-number($amount, "###.##0,00", "IcelandicNumber")'/>
			</xsl:when>
			<xsl:otherwise>0,00</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdef -->
	<xsl:template name="upphaedmvsk2">
		<xsl:variable name="amount" select="cac:LineItem/cbc:LineExtensionAmount"/>
		<xsl:variable name="tax" select="cac:LineItem/cbc:TotalTaxAmount"/>
		<xsl:value-of select='format-number($amount + $tax, "###.##0,00", "IcelandicNumber")'/>
	</xsl:template>
	
	<xsl:template name="Partialdelivery">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.='true']">Yes</xsl:when>
			<xsl:when test="$text[.='false']">No</xsl:when>
			<xsl:otherwise>Not present</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<!-- template icenumberdef -->
	<xsl:template name="Einingar">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.='C62']">Pcs</xsl:when>
			<xsl:when test="$text[.='KGS']">kg</xsl:when>
			<xsl:when test="$text[.='MTR']">m</xsl:when>
			<xsl:when test="$text[.='LTR']">l</xsl:when>
			<xsl:when test="$text[.='MTK']">m²</xsl:when>
			<xsl:when test="$text[.='MTQ']">m³</xsl:when>
			<xsl:when test="$text[.='KMT']">km</xsl:when>
			<xsl:when test="$text[.='TNE']">t</xsl:when>
			<xsl:when test="$text[.='KWH']">kWh</xsl:when>
			<xsl:when test="$text[.='DAY']">Day</xsl:when>
			<xsl:when test="$text[.='HUR']">Hrs</xsl:when>
			<xsl:when test="$text[.='MIN']">Min</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template Greidslutegund -->
</xsl:stylesheet>
