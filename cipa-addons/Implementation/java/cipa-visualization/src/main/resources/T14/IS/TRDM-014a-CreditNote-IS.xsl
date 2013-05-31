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
<!-- Útgáfa 1.02, 4. október 2011 -->
<!-- Þorkell Pétursson, thorkell.petursson@fjs.is -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:clm54217="urn:un:unece:uncefact:codelist:specification:54217:2001" xmlns:clm5639="urn:un:unece:uncefact:codelist:specification:5639:1988" xmlns:clm66411="urn:un:unece:uncefact:codelist:specification:66411:2001" xmlns:clmIANAMIMEMediaType="urn:un:unece:uncefact:codelist:specification:IANAMIMEMediaType:2003" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:n1="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output version="4.0" method="html" indent="no" encoding="ISO-8859-1" media-type="text/html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
	<xsl:decimal-format name="IcelandicNumber" decimal-separator="," grouping-separator="."/>
	<xsl:variable name="erpro">
		<xsl:call-template name="ProsentaHeader"/>
	</xsl:variable>
	<xsl:variable name="ergrunnupph">
		<xsl:call-template name="GrunnupphaedHeader"/>
	</xsl:variable>
	<xsl:template match="n1:CreditNote">
		<html>
			<head>
				<link href="style.css" title="default" type="text/css" media="screen,projection,print" rel="stylesheet"/>
				<script src="toggleContainer.js" type="text/javascript"/>
				<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
				<title>CREDIT NOTE</title>
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
			<div class="content-parent-main">
				<!-- Comment Content Start -->
				<div class="content-content">
					<div id="haus">
						<div id="hausseljandi">
							<div id="seljandi" class="leftseljandi">
								<xsl:call-template name="Seljandi"/>
							</div>
							<!-- seljandi -->
							<div id="hausreikningur" class="righthausreikningur">
								<xsl:call-template name="Reikningur"/>
							</div>
							<!-- haus reikningur -->
						</div>
						<div id="hausefri">
							<div id="greidandi" class="leftgreidandi">
								<xsl:call-template name="Greidandi"/>
							</div>
							<!-- Greiðandi -->
							<div id="haussummur" class="righthaussummur">
								<xsl:call-template name="Summur"/>
							</div>
							<!-- haussummur -->
						</div>
						<!-- hausefri -->
	

							<!-- mottakandi -->
							<div id="lysing" class="rightlysing">
								<xsl:call-template name="Lysing"/>
							</div><p class="clear" />
							<!-- Lýsing -->

						<!-- hausnedri -->
					</div>
					<!-- haus -->
					<xsl:call-template name="Linur"/>

					<div class="floatkassi">
					<xsl:call-template name="SkattarOgAfslnytt"/>					
					<xsl:call-template name="Samtolur"/>
					</div><p class="clear" />
				</div>
			</div>
			<!--<div class="content-parent-b">
        <div class="content-parent-b-r">
          <div class="content-parent-b-l"></div>
        </div>
      </div> -->
		</div>
		<!-- sida -->
	</xsl:template>
	<xsl:template name="Seljandi">
		<div class="dalkfyrirsogn3">Seller / AccountingSupplierParty:</div>
		<div>
			<div class="dalkfyrirsogn2">
				<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name"/>
			</div>
		</div>
		<div style="margin-top: 0.0em;">
			<div class="left60prc">
				<div>LegalEntityID.&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
				</div>
				<div>
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber"/>
					<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox[.!='']">,&#160;Po.box:&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox"/>
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
					<div>Tel::&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
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
			<div class="letur8">CREDIT NOTE</div>
			<div class="letur7">Credit Note Nr:&#160;<xsl:value-of select="cbc:ID"/>
			</div>
		</div>
	</xsl:template>
	<!-- Reikningur -->
	<xsl:template name="Greidandi">
		<div class="dalkfyrirsogn">Buyer / AccountingCustomerParty:</div>
		<div style="margin-top: 1.2em; margin-left: 1.2em; line-height: 120%">
			<div class="ListItem">
				<b><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name"/></b>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber"/>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox[.!='']">,&#160;Po.box:&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox"/>
				</xsl:if>
			</div>
			<xsl:choose>
				<xsl:when test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
					<div class="ListItem">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
					</div>
				</xsl:when>
			</xsl:choose>
<div class="ListItem">			
			<xsl:choose>
				<xsl:when test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Department">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Department"/>&#160;
				</xsl:when>
</xsl:choose>				
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<xsl:for-each select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID">
					(<xsl:value-of select="@schemeID"/>&#160;
					</xsl:for-each>
					<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID"/>)
				</xsl:if>
			</div>
			<div class="ListItem">
				<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
			<xsl:choose>
					<xsl:when test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
			</xsl:when>
				</xsl:choose>
				<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
			</div>
			<div class="ListItem">
            LegalEntityID.&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
			</div>
			
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID[.!='']">
					<div class="ListItem">VAT nr.&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
					</div>
				</xsl:if>			
			
		</div>
	</xsl:template>
	<!-- Greiðandi -->
	<xsl:template name="Summur">
		<div id="haussummurgjalddagi" class="righhaussummurgjalddagi">
			<div class="dalkfyrirsognbold">IssueDate</div>
			<div class="letur9" style="text-align: center;">
				<xsl:call-template name="icedate">
					<xsl:with-param name="text" select="cbc:IssueDate"/>
				</xsl:call-template>
			</div>
		</div>
		<!-- Gjalddagi -->
		<div id="haussummureindagi" class="righhaussummureindagi">
			<div class="dalkfyrirsognbold">PayableAmount</div>
			<div class="letur9" style="text-align: center;">
				<xsl:call-template name="icenumberdecdef">
					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PayableAmount"/>
				</xsl:call-template>
			</div>
		</div>


		<!-- Til greiðslu -->
		<div class="letur1" style="text-align: right">DocumentCurrencyCode:&#160;<xsl:value-of select="cbc:DocumentCurrencyCode"/>&#160;</div>
	</xsl:template>
	<!-- Summur -->
	<xsl:template name="Greidsluupplysingar">
		<div class="leftgreidsluupplysingarhaus">Tilvísanir í fyrri reikninga:</div>
		<table width="100%" cellspacing="0" cellpadding="3" summary="Totals2" class="ntablenoborder">
				<tr>
					<xsl:if test="cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID[.!='']">
					<td width="50%" class="letur1" align="left" nowrap="nowrap">&#160;&#160;<b>InvoiceDocumentReference</b></td>	
					</xsl:if>
					<xsl:if test="cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID[.!='']">
					<td width="50%" class="letur1" align="left" nowrap="nowrap"><b>CreditNoteDocumentReference</b></td>
					</xsl:if>
				</tr>
				<tr>
					<xsl:if test="cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID[.!='']">
					<td width="50%" class="letur1" align="left" nowrap="nowrap">&#160;&#160;<xsl:value-of select="cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID"/></td>
					</xsl:if>
					
					<xsl:if test="cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID[.!='']">
					<td width="50%" class="letur1" align="left" nowrap="nowrap"><xsl:value-of select="cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID"/></td>
					</xsl:if>
					</tr>
										
		</table>
		
	</xsl:template>
	<!-- Greidsluupplysingar -->
	<xsl:template name="Lysing">
		<div class="letur1b" style="display: inline">Note: </div>
		<div class="letur1" style="display: inline">
			<xsl:value-of select="cbc:Note"/>
		</div>
		<div class="dalkfyrirsognbold">&#160;</div>
		<xsl:if test="cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID[.!='']">
			<div class="letur1b" style="display: inline">InvoiceDocumentReference: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
			<xsl:if test="cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">CreditNoteDocumentReference: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		

		<xsl:if test="cac:ContractDocumentReference/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">ContractDocumentID: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:ContractDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:ContractDocumentReference/cbc:DocumentType[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">ContractDocumentType: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:ContractDocumentReference/cbc:DocumentType"/>
			</div>
		</xsl:if>



		<xsl:if test="cbc:AccountingCost[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">AccountingCost: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cbc:AccountingCost"/>
			</div>
		</xsl:if>

		<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
			<br/>
			<div class="letur1b" style="display: inline">CustomerPartyID: </div>
			<div class="letur1" style="display: inline">
				<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
			</div>
		</xsl:if>
				<xsl:if test="cac:InvoicePeriod/cbc:StartDate[.!='']">
				<br/>
					<div class="letur1b" style="display: inline">InvoicePeriod:&#160;</div>
					<div class="letur1" style="display: inline">
					<xsl:call-template name="icedate">
						<xsl:with-param name="text" select="cac:InvoicePeriod/cbc:StartDate"/>
					</xsl:call-template>&#160;-&#160;<xsl:call-template name="icedate">
						<xsl:with-param name="text" select="cac:InvoicePeriod/cbc:EndDate"/>
					</xsl:call-template>
					</div>
			</xsl:if>
			
			<xsl:if test="cbc:TaxPointDate[.!='']">
			<br/>
				<div class="letur1b" style="display: inline">TaxPointDate:&#160;</div>
					<div class="letur1" style="display: inline">
					<xsl:call-template name="icedate">
						<xsl:with-param name="text" select="cbc:TaxPointDate"/>
					</xsl:call-template>
				</div>
			</xsl:if>
			

		<br/>
		<div class="rightlysingbox1">
			<div class="letur1b">PaymentTerms:</div>
		</div>
		<div class="rightlysingbox2">
			<div class="letur1">
				<xsl:choose>
					<xsl:when test="cac:PaymentTerms/cbc:Note">
						<xsl:value-of select="cac:PaymentTerms/cbc:Note"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>
	</xsl:template>
	<!-- Lýsing -->
	<xsl:template name="Linur">
		<div>&#160;</div>
		<div id="linur" class="linur">
			<table width="100%" cellspacing="0" cellpadding="3" class="ntable" summary="CreditNote lines">
				<tr height="25">
					<td width="4%" class="hdrcol22" align="right" nowrap="nowrap">&#160;</td>
					<td width="6%" class="hdrcol22" align="left" nowrap="nowrap">ItemID</td>
					<td width="33%" class="hdrcol22" align="left">ItemName</td>
					<td width="6%" class="hdrcol22" align="right" style="padding-right: 0.4em;" nowrap="nowrap">Quantity</td>
					<td width="6%" class="hdrcol22" align="center" nowrap="nowrap">UOM.</td>
					<td width="6%" class="hdrcol22" align="right" nowrap="nowrap">PriceAmount*</td>
					<td width="6%" class="hdrcol22" align="center" nowrap="nowrap">TaxCategoryID</td>
					<td width="11%" class="hdrcol22" align="right" nowrap="nowrap">Discount</td>
					<td width="11%" class="hdrcol22" align="right" nowrap="nowrap">LineExtensionAmount</td>
					<td width="11%" class="hdrcol22" align="right" nowrap="nowrap">Amount+VAT&#160;</td>
				</tr>
				<xsl:for-each select="cac:CreditNoteLine">
					<tr class="col2{position() mod 2}" style="border-left: 0.1em solid #CDA1B6; border-right: 0.1em solid #CDA1B6;">
						<td width="4%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- linu nr -->
							<xsl:value-of select="cbc:ID"/>.&#160;
						</td>
						<td width="6%" valign="top" align="left" class="hdrcol23" nowrap="nowrap">
							<!-- vörunúmer -->
							<xsl:value-of select="cac:Item/cac:SellersItemIdentification/cbc:ID"/>
						</td>
						<td width="33%" valign="top" align="left" class="hdrcol23">
							<!-- Lýsing -->
							<xsl:value-of select="cac:Item/cbc:Name"/>
						</td>
						<td width="6%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Magn -->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:CreditedQuantity"/>
							</xsl:call-template>&#160;
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Ein -->
							<xsl:call-template name="Einingar">
								<xsl:with-param name="text" select="cbc:CreditedQuanity/@unitCode"/>
							</xsl:call-template>
						</td>
						<td width="6%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Ein.verð*-->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cac:Price/cbc:PriceAmount"/>
							</xsl:call-template>
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- VSK-->
							<xsl:value-of select="cac:Item/cac:ClassifiedTaxCategory/cbc:ID"/>
						</td>
						<td width="11%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Afsl. kr.-->
							<xsl:call-template name="afslatturlinu"/>
						</td>
						<td width="11%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Upphæð -->
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:LineExtensionAmount"/>
							</xsl:call-template>
						</td>
						<td width="11%" valign="top" align="right" class="hdrcol23" nowrap="nowrap">
							<!-- Upphæð m.vsk-->
							<xsl:call-template name="upphaedmvsk"/>
							<!--<xsl:with-param name="text" select="cac:Price/cbc:PriceAmount"/>-->&#160;
						</td>
					</tr>
				</xsl:for-each>
			</table>
			</div>
</xsl:template>
		<!-- Linur -->
<xsl:template name="Samtolur">
<div class="tengilidirkaupanda">
<table width="100%" cellspacing="0" cellpadding="0" summary="Totals2" border="0">
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>					
					<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">LineExtensionAmount:</td>
					<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
						<xsl:value-of select='format-number(sum(cac:CreditNoteLine/cbc:LineExtensionAmount), "###.###,##", "IcelandicNumber")'/>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
				<xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='true']">
					<tr>
						<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
						<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">
							<xsl:value-of select="cbc:AllowanceChargeReason"/>:</td>
						<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:Amount"/>
							</xsl:call-template>						</td>
						<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
					</tr>
				</xsl:for-each>
				<xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='false']">
					<tr>
						<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>						
						<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">
							<xsl:value-of select="cbc:AllowanceChargeReason"/>:</td>
						<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">- 
							<xsl:call-template name="icenumberdecdef">
								<xsl:with-param name="text" select="cbc:Amount"/>
							</xsl:call-template>						</td>
						<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
					</tr>
				</xsl:for-each>
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>					
					<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">VAT Total:</td>
					<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
						<xsl:value-of select='format-number(sum(cac:TaxTotal/cac:TaxSubtotal/cbc:TaxAmount), "###.###,##", "IcelandicNumber")'/>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
					<td width="120px" class="hdrcol234" align="right" nowrap="nowrap">PayableRoundingAmount:</td>
					<td width="87px" class="hdrcol234" align="right" nowrap="nowrap">
						<xsl:call-template name="icenumberdecdef">
							<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PayableRoundingAmount"/>
						</xsl:call-template>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
				
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>					
					<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">
						<b>TaxInclusiveAmount:</b>
					</td>
					<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
						<b>
							<xsl:value-of select='format-number(round(cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount), "###.###,00", "IcelandicNumber")'/>
						</b>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
<xsl:if test="cac:LegalMonetaryTotal/cbc:PrepaidAmount[.!='']">				
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>	
					<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">PrepaidAmount:</td>
					<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
						<xsl:value-of select='format-number(round(cac:LegalMonetaryTotal/cbc:PrepaidAmount), "###.###,00", "IcelandicNumber")'/>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
				
				<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>	
					<td width="120px" class="hdrcol234" align="right" nowrap="nowrap">

					</td>
					<td width="87px" class="hdrcol234" align="right" nowrap="nowrap">

					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
				</xsl:if>
								<tr>
					<td width="70px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>	
					<td width="120px" class="hdrcol23" align="right" nowrap="nowrap">
						<b>PayableAmount:</b>
					</td>
					<td width="87px" class="hdrcol23" align="right" nowrap="nowrap">
						<b>
							<xsl:value-of select='format-number(round(cac:LegalMonetaryTotal/cbc:PayableAmount), "###.###,00", "IcelandicNumber")'/>
						</b>
					</td>
					<td width="104px" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				</tr>
			</table>

</div>
	</xsl:template>

<xsl:template name="SkattarOgAfslnytt">
<div class="tengilidirseljanda">
						<div class="hdrcolSmall" align="left" nowrap="nowrap">&#160;&#160;&#160;&#160; No. of lines: <xsl:value-of select='format-number(count(cac:CreditNoteLine/cbc:ID), "###.###", "IcelandicNumber")'/>&#160;&#160;&#160;&#160;&#160;&#160;&#160;*Unit Price is excluding VAT.</div><br/>
						<table width="100%" cellspacing="1" cellpadding="2" class="ntable" summary="VAT totals">
							<thead>
								<tr height="25">
									<td align="right" class="hdrcol22" colspan="3">VAT Category / %</td>
									<td align="right" class="hdrcol22">TaxableAmount</td>
									<td align="right" class="hdrcol22">VAT amount&#160;</td>
								</tr>
							</thead>
							<tbody>
								<xsl:for-each select="cac:TaxTotal">
									<xsl:for-each select="cac:TaxSubtotal">
										<tr height="20">
											<td width="4%" align="right"/>
											<td width="6%" align="left" class="hdrcol23">
												<xsl:value-of select="cac:TaxCategory/cbc:ID"/>
											</td>
											<td width="20%" align="right" class="hdrcol23">(<xsl:call-template name="icenumberdecdef2">
													<xsl:with-param name="text" select="cac:TaxCategory/cbc:Percent"/>
												</xsl:call-template>%)
                      </td>
											<td width="33%" align="right" class="hdrcol23">
												<xsl:call-template name="icenumberdecdef">
													<xsl:with-param name="text" select="cbc:TaxableAmount"/>
												</xsl:call-template>
											</td>
											<td width="37%" align="right" class="hdrcol23">
												<xsl:call-template name="icenumberdecdef">
													<xsl:with-param name="text" select="cbc:TaxAmount"/>
												</xsl:call-template>&#160;
                      </td>
										</tr>
									</xsl:for-each>
								</xsl:for-each>
								<tr height="25">
									<td align="right" class="hdrcol23" style="border-top: 0.1em solid #CDA1B6;" colspan="5">
										<b>VAT total Amount:&#160;&#160;<xsl:value-of select='format-number(sum(cac:TaxTotal/cac:TaxSubtotal/cbc:TaxAmount), "###.###,##", "IcelandicNumber")'/>&#160;</b>
									</td>
								</tr>
								<xsl:if test="(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReasonCode) or (cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReason) [.!='']">
								<tr height="25">
									<td align="right" class="hdrcol23" style="border-top: 0.1em solid #CDA1B6;" colspan="5">
										<b>TaxExemptionReasonCode:
										<xsl:if test="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReasonCode[.!='']">
										<xsl:value-of select="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReasonCode"/>
										</xsl:if>
										<xsl:if test="(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReasonCode) and (cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReason) [.!='']">
										-
										</xsl:if>
										<xsl:if test="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReason[.!='']">
										<xsl:value-of select="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TaxExemptionReason"/>
										</xsl:if>
										</b>
									</td>
								</tr>
								</xsl:if>
							</tbody>
						</table>


		<!-- skattarogafsl -->
		</div>
	</xsl:template>
	
	
	<!-- template Linur -->
	
	<xsl:template name="Annad">
		<div id="sidaannad" class="annadsidafalid">
			<div class="content-parent">
				<div class="content-parent-t">
					<div class="content-parent-t-r">
						<div class="ntable2">
							<!--class="content-parent-t-l"-->
							<div onclick="toggleContainer('annad', 'annadopencllink', 'Show details', 'Hide details', 'closedlink', 'openlink', 'sidaannad', 'annadsidafalid', 'annadsida');" class="openlink" id="annadopencllink">Show details</div>
						</div>
					</div>
				</div>
			</div>
			<div class="content-parent-main">
				<!-- Comment Content Start -->
				<div class="content-content">
					<div id="annad" class="annadfela">
						<div class="floatkassi">
							<xsl:call-template name="TengilidurSeljanda"/>
							<xsl:call-template name="TengilidurKaupanda"/>
						</div><p class="clear" />
						<xsl:call-template name="AnnadItarupplLinu"/>
						<xsl:call-template name="AnnadItarupplLinuAfls"/>
						<!--<xsl:call-template name="AnnadGreidsluSkilmalar"/>
            <xsl:call-template name="AnnadItarupplLinur"/>-->
						<xsl:call-template name="AnnadVidhengi2"/>
					</div>
					<!-- annad -->
				</div>
			</div>
			<div class="content-parent-b">
				<div class="content-parent-b-r">
					<div class="content-parent-b-l"/>
				</div>
			</div>
		</div>
		<!-- sidaannad -->
	</xsl:template>
	<!-- template Annad -->
	
	<!-- template AnnadGreidsluuppl -->
	<xsl:template name="TengilidurSeljanda">	
		<xsl:if test="(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FirstName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:JobTitle) or (cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone) or
(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax) or
(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or
(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or
(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID) [.!='']">
			<div class="tengilidirseljanda">	
			<div class="itarupplBig">Additional seller information:</div>			
			<div class="minifloat">	
				<xsl:choose>
					<xsl:when test="(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FirstName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:JobTitle) [.!='']">
						<div>&#160;<b>SupplierParty Person:</b>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FirstName"/>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:MiddleName"/>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:FamilyName"/>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:JobTitle"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail[.!='']">
					<div>&#160;E-mail: <xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>&#160;Tel: <xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>&#160;Fax: <xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or (cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or (cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) [.!='']">
						<div>
							<b>&#160;PartyLegalEntity:</b>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
						</div>
						<xsl:choose>
							<xsl:when test="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, <xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity[.!='']">
										<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity"/>, </xsl:if>
									<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<div>
						<b>&#160;PostalAddressID:</b>
					</div>
					<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
					<div>
						<b>&#160;SupplierPartyID:</b>
					</div>
					<div>&#160;<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
			</div>
</div>
</xsl:if>
	</xsl:template>
	<!-- template tengiliður seljanda-->
	<xsl:template name="TengilidurKaupanda">
		<xsl:if test="(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FirstName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:JobTitle) or (cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telephone) or
(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telefax) or
(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or
(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or
(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID) [.!='']">		
			<div class="tengilidirkaupanda">
			<div class="itarupplBig">Additional buyer information:</div>			
			<div class="minifloat">
				
				
				<xsl:choose>
					<xsl:when test="(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FirstName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:MiddleName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FamilyName) or (cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:JobTitle) [.!='']">
						<div>&#160;<b>BuyerParty Person:</b>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FirstName"/>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:MiddleName"/>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:FamilyName"/>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:JobTitle"/>
						</div>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail[.!='']">
					<div>&#160;E-mail: <xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telephone[.!='']">
					<div>&#160;Tel:: <xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telefax[.!='']">
					<div>&#160;Fax: <xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telefax"/>
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) or (cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity) or (cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) [.!='']">
						<div>
							<b>&#160;PartyLegalEntity:</b>
						</div>
						<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
						</div>
						<xsl:choose>
							<xsl:when test="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, <xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity[.!='']">
										<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentity"/>, </xsl:if>
									<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>

				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID[.!='']">
					<div>
						<b>&#160;PostalAddressID:</b>
					</div>
					<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
					<div>
						<b>&#160;CustomerPartyID:</b>
					</div>
					<div>&#160;<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
			</div>
			</div>
			</xsl:if>
	</xsl:template>
	<!-- template tengiliður kaupanda-->
	<xsl:template name="AnnadItarupplLinu">
		<table>
			<tr height="10">
				<td/>
			</tr>
		</table>
		<div class="itarupplBig">Additional line information:</div>
		<table class="ntable" cellpadding="1" cellspacing="1" width="100%" summary="Additional line information" border="0">
			<tr height="25">
				<td width="1%" class="hdrcol22" align="center" nowrap="nowrap">&#160;</td>
				<td width="3%" class="hdrcol22" align="left" nowrap="nowrap">&#160;</td>
				<td width="16%" class="hdrcol22" align="left" nowrap="nowrap">SellerItemID</td>
				<td width="16%" class="hdrcol22" align="left" nowrap="nowrap">StandardItemID</td>
				<td width="16%" class="hdrcol22" align="left">CommodityClassification</td>
				<td width="16%" class="hdrcol22" align="left" style="padding-right: 0.4em;" nowrap="nowrap"></td>
				<td width="16%" class="hdrcol22" align="left" nowrap="nowrap"></td>
				<td width="15%" class="hdrcol22" align="left" nowrap="nowrap"></td>
				<td width="1%" class="hdrcol22" align="left" nowrap="nowrap">&#160;</td>
			</tr>
			<xsl:for-each select="cac:CreditNoteLine">
				<tr>
					<td class="hdrcol23" align="left" nowrap="nowrap">&#160;</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer -->
						<xsl:value-of select="cbc:ID"/>
					</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer -->
						<xsl:value-of select="cac:Item/cac:SellersItemIdentification/cbc:ID"/>
					</td>
					<td valign="top" align="left" class="hdrcol23">
						<!-- Lýsing -->
						<xsl:value-of select="cac:Item/cac:StandardItemIdentification/cbc:ID"/>
					</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer -->
						<xsl:value-of select="cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode"/>
					</td>
					<td valign="top" align="left" class="hdrcol23">
						<!-- Lýsing -->

					</td>
					<td valign="top" align="left" class="hdrcol23" nowrap="nowrap">
						<!-- vörunúmer -->

					</td>
					<td valign="top" align="left" class="hdrcol23">
						<!-- Lýsing -->

					</td>
					<td valign="top" align="left" class="hdrcol23"/>
				</tr>
				<xsl:choose>
					<xsl:when test="cac:Item/cbc:Description[.!='']">
						<tr>
							<td/>
							<td colspan="7" class="hdrcol23">
								<xsl:value-of select="cac:Item/cbc:Description"/>
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
	<xsl:template name="AnnadItarupplLinuAfls">
		<xsl:if test="cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:ChargeIndicator">
			<table>
				<tr height="10">
					<td/>
				</tr>
			</table>
			<div class="itarupplBig">InvoiceLine Price/AllowanceCharge (informative)</div>
			<table class="ntable" cellpadding="1" cellspacing="1" width="100%" summary="Price/AllowanceCharge (informative)" border="0">
				<xsl:call-template name="AfslHeader"/>
				<xsl:for-each select="cac:CreditNoteLine/cac:Price/cac:AllowanceCharge">
					<xsl:if test=".!=''">
						<tr height="25">
							<td class="hdrcol23" align="left" nowrap="nowrap">&#160;&#160;<xsl:value-of select="../../cbc:ID"/>.</td>
							<td class="hdrcol23" align="left" nowrap="nowrap">
								<xsl:value-of select="../../cac:Item/cac:SellersItemIdentification/cbc:ID"/>
							</td>
							<td class="hdrcol23" align="left" nowrap="nowrap">
								<xsl:call-template name="afslatturgjold">
									<xsl:with-param name="afsl" select="cbc:ChargeIndicator"/>
								</xsl:call-template>
							</td>
							<xsl:if test="$erpro > 0">
								<td class="hdrcol23" align="left" nowrap="nowrap">
									<xsl:choose>
										<xsl:when test="cbc:MultiplierFactorNumeric">
											<xsl:value-of select='format-number(cbc:MultiplierFactorNumeric *100,"###.###,0", "IcelandicNumber")'/>
										</xsl:when>
										<xsl:otherwise>&#160;</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:if>
							<xsl:if test="$ergrunnupph > 0">
								<td class="hdrcol23" align="right" nowrap="nowrap">
									<xsl:choose>
										<xsl:when test="cbc:BaseAmount">
											<xsl:value-of select='format-number(cbc:BaseAmount,"###.###,00", "IcelandicNumber")'/>
										</xsl:when>
										<xsl:otherwise>&#160;</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:if>
							<td class="hdrcol23" align="right" nowrap="nowrap">
								<xsl:value-of select='format-number(cbc:Amount,"###.###,00", "IcelandicNumber")'/>&#160;&#160;</td>
							<td class="hdrcol23" align="left" nowrap="nowrap">
								<xsl:value-of select="cbc:AllowanceChargeReason"/>
							</td>
							<xsl:if test="$erpro = 0">
								<td class="hdrcol23" align="left" nowrap="nowrap">&#160;</td>
							</xsl:if>
							<xsl:if test="$ergrunnupph = 0">
								<td class="hdrcol23" align="left" nowrap="nowrap">&#160;</td>
							</xsl:if>
						</tr>
					</xsl:if>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>
	

	<!-- template AnnadGreidsluuppl -->
	<xsl:template name="AfslHeader">
		<tr height="25">
			<td width="8%" class="hdrcol22" align="left" nowrap="nowrap">&#160;&#160;LineNo.</td>
			<td width="13%" class="hdrcol22" align="left" nowrap="nowrap">SellerItemID</td>
			<td width="13%" class="hdrcol22" align="left" nowrap="nowrap">Allowance/Charge</td>
			<xsl:if test="$erpro > 0">
				<td width="8%" class="hdrcol22" align="left">Percentage </td>
			</xsl:if>
			<xsl:if test="$ergrunnupph > 0">
				<td width="13%" class="hdrcol22" align="right">BaseAmount</td>
			</xsl:if>
			<td width="13%" class="hdrcol22" align="right" nowrap="nowrap">Amount&#160;&#160;</td>
			<td width="32%" class="hdrcol22" align="left" nowrap="nowrap">Reason text</td>
			<xsl:if test="$erpro = 0">
				<td width="8%" class="hdrcol22" align="Right">&#160;</td>
			</xsl:if>
			<xsl:if test="$ergrunnupph = 0">
				<td width="13%" class="hdrcol22" align="right">&#160;</td>
			</xsl:if>
		</tr>
	</xsl:template>
	<xsl:template name="ProsentaHeader">
		<xsl:param name="count" select="0"/>
		<xsl:for-each select="n1:Invoice/cac:CreditNoteLine/cac:Price/cac:AllowanceCharge">
			<xsl:if test="cbc:MultiplierFactorNumeric[.!='']">
				<xsl:call-template name="ProsentaHeader">
					<xsl:with-param name="count" select="$count + 1"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="$count > 0">1</xsl:if>
		<xsl:if test="$count = 0">0</xsl:if>
	</xsl:template>
	<xsl:template name="GrunnupphaedHeader">
		<xsl:param name="count2" select="0"/>
		<xsl:for-each select="n1:Invoice/cac:CreditNoteLine/cac:Price/cac:AllowanceCharge">
			<xsl:if test="cbc:BaseAmount[.!='']">
				<xsl:call-template name="GrunnupphaedHeader">
					<xsl:with-param name="count2" select="$count2 + 1"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="$count2 > 0">1</xsl:if>
		<xsl:if test="$count2 = 0">0</xsl:if>
	</xsl:template>
	
	
		<xsl:template name="AnnadVidhengi2">
			<xsl:if test="cac:AdditionalDocumentReference/cbc:ID[.!='']">
				<table>
					<tr height="10">
						<td/>
					</tr>
				</table>
				<div class="itarupplBig">Attachment:</div>
				<table class="ntable" cellpadding="1" cellspacing="0" width="100%" summary="Viðhengi" border="0">
					<tr height="25">
						<td width="10%" class="hdrcol22" align="left" nowrap="nowrap">&#160;&#160;ID.</td>
						<td width="18%" class="hdrcol22" align="left" nowrap="nowrap">Type</td>
						<td width="72%" class="hdrcol22" align="left" nowrap="nowrap">Reference</td>
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
	
	
	<xsl:template name="kennitala">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select="concat(substring($text, 1, 6), '-', substring($text, 7, 4))"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template höfuðbók -->
	<xsl:template name="hofudbok">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="contains($text,':')">
				<xsl:value-of select="substring-after($text,':')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
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
	<xsl:template name="afslatturgjold">
		<xsl:param name="afsl" select="cac:Price/cac:AllowanceCharge/cbc:ChargeIndicator"/>
		<xsl:choose>
			<xsl:when test="$afsl[.='true']">
        Charge
      </xsl:when>
			<xsl:when test="$afsl[.='false']">
        Allowance
      </xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="afslatturgjoldlinum">
		<xsl:param name="afsl" select="cbc:ChargeIndicator"/>
		<xsl:choose>
			<xsl:when test="$afsl[.='true']">
        Gjöld
      </xsl:when>
			<xsl:when test="$afsl[.='false']">
        Afsláttur
      </xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>	
	
	<!-- template icenumberdef -->
	<xsl:template name="upphaedmvsk">
		<xsl:param name="vskkodi" select="cac:Item/cac:ClassifiedTaxCategory/cbc:ID"/>
		<xsl:param name="amount" select="cac:CreditNoteLine/cbc:LineExtensionAmount"/>
		<xsl:choose>
			<xsl:when test="$vskkodi[.='S']">
				<!--<xsl:value-of select='format-number(cbc:LineExtensionAmount * 1.245, "###.##0,00", "IcelandicNumber")'/>-->
				<xsl:call-template name="vskupphaedS">
					<xsl:with-param name="upph" select="cbc:LineExtensionAmount"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$vskkodi[.='AA']">
				<!--<xsl:value-of select='format-number(cbc:LineExtensionAmount * 1.07, "###.##0,00", "IcelandicNumber")'/>-->
				<xsl:call-template name="vskupphaedAA">
					<xsl:with-param name="upph" select="cbc:LineExtensionAmount"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(cbc:LineExtensionAmount, "###.##0,00", "IcelandicNumber")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template icenumberdef -->
	<xsl:template name="vskupphaedS">
		<xsl:param name="upph"/>
		<xsl:for-each select="../cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
			<xsl:choose>
				<xsl:when test="cbc:ID[.='S']">
					<xsl:variable name="vskprosent" select="cbc:Percent"/>
					<xsl:value-of select='format-number($upph * (1+($vskprosent)*0.01), "###.##0,00", "IcelandicNumber")'/>
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<!-- template icenumberdef -->
	<xsl:template name="vskupphaedAA">
		<xsl:param name="upph"/>
		<xsl:for-each select="../cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
			<xsl:choose>
				<xsl:when test="cbc:ID[.='AA']">
					<xsl:variable name="vskprosent" select="cbc:Percent"/>
					<xsl:value-of select='format-number($upph * (1+($vskprosent)*0.01), "###.##0,00", "IcelandicNumber")'/>
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<!-- template icenumberdef -->
	<xsl:template name="Einingar">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.='C62']">stk</xsl:when>
			<xsl:when test="$text[.='KGS']">kg</xsl:when>
			<xsl:when test="$text[.='MTR']">m</xsl:when>
			<xsl:when test="$text[.='LTR']">l</xsl:when>
			<xsl:when test="$text[.='MTK']">m²</xsl:when>
			<xsl:when test="$text[.='MTQ']">m³</xsl:when>
			<xsl:when test="$text[.='KMT']">km</xsl:when>
			<xsl:when test="$text[.='TNE']">t</xsl:when>
			<xsl:when test="$text[.='KWH']">kWh</xsl:when>
			<xsl:when test="$text[.='DAY']">d</xsl:when>
			<xsl:when test="$text[.='HUR']">klst</xsl:when>
			<xsl:when test="$text[.='MIN']">min</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template Greidslutegund -->
</xsl:stylesheet>
