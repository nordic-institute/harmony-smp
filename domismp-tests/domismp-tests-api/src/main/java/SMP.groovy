import groovy.sql.Sql;
import java.sql.SQLException;
import java.security.MessageDigest;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.custommonkey.xmlunit.*;

import java.security.PublicKey;
import java.security.cert.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext
import javax.xml.crypto.dsig.XMLSignatureFactory
import javax.xml.crypto.dsig.XMLSignature
import java.util.Iterator;
// Giving error Could not find artifact xmlbeans:xbean:jar:fixed-2.4.0 in cefdigital-releases (https://ec.europa.eu/digital-building-blocks/artifact/content/repositories/eDelivery/)
//import sun.misc.IOUtils;
import java.util.Base64

import java.text.SimpleDateFormat
import com.eviware.soapui.support.GroovyUtils
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep
import groovy.json.JsonSlurper
import groovy.json.JsonOutput



class SMP implements  AutoCloseable
{
	// Database parameters
	def sqlConnection;
	def url;
	def driver;
	def testDatabase="false";
	def messageExchange;
	def context
	def log;
	static def DEFAULT_LOG_LEVEL = true;

	// Table allocated to store the data/parameters of the request.
	def requestDataTable = [];

	// Table allocated to store the data/parameters of the response.
	def responseDataTable = [];

	// Table allocated to store the intermediate data/parameters.
	def tempoContainer = [];

	// String allocated to extract parts of XML.
	def tempoString = null;

	// Table allocated to store metadata.
	def tablebuffer = [];

	//Signature Algorithm
	def String SIGNATURE_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
	def String SIGNATURE_XMLNS = "http://www.w3.org/2000/09/xmldsig#";

	// Node container
	def Node nodeContainer = null;

	def dbUser=null
	def dbPassword=null

	static def LOGGED_USER=null
	static def XSFRTOKEN=null
	static def USERID=null
	static def SYSTEM_USER="system"
	static def SYSTEM_PWD="123456"

	// Constructor of the SMP Class
	SMP(log,messageExchange,context) {
		debugLog("Create SMP instance", log)
		this.log = log
		this.messageExchange = messageExchange;
		this.context=context;
		this.url=context.expand( '${#Project#jdbc.url}' );
		driver=context.expand( '${#Project#jdbc.driver}' );
		testDatabase=context.expand( '${#Project#testDB}' );
		dbUser=context.expand( '${#Project#dbUser}' );
		dbPassword=context.expand( '${#Project#dbPassword}' );
		sqlConnection = null;
		debugLog("SMP instance created", log)
	}

	// Class destructor
	/*
    void finalize(){
        log.info "Test finished."
    }*/
	void close(){
		log.info "Test finished."
	}

//=========================================================================
//======================== Logging functions ==============================
//=========================================================================

//------------------------ Log debug informations -------------------------
	static def void debugLog(logMsg, logObject,  logLevel = DEFAULT_LOG_LEVEL) {
		if (logLevel.toString()=="1" || logLevel.toString() == "true")
			logObject.info (logMsg)
	}


//=========================================================================
//======================= Database functions ==============================
//=========================================================================

//-------------------------- Open DB connection ---------------------------
	def openConnection(){
		debugLog("Open DB connections", log)
		if(testDatabase.toLowerCase()=="true") {
			if (sqlConnection) {
				debugLog("DB connection seems already open", log)
			}
			else {
				try{
					if(driver.contains("oracle")){
						// Oracle DB
						GroovyUtils.registerJdbcDriver( "oracle.jdbc.driver.OracleDriver" )
					}else{
						// Mysql DB (assuming fallback: currently, we use only those 2 DBs ...)
						GroovyUtils.registerJdbcDriver( "com.mysql.jdbc.Driver" )
					}
					debugLog("Open connection with url ${url} dbUser=${dbUser} pass=${dbPassword} driver=${driver} |", log)
					sqlConnection = Sql.newInstance(url, dbUser, dbPassword, driver)

				}
				catch (SQLException ex)
				{
					assert 0,"SQLException occurred: " + ex;
				}
			}
		}
		else // testDatabase.toLowerCase()=="false")
			assert 0, "testDatabase param is set not set to true value - would not try to open DB connection"
	}

//------------------------- Close DB connection ---------------------------
	// Close the DB connection opened previously
	def closeConnection(){
		debugLog("Close DB connection", log)
		if(testDatabase.toLowerCase()=="true"){
			if(sqlConnection){
				sqlConnection.connection.close();
				sqlConnection = null;
			}
		}
		debugLog("DB connection closed", log)
	}

//----------------------- Run list of sql queries -------------------------
	def executeListOfSqlQueries(String[] sqlQueriesList) {
		def connectionOpenedInsideMethod = false

		if (!sqlConnection) {
			debugLog("Method executed without connections open to the DB - try to open connection", log)
			openConnection()
			connectionOpenedInsideMethod = true
		}

		for (query in sqlQueriesList) {
			debugLog("Executing SQL query: " + query, log)
			try{
				sqlConnection.execute query
			}
			catch (SQLException ex){
				closeConnection();
				assert 0,"SQLException occurred: " + ex;
			}
		}

		if (connectionOpenedInsideMethod) {
			debugLog("Connection to DB opened during method execution - close opened connection", log)
			closeConnection()
		}
	}

//----------- Run list of sql queries and return first row ----------------
	def executeSqlAndReturnFirstRow(String query) {
		def connectionOpenedInsideMethod = false
		def res

		if (!sqlConnection) {
			debugLog("Method executed without connections open to the DB - try to open connection", log)
			openConnection()
			connectionOpenedInsideMethod = true
		}

		debugLog("Executing SQL query: " + query, log)
		debugLog("Executing SQL query: " + (sqlConnection == null), log)
		try{
			res = sqlConnection.firstRow query
		}
		catch (SQLException ex){
			closeConnection();
			assert 0,"SQLException occurred: " + ex;
		}

		if (connectionOpenedInsideMethod) {
			debugLog("Connection to DB opened during method execution - close opened connection", log)
			closeConnection()
		}
		return res
	}

	def findDomainName() {
		def result = executeSqlAndReturnFirstRow('SELECT DOMAIN_CODE FROM SMP_DOMAIN order by ID')
		return result.domain_code
	}


//=========================================================================
//================== Test results assessment functions ====================
//=========================================================================

//------------------ Initialize the parameters names ----------------------
	def initParameters(String testType, String indicator){
		if(indicator.toLowerCase()=="request"){
			switch(testType.toLowerCase()){
				case "servicemetadata":
					requestDataTable[0]=["0","businessIdSchemeRequest"];
					requestDataTable[1]=["0","ParticipantIdentifierRequest"];
					requestDataTable[2]=["0","documentIdentSchemeRequest"];
					requestDataTable[3]=["0","documentIdentifierRequest"];
					break;
				case "servicegroup":
					requestDataTable[0]=["0","businessIdSchemeRequest"];
					requestDataTable[1]=["0","ParticipantIdentifierRequest"];
					requestDataTable[2]=["0","Extension"];
					requestDataTable[3]=["0","Certificate"];
					break;
				case "redirection":
					requestDataTable[0]=["0","redirectUrl"];
					requestDataTable[1]=["0","CertificateUID"];
					break;
				default:
					log.info "Unknown operation";
			}
		}
		if(indicator.toLowerCase()=="response"){
			switch(testType){
				case "servicemetadata":
					responseDataTable[0]=["0","businessIdSchemeResponse"];
					responseDataTable[1]=["0","ParticipantIdentifierResponse"];
					responseDataTable[2]=["0","documentIdentSchemeRequest"];
					responseDataTable[3]=["0","documentIdentifierRequest"];
					break;
				case "servicegroup":
					responseDataTable[0]=["0","businessIdSchemeResponse"];
					responseDataTable[1]=["0","ParticipantIdentifierResponse"];
					responseDataTable[2]=["0","Extension"];
					responseDataTable[3]=["0","Certificate"];
					break;
				case "redirection":
					responseDataTable[0]=["0","redirectUrl"]
					responseDataTable[1]=["0","CertificateUID"]
					break;
				default:
					log.info "Unknown operation";
			}
		}
	}

//--------------------- Extract request parameters ------------------------
	def extractRequestParameters(String testType, String testStepName="false"){
		def requestContent = null;

		// Load the Request
		requestContent = messageExchange.getOperation();
		assert (requestContent!=null),locateTest()+"Error: Not possible to extract the request content. Request content extracted is empty.";

		// Browse the REST request
		extractFromURL(requestContent.toString());

		switch(testType.toLowerCase()){

		// Extract the Participant Identifier and the Business Identifier Scheme from the Request
			case "servicegroup":
				debugLog("In extractRequestParameters tempoContainer: $tempoContainer", log)
				initParameters("servicegroup","request");
				requestDataTable[0][0] = tempoContainer[0];
				requestDataTable[1][0] = tempoContainer[1];
				if(testStepName.toLowerCase()!="false"){
					requestDataTable[2][0] = extractExtValues(extractTextFromReq(testStepName));
					requestDataTable[3][0] = extractNodeValue("CertificateIdentifier",extractTextFromReq(testStepName));
				}
				break;

				// Extract the Participant Identifier and the document from the Request
			case "servicemetadata":
			case "signature":
				initParameters("servicemetadata","request");
				requestDataTable[0][0] = tempoContainer[0];
				requestDataTable[1][0] = tempoContainer[1];
				requestDataTable[2][0] = tempoContainer[2];
				requestDataTable[3][0] = tempoContainer[3];
				break;


			case "redirection":
				initParameters("redirection","request");
				break;

			default:
				if(testType.toLowerCase()=="contenttype"){
					// Do nothing
					break;
				}
				assert(0), locateTest()+"Error: -extractRequestParameters-Unknown operation: "+testType+"."+" Possible operations: serviceGroup, serviceMetadata, Redirection, Signature, contentType";
				break;
		}
	}

//-------------------- Fetch expected results values ----------------------
	def static fetchExpectedValues(context, log, String testType){
	
		def expectedParameters=[:]
		
		switch(testType.toLowerCase()){
			case "sgextension":
				expectedParameters["extension"]=getExtensionFromString(context, log,getSoapUiCustomProperty(log, context, "PutResourceRequestExtTemplate", "testsuite",false))
			case "servicegroup":
			case "resource":
				expectedParameters["partId"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierValue", "testcase",false)
				expectedParameters["partScheme"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierScheme", "testcase",true)
				expectedParameters["version"]=getSoapUiCustomProperty(log, context, "version", "testsuite",false)
				break;
			case "subresourcemulti":
			case "servicemetadatamulti":
				expectedParameters["metadata"]=getMetadataFromString(context, log, getSoapUiCustomProperty(log, context, "PutSubresourceRequestTemplateMulti", "testsuite",false))
			case "servicemetadata":
			case "subresource":
				expectedParameters["partId"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierValue", "testcase",false)
				expectedParameters["partScheme"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierScheme", "testcase",true)
				expectedParameters["version"]=getSoapUiCustomProperty(log, context, "version", "testsuite",false)
				expectedParameters["serviceId"]=getSoapUiCustomProperty(log, context, "SubresourceIdentifierValue", "testcase",false)
				expectedParameters["serviceScheme"]=getSoapUiCustomProperty(log, context, "SubresourceIdentifierScheme", "testcase",false)
				if(expectedParameters["metadata"]==null){
					expectedParameters["metadata"]=getMetadataFromString(context, log, getSoapUiCustomProperty(log, context, "PutSubresourceRequestTemplate", "testsuite",false))
				}
				break;
			case "redirection":
				expectedParameters["partId"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierValue", "testcase",false)
				expectedParameters["partScheme"]=getSoapUiCustomProperty(log, context, "ResourceIdentifierScheme", "testcase",true)
				expectedParameters["version"]=getSoapUiCustomProperty(log, context, "version", "testsuite",false)
				expectedParameters["serviceId"]=getSoapUiCustomProperty(log, context, "SubresourceIdentifierValue", "testcase",false)
				expectedParameters["serviceScheme"]=getSoapUiCustomProperty(log, context, "SubresourceIdentifierScheme", "testcase",false)
				expectedParameters["redirectUrl"]=getSoapUiCustomProperty(log, context, "redirectUrl", "testcase",false)
				break;	
			case "contenttype":
				expectedParameters["contenttype"]=getSoapUiCustomProperty(log, context, "contenttype", "testcase",false)
				expectedParameters["charset"]=getSoapUiCustomProperty(log, context, "charset", "testcase",false)
				break;	
			case "signature":
				expectedParameters["smpsignaturemethod"]=getSoapUiCustomProperty(log, context, "smpsignaturemethod", "testsuite",false)
				expectedParameters["smpsignaturesubj"]=getSoapUiCustomProperty(log, context, "smpsignaturesubj", "testsuite",true)
				break;				
			default:
				assert(0), "Error: -fetchExpectedValues-Unknown operation: "+testType+"."+" Possible operations: resource, servicegroup, subresource, servicemetadata, Redirection, Signature, contentType.";
				break;
		}
		return expectedParameters
	}


//------------------------- Verify tests results --------------------------
	def static verifyTestResults(context, log, messageExchange, String testType){
		debugLog("Verifying test results ...", log)
		def responseParameters=[:]
		def expectedParameters=[:]
		responseParameters=retrieveResponseParameters(context, log, messageExchange, testType)		
		expectedParameters=fetchExpectedValues(context, log, testType)
		debugLog("responseParameters="+responseParameters, log)
		debugLog("expectedParameters="+expectedParameters, log)
		switch(testType.toLowerCase()){
			case "sgextension":
				assert(compare2XMLs(log,expectedParameters["extension"], responseParameters["extension"])), " Error: extension in the response does not match the expected value "
			case "servicegroup":
			case "resource":
				assert(expectedParameters["partId"].toLowerCase().equals(responseParameters["partId"].toLowerCase())),"Error: ParticipantID response value: "+responseParameters["partId"]+" does not match expected value: "+expectedParameters["partId"] 
				assert(expectedParameters["partScheme"].toLowerCase().equals(responseParameters["partScheme"].toLowerCase())),"Error: ParticipantScheme response value: "+responseParameters["partScheme"]+" does not match expected value: "+expectedParameters["partScheme"]
				assert(expectedParameters["version"].toLowerCase().equals(responseParameters["version"].toLowerCase())),"Error: version response value: "+responseParameters["version"]+" does not match expected value: "+expectedParameters["version"]
				break;
			case "subresourcemulti":
			case "servicemetadatamulti":
			case "servicemetadata":
			case "subresource":
				assert(expectedParameters["partId"].toLowerCase().equals(responseParameters["partId"].toLowerCase())),"Error: ParticipantID response value: "+responseParameters["partId"]+" does not match expected value: "+expectedParameters["partId"] 
				assert(expectedParameters["partScheme"].toLowerCase().equals(responseParameters["partScheme"].toLowerCase())),"Error: ParticipantScheme response value: "+responseParameters["partScheme"]+" does not match expected value: "+expectedParameters["partScheme"]
				assert(expectedParameters["version"].toLowerCase().equals(responseParameters["version"].toLowerCase())),"Error: version response value: "+responseParameters["version"]+" does not match expected value: "+expectedParameters["version"]
				assert(expectedParameters["serviceId"].toLowerCase().equals(responseParameters["serviceId"].toLowerCase())),"Error: serviceId response value: "+responseParameters["serviceId"]+" does not match expected value: "+expectedParameters["serviceId"] 
				assert(expectedParameters["serviceScheme"].toLowerCase().equals(responseParameters["serviceScheme"].toLowerCase())),"Error: serviceScheme response value: "+responseParameters["serviceScheme"]+" does not match expected value: "+expectedParameters["serviceScheme"]
				assert(compare2XMLs(log,expectedParameters["metadata"], responseParameters["metadata"])), " Error: metadata in the response does not match the expected value "
				break;
			case "redirection":
				assert(expectedParameters["partId"].toLowerCase().equals(responseParameters["partId"].toLowerCase())),"Error: ParticipantID response value: "+responseParameters["partId"]+" does not match expected value: "+expectedParameters["partId"] 
				assert(expectedParameters["partScheme"].toLowerCase().equals(responseParameters["partScheme"].toLowerCase())),"Error: ParticipantScheme response value: "+responseParameters["partScheme"]+" does not match expected value: "+expectedParameters["partScheme"]
				assert(expectedParameters["version"].toLowerCase().equals(responseParameters["version"].toLowerCase())),"Error: version response value: "+responseParameters["version"]+" does not match expected value: "+expectedParameters["version"]
				assert(expectedParameters["serviceId"].toLowerCase().equals(responseParameters["serviceId"].toLowerCase())),"Error: serviceId response value: "+responseParameters["serviceId"]+" does not match expected value: "+expectedParameters["serviceId"] 
				assert(expectedParameters["serviceScheme"].toLowerCase().equals(responseParameters["serviceScheme"].toLowerCase())),"Error: serviceScheme response value: "+responseParameters["serviceScheme"]+" does not match expected value: "+expectedParameters["serviceScheme"]
				assert(expectedParameters["redirectUrl"].toLowerCase().equals(responseParameters["redirectUrl"].toLowerCase())),"Error: redirectUrl response value: "+responseParameters["redirectUrl"]+" does not match expected value: "+expectedParameters["redirectUrl"]
				break;
			case  "contenttype":
				assert(expectedParameters["contenttype"].toLowerCase().equals(responseParameters["contenttype"].toLowerCase())),"Error: Content-Type response value: "+responseParameters["contenttype"]+" does not match expected value: "+expectedParameters["contenttype"] 
				assert(expectedParameters["charset"].toLowerCase().equals(responseParameters["charset"].toLowerCase())),"Error: Charset response value: "+responseParameters["charset"]+" does not match expected value: "+expectedParameters["charset"] 
				break;
			case  "signature":
				assert(expectedParameters["smpsignaturemethod"].toLowerCase().equals(responseParameters["smpsignaturemethod"].toLowerCase())),"Error: SMP signature method response value: "+responseParameters["smpsignaturemethod"]+" does not match expected value: "+expectedParameters["smpsignaturemethod"] 
				assert(expectedParameters["smpsignaturesubj"].toLowerCase().equals(responseParameters["smpsignaturesubj"].toLowerCase())),"Error: SMP signature subject response value: "+responseParameters["smpsignaturesubj"]+" does not match expected value: "+expectedParameters["smpsignaturesubj"] 
				break;
			default:
				assert(0), "Error: -verifyTestResults-Unknown operation: "+testType+"."+" Possible operations: resource, servicegroup, subresource, servicemetadata, Redirection, Signature, contentType.";
				break;
		}
		debugLog("Test results verified successfully", log)
				
	}

//----------------- Extract and return response parameters ----------------
	def static retrieveResponseParameters(context, log, messageExchange, String testType){
		def parametersMap=[:];
		def ctMap=[];
		def headers=null;
		def allNodes=null;
		// Load the response xml file
		def responseContent = messageExchange.getResponseContentAsXml();
		// Extract the Participant Identifier, the references to the signed metadata and the extensions from the Response
		def ServiceDetails = new XmlSlurper().parseText(responseContent);
		parametersMap["version"]="1.0"
		switch(testType.toLowerCase()){
			case "sgextension":
				parametersMap["extension"]=getExtensionFromString(context, log, responseContent)							
			case "servicegroup":
			case "resource":
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name().toLowerCase().equals("participantid")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@schemeID.text();
					}
					if(it.name().toLowerCase().equals("participantidentifier")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@scheme.text();
					}
					if(it.name().toLowerCase().equals("smpversionid")){
						parametersMap["version"]=it.text();
					}
				}
				break;

			case "subresourcemulti":
			case "servicemetadatamulti":
			case "servicemetadata":
			case "subresource":
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name().toLowerCase().equals("participantid")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@schemeID.text();
					}
					if(it.name().toLowerCase().equals("participantidentifier")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@scheme.text();
					}
					if(it.name().toLowerCase().equals("smpversionid")){
						parametersMap["version"]=it.text();
					}
					if(it.name().toLowerCase().equals("serviceid")){
						parametersMap["serviceId"]=it.text();
						parametersMap["serviceScheme"]=it.@schemeID.text();
					}
					if(it.name().toLowerCase().equals("documentidentifier")){
						parametersMap["serviceId"]=it.text();
						parametersMap["serviceScheme"]=it.@scheme.text();
					}
					parametersMap["metadata"]=getMetadataFromString(context, log, responseContent)
				
				}
				
				break;
			case "redirection":
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name().toLowerCase().equals("participantid")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@schemeID.text();
					}
					if(it.name().toLowerCase().equals("participantidentifier")){
						parametersMap["partId"]=it.text();
						parametersMap["partScheme"]=it.@scheme.text();
					}
					if(it.name().toLowerCase().equals("smpversionid")){
						parametersMap["version"]=it.text();
					}
					if(it.name().toLowerCase().equals("serviceid")){
						parametersMap["serviceId"]=it.text();
						parametersMap["serviceScheme"]=it.@schemeID.text();
					}
					if(it.name().toLowerCase().equals("documentidentifier")){
						parametersMap["serviceId"]=it.text();
						parametersMap["serviceScheme"]=it.@scheme.text();
					}
					if((it.name().toLowerCase().equals("publisheruri")) && (it.parent().name().toLowerCase().equals("redirect"))){
						parametersMap["redirectUrl"]=it.text();
					}
					if(it.name().toLowerCase().equals("redirect")){
						parametersMap["redirectUrl"]=it.@href.text();
					}
				}
				break;
			case "contenttype":
				headers=messageExchange.getResponseHeaders()
				headers.each{ header ->
					if(header.getKey().equals("Content-Type")){
						ctMap=header.getValue()[0].split(";")
						parametersMap["contenttype"]=ctMap[0]
						parametersMap["charset"]=ctMap[1].split("=")[1]
					}
				}
				break;				
			case "signature":
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name().toLowerCase().equals("signaturemethod")){
						parametersMap["smpsignaturemethod"]=it.@Algorithm.text();
					}
					if(it.name().toLowerCase().equals("x509subjectname")){
						parametersMap["smpsignaturesubj"]=it.text();
					}
				}				
				break;
			default:
				assert(0), "Error: -retrieveResponseParameters-Unknown operation: "+testType+"."+" Possible operations: resource, servicegroup, subresource, servicemetadata, Redirection, Signature, contentType.";
				break;
		}
		return parametersMap
	}
	
//--------------------- Get the Metadata from string ----------------------	
	def static String getMetadataFromString(context, log, String input){
		def certValue="";
		def newCertValue=""
		def certId="contentbinaryobject";
		def stringMeta=extractFromXML(removeNamespaces(input),"ProcessMetadata")	
		def details=null
		def allNodes=null

		if(stringMeta.length()==0){
			stringMeta=extractFromXML(removeNamespaces(input),"ProcessList")
			certId="certificate";
		}		
		details = new XmlSlurper().parseText(input);
		allNodes = details.depthFirst().each{
					if(it.name().toLowerCase().equals(certId)){
						certValue=it.text();
					}
		}
		newCertValue=certValue.replaceAll("\\s","")
		return stringMeta.replace(certValue,newCertValue)
	}

//-------------------- Get the Extension from string ----------------------	
	def static String getExtensionFromString(context, log, String input){
		def stringMeta=extractFromXML(removeNamespaces(input),"SMPExtensions")	

		if(stringMeta.length()==0){
			stringMeta=extractFromXML(removeNamespaces(input),"Extension")
		}		

		return stringMeta
	}
	
//---------------------- Extract response parameters ----------------------
// To be depcrecated in the future: use instead "retrieveResponseParameters" function
	def extractResponseParameters(String testType){
		def headerFound = 0;
		def urlRefCounter = 0;
		def allNodes = null;
		// Load the response xml file
		def responseContent = messageExchange.getResponseContentAsXml();
		// Extract the Participant Identifier, the references to the signed metadata and the extensions from the Response
		def ServiceDetails = new XmlSlurper().parseText(responseContent);
		switch(testType.toLowerCase()){
			case "servicegroup":
				initParameters("servicegroup","response");
				urlRefCounter = 4;
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name()== "ParticipantIdentifier"){
						responseDataTable[1][0]=it.text();
						responseDataTable[0][0]=it.@scheme.text();
					}
					if(it.name()== "ServiceMetadataReference"){
						responseDataTable[urlRefCounter]=[it.@href.text(),"ServiceMetadataReference"];
						urlRefCounter+=1;
					}
					/*if(it.name()== "Extension"){
                        responseDataTable[2][0]=it.text();
                        }*/
					if(it.name()== "CertificateIdentifier"){
						responseDataTable[3][0]=it.text();
					}
				}
				// Extract the extension
				responseDataTable[2][0]=extractExtValues(responseContent.toString());
				break;

			case "servicemetadata":
			case "signature":
				tempoString = null;
				initParameters("servicemetadata","response");
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name()== "ParticipantIdentifier"){
						responseDataTable[1][0]=it.text();
						responseDataTable[0][0]=it.@scheme.text();
					}
					if(it.name()== "DocumentIdentifier"){
						responseDataTable[2][0]=it.@scheme.text();
						responseDataTable[3][0]=it.text();
					}
				}
				tempoString = responseContent.toString();
				break;

			case "redirection":
				initParameters("redirection","response");
				allNodes = ServiceDetails.depthFirst().each{
					if(it.name()== "Redirect"){
						responseDataTable[0][0]=it.@href.text();
					}
					if(it.name()== "CertificateUID"){
						responseDataTable[1][0]=it.text();
					}
				}
				assert((responseDataTable[0][0]!=null)&&(responseDataTable[0][0]!="0")), locateTest()+"Error: Redirection is expected but redirect element was not found in the response.";
				assert((responseDataTable[1][0]!=null)&&(responseDataTable[1][0]!="0")), locateTest()+"Error: Redirection is expected but CertificateUID element was not found in the response.";
				break;

			default:
				// content-Type = text/xml
				if(testType.toLowerCase()=="contenttype"){
					for(header in messageExchange.getResponseHeaders()){
						if((header.toString().contains("Content-Type")) && (header.toString().contains("text/xml"))){
							headerFound = 1;
						}
					}
					assert(headerFound==1), locateTest()+"Error: Header content-Type is not found or is not set to text/xml.";
					break;
				}
				assert(0), locateTest()+"Error: -extractResponseParameters-Unknown operation: "+testType+"."+" Possible operations: serviceGroup, serviceMetadata, Redirection, Signature, contentType.";
				break;
		}
	}
	
//--------------------------- Verify tests results ------------------------
// To be deprecated in the future: use instead "verifyTestResults" function
	def verifyResults(String testType, String expectedResult, String testStepName="false", String redirectURL=null, String redirectCer=null, int nRef=0){
		// In case of testType = "servicegroup",
		debugLog("Entering verifyResults method with testType: $testType, expectedResult: $expectedResult, testStepName: $testStepName, redirectURL: $redirectURL, redirectCer: $redirectCer, nRef: $nRef", log)
		def counter = 0;
		def String reqString = null;
		def String extensionRequest = "0";
		def String extensionResponse = "0";
		def sigAlgo = "0";
		debugLog("Befor extractRequestParameters(testType,testStepName)", log)
		extractRequestParameters(testType,testStepName);
		debugLog("After extractRequestParameters(testType,testStepName)", log)
		extractResponseParameters(testType);
		debugLog("After extractResponseParameters($testType)", log)
		switch(testType.toLowerCase()){
			case "servicegroup":
				if(expectedResult.toLowerCase()=="success"){
					while(counter<4){
						if ((counter==2)&&(requestDataTable[2][0]!="0")){
							if(compareXMLs(responseDataTable[counter][0],requestDataTable[counter][0])==false){
								log.error "Extension in request: "+requestDataTable[counter][0];
								log.error "Extension in response: "+responseDataTable[counter][0];
								assert(0), locateTest()+"Error: Extension returned is different from Extension pushed. For details, please refer to logs in red.";
							}
						}else{
							assert(responseDataTable[counter][0].toLowerCase()==requestDataTable[counter][0].toLowerCase()), locateTest()+"Error: in request, "+requestDataTable[counter][1]+"=\""+requestDataTable[counter][0]+"\""+" wheras in response, "+responseDataTable[counter][1]+"=\""+responseDataTable[counter][0]+"\".";
						}
						counter++;
					}
					counter = 4;
					if(nRef>0){
						assert(nRef+counter==responseDataTable.size()), locateTest()+"Error: Number of ServiceMetadataReference in the response is "+(responseDataTable.size()-counter)+" instead of "+nRef+".";
					}
					while(counter < responseDataTable.size()){
						if(responseDataTable[counter][1]=="ServiceMetadataReference"){
							extractFromURL(responseDataTable[counter][0])
							assert((tempoContainer[0]==responseDataTable[0][0])&&(tempoContainer[1]==responseDataTable[1][0])), locateTest()+"Error: in a ServiceMetadataReference in the response, participant is ("+tempoContainer[0]+","+tempoContainer[1]+") instead of ("+responseDataTable[0][0]+","+responseDataTable[1][0]+").";
						}
						counter++;
					}
					if(nRef>0){
						assert(nRef), locateTest()+"Error: in a ServiceMetadataReference in the response, participant is ("+tempoContainer[0]+","+tempoContainer[1]+") instead of ("+responseDataTable[0][0]+","+responseDataTable[1][0]+").";
					}
				}
				break;
			case "servicemetadata":
				counter = 0;
				if(expectedResult.toLowerCase()=="success"){
					while(counter<4){
						assert(responseDataTable[counter][0]==requestDataTable[counter][0]), locateTest()+"Error: in request, "+requestDataTable[counter][1]+"=\""+requestDataTable[counter][0]+"\""+" wheras in response, "+responseDataTable[counter][1]+"=\""+responseDataTable[counter][0]+"\".";
						counter++;
					}
					extensionResponse=extractExtValues(removeNamespaces(tempoString));
					tempoString = extractPartFromXML(removeNamespaces(tempoString),"servicemetadata");
					reqString = removeNamespaces(extractPartFromXML(extractTextFromReq(testStepName),"servicemetadata"));
					assert(compareMetadata(reqString,tempoString).toLowerCase()=="true"), locateTest()+"Error: in ServiceMetadata returned ------"+tempoString+"------ is not equal to the metadata pushed ------"+reqString+"------.";
					extensionRequest=extractNodeValue("Extension", extractTextFromReq(testStepName),"ServiceInformation");
					extensionRequest=extractExtValues(removeNamespaces(extractTextFromReq(testStepName)));
					if(compareXMLs(extensionRequest,extensionResponse)==false){
						log.error "Extension in request: "+extensionRequest;
						log.error "Extension in response: "+extensionResponse;
						assert(0), locateTest()+"Error: Extension returned is different from Extension pushed. For details, please refer to logs in red.";
					}
				}
				break;
			case "redirection":
				counter = 0;
				requestDataTable[0][0]=redirectURL;
				requestDataTable[1][0]=redirectCer;
				assert(requestDataTable[0][0]==responseDataTable[0][0]), locateTest()+"Error: in ServiceMetadata returned redirect URL is ------"+responseDataTable[0][0]+"------ instead of ------"+requestDataTable[0][0]+"------.";
				assert(requestDataTable[0][0]==responseDataTable[0][0]), locateTest()+"Error: in ServiceMetadata returned certificate is ------"+responseDataTable[1][0]+"------ instead of ------"+requestDataTable[1][0]+"------.";
				break;
			case "signature":
				debugLog("Start SMP signature verification ...", log)
				debugLog("Start extracting signature algorithm ...", log)
				sigAlgo = extractNodeValue("SignatureMethod", tempoString,null, "Algorithm");
				debugLog("Signature algorithm=\"$sigAlgo\"", log)
				assert(sigAlgo!= "0"), locateTest()+"Error: Signature Algorithm couldn't be extracted from the response."
				assert(SIGNATURE_ALGORITHM==sigAlgo), locateTest()+"Error: Signature Algorithm is "+sigAlgo+" instead of "+SIGNATURE_ALGORITHM+".";
				// Verify the SMP signature validity
				debugLog("Start SMP signature validation ...", log)
				def Boolean validResult = validateSignature(returnDOMDocument(tempoString));
				assert (validResult == true),locateTest()+"Error: Signature of the SMP is not valid.";
				debugLog("SMP signature successfully validated.", log)
				validResult =false;

				// TODO: Enable the extension signature validation.
				validResult = validateSignatureExtension(returnDOMDocument(tempoString));
				assert (validResult == true),locateTest()+"Error: Signature in the extension is not valid.";
				break;

			default:
				if(testType.toLowerCase()=="contenttype"){
					// Do nothing
					break;
				}
				assert(0), locateTest()+"Error: -verifyResults-Unknown operation: "+testType+"."+" Possible operations: serviceGroup, serviceMetadata, Redirection, Signature, contentType.";
				break;
		}
	}

//----------------------- Extract text from request -----------------------
// To be deprecated ?
	def String extractTextFromReq(String testStepName){
		def fullRequest = context.testCase.getTestStepByName(testStepName);
		assert (fullRequest != null), locateTest()+"Error in function \"extractTextFromReq\": can't find test step name: \""+testStepName+"\"";
		def request = fullRequest.getProperty( "request" );
		def result = request.value.toString();
		result = result.replace("%23","#");
		result = result.replace("%3A",":");
		return result;
	}
	
//---------------- Extract subresource metadata from url ------------------
// To be deprecated ?	
	def extractFromURL(String url){
		def Table1 = [];
		def parts = [];
		def mesure = 0;
		def extraParts = null;
		debugLog("entering extractFromURL", log)

		tempoContainer=["0","0","0","0"];

		Table1 = url.split('/services/');
		parts=Table1[0].split('/');
		mesure=parts.size();
		assert (mesure > 0),locateTest()+"Error: Could not extract the Participant Identifier from the url. Non usual url format.";
		parts[mesure-1]=parts[mesure-1].replace("%3A",":");
		parts[mesure-1]=parts[mesure-1].replace("%23","#");

		if(Table1.size() > 1){
			extraParts=Table1[1]
		}
		Table1 = [];
		Table1 = parts[mesure-1].split('::',2);
		assert (Table1.size()== 2),locateTest()+"Error: Could not extract the Participant Identifier from the url. Non usual url format, :: separator not found";
		tempoContainer[0] = Table1[0];
		tempoContainer[1] = Table1[1];
		debugLog("Filling tempoContainer table", log)
		// TODO FIX this backward compatibility issue
		if (messageExchange.getProperties()) {
			debugLog("Extracting ParticipantIdentifier from property. Table1: Table1", log)
			tempoContainer[0] = messageExchange.getProperty('ParticipantIdentifierScheme')
			tempoContainer[1] = messageExchange.getProperty('ParticipantIdentifier')
		}

		if(extraParts!=null){
			debugLog("Filling tempoContainer table fields 2 and 3. extraParts: $extraParts", log)
			extraParts = extraParts.replace("%3A",":");
			extraParts = extraParts.replace("%23","#");
			Table1 = [];
			Table1=extraParts.split('::',2);
			tempoContainer[2] = Table1[0].replace("%2F","/");
			tempoContainer[3] = Table1[1].replace("%2F","/");
			// TODO FIX this backward compatibility issue
			if (messageExchange.getProperties()) {
				debugLog("Extracting DocTypeIdentifier from property", log)
				tempoContainer[2] = messageExchange.getProperty('DocTypeIdentifierScheme')
				tempoContainer[3] = messageExchange.getProperty('DocTypeIdentifier')
			}
		}
		debugLog("Leaving extractFromURL", log)
	}

//---------------------------- Compare 2 Metadata -------------------------	
	def String compareMetadata(String metaData1, String metaData2){
		def i = 0;
		def String outcome = "false";
		def table1 = [];
		def table2 = [];
		table1=parseMetadata("<rootnode>"+metaData1+"</rootnode>");
		table2=parseMetadata("<rootnode>"+metaData2+"</rootnode>");
		outcome = compareTables(table1,table2);
		return (outcome);
	}

//---------------------------- Parse Metadata -------------------------
	def parseMetadata(String metadata){
		def i = 0;
		def result = [];
		def switchProcess = 0;
		def switchEndPoint = 0;
		def String oldProcessId ="0";
		def String oldProcessScheme ="0";
		tablebuffer=["0","0","0","0","0","0","0","0","0","0","0","0","0"];
		def rootMT = new XmlSlurper().parseText(metadata);
		def allNodes = rootMT.depthFirst().each{
			if(it.name()== "ProcessIdentifier"){
				if(switchProcess==0){
					oldProcessScheme=it.@scheme.text();
					oldProcessId=it.text();
					switchProcess=1;
				}else{
					oldProcessScheme=tablebuffer[0];
					oldProcessId=tablebuffer[1];
					switchProcess=0;
				}
				tablebuffer[0]=it.@scheme.text();
				tablebuffer[1]=it.text();
			}
			if(it.name()== "Endpoint"){
				if(switchEndPoint==0){
					switchEndPoint=1;
				}else{
					//charge endpoint
					result[i]=returnHash(tablebuffer.join(","));
					i=i+1;
				}
				tablebuffer[2]=it.@transportProfile.text();
			}
			if(it.name()== "EndpointURI"){
				tablebuffer[3]=it.text().trim();
			}
			if(it.name()== "RequireBusinessLevelSignature"){
				if(it.text()=~ /[f|F][a|A][l|L][S|s]/){
					tablebuffer[4]="0";
				}else{
					if(it.text()=~ /[T|t][R|r][U|u]/){
						tablebuffer[4]="1";
					}
					else{
						tablebuffer[4] = it.text()
					}
				}
			}
			if(it.name()== "ServiceActivationDate"){
				//tablebuffer[5]=it.text();
				tablebuffer[5]=Date.parse("yyyy-MM-dd",it.text());
			}
			if(it.name()== "ServiceExpirationDate"){
				//tablebuffer[6]=it.text();
				tablebuffer[6]=Date.parse("yyyy-MM-dd",it.text());
			}
			if(it.name()== "Certificate"){
				tablebuffer[7]=it.text();
			}
			if(it.name()== "ServiceDescription"){
				tablebuffer[8]=it.text();
			}
			if(it.name()== "TechnicalContactUrl"){
				tablebuffer[9]=it.text();
			}
			if(it.name()== "minimumAuthenticationLevel"){
				tablebuffer[10]=it.text();
			}
			if(it.name()== "TechnicalInformationUrl"){
				tablebuffer[11]=it.text();
			}
			if(it.name()== "extension"){
				tablebuffer[12]=it.text();
			}
		}
		result[i]=returnHash(tablebuffer.join(","));
		return(result);
	}	

	
//=========================================================================
//========== Handle DomiSMP configuration properties functions ============
//=========================================================================
//------------- Get the Smp configuration props metadata ------------------
	def static getSmpConfigPropertyMeta(log, context, propName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getSmpConfigPropertyMeta\".", log)
		debugLog("  getSmpConfigPropertyMeta  [][]  Property to get: \"$propName\".", log)
		def jsonSlurper = new JsonSlurper()
		def commandString=null
		def commandResult=null 
		def propMap=null 
		def propMetadata=null
		def propMeta=null
		def urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		def urlExt="/ui/internal/rest/property?"

		try{
			commandString=["curl", urlToSMP + urlExt + "page=0&pageSize=50&property=$propName",
                                 "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                 "-H",  "Content-Type: text/xml",
                                 "-H","X-XSRF-TOKEN: " + returnXsfrToken(log, context, authenticationUser, authenticationPwd),
                                 "-v"]
			commandResult=runCommandInShell(log, commandString)
			assert(commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/),"Error:getSmpConfigPropertyMeta: Error while fetching value of property \"$propName\"."
            propMetadata=commandResult[0]
			debugLog("  getSmpConfigPropertyMeta  [][]  Property get result: $propMetadata", log)
            propMap=jsonSlurper.parseText(propMetadata)
            assert(propMap != null),"Error:getSmpConfigPropertyMeta: Error while parsing the returned property value: null result found."
			propMap.serviceEntities.each{ prop ->
				if(prop.property.toLowerCase().equals(propName.toLowerCase())){
					propMeta=prop
				}
			}
			debugLog("  getSmpConfigPropertyMeta  [][]  Property \"$propName\" metadata = \"$propMeta\".", log)
        }finally{
            XSFRTOKEN=null
        }
		return propMeta
	}

//-------------- Get the Smp configuration property value -----------------
	def static getSmpConfigPropertyValue(log, context, propName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getSmpConfigPropertyValue\".", log)
		debugLog("  getSmpConfigPropertyValue  [][]  Property to get: \"$propName\".", log)
		
		return getSmpConfigPropertyMeta(log, context, propName, authenticationUser, authenticationPwd).value
	}

//-------------- Set the Smp configuration property value -----------------
	def static setSmpConfigProperty(log, context, propName, newValue, waitForApp=false, waitDuration="20", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"setSmpConfigProperty\".", log)
		debugLog("  setSmpConfigProperty  [][]  Setting property \"$propName\" to \"$newValue\".", log)
		def updatedProp=[:]
		def updatedPropJson=null
		def updatedPropJsonList=[]
		def commandString=null
		def commandResult=null 
		def urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		def urlExt="/ui/internal/rest/property"
		
		// Get the propety Metadata
		def propMetadata=getSmpConfigPropertyMeta(log, context, propName, authenticationUser, authenticationPwd)
		
		propMetadata.each{index, val ->
			if(!index.equals("desc")){
				updatedProp[index]=val
			}			
		}
		updatedProp["status"]=1
		updatedProp["deleted"]=false
		updatedProp["value"]=newValue
		
		updatedPropJsonList<<updatedProp
		updatedPropJson=JsonOutput.toJson(updatedPropJsonList).toString()

		try{
			commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + returnXsfrToken(log, context, authenticationUser, authenticationPwd),
                                         "-X", "PUT",
                                         "--data", formatJsonForCurl(log,updatedPropJson),
                                         "-v"]	
										 
			commandResult=runCommandInShell(log, commandString)
        }finally{
            XSFRTOKEN=null
        }
		assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:setSmpConfigProperty: Error while trying to connect to the SMP. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		if(waitForApp){
			waitFor(log,waitDuration, "sec")
		}
		debugLog("  setSmpConfigProperty  [][]  Property \"$propName\" update done successfully.", log)		
	}

	
//=========================================================================
//=================== Domain members functions ============================
//=========================================================================
//------------------------- add domain member -----------------------------
	def static addDomainMember(log, context, domainCode, username, roleType="VIEWER", memberOf="DOMAIN", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"addDomainMember\".", log)
		debugLog("  addDomainMember  [][]  Adding user \"$username\" to domain \"$domainCode\" with role type \"$roleType\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 

		def exists=domainMemberExists(log, context, domainCode, username, authenticationUser, authenticationPwd)
		
		if(!exists){
			json=ifWindowsEscapeJsonString('{\"memberOf\":\"' + "${memberOf}" + '\", \"roleType\":\"' + "${roleType}" + '\", \"username\":\"' + "${username}" + '\"	}')
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/member/put"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:addDomainMember: Error while trying to add user \"$username\" to domain \"$domainCode\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  addDomainMember  [][]  User \"$username\" is now member of domain \"$domainCode\".", log)	
		}else{
			debugLog("  addDomainMember  [][]  Domain member \"$username\" already exists (domain \"$domainCode\"). Skip the creation ...", log)
		}
	}

//------------------ get domain Members metadata list ---------------------
	def static getAllDomainMembersMetadata(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getAllDomainMembersMetadata\".", log)
		debugLog("  getAllDomainMembersMetadata  [][]  Getting the members metadata list for domain \"$domainCode\" ...", log)
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 

		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID		
		domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/edit/rest/$userIdent/domain/$domainId/member?page=0&pageSize=20"
		
		commandString=["curl", urlToSMP+urlExt,
                                    "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                    "-H", "Content-Type: application/json",
                                    "-H","X-XSRF-TOKEN: " + xsrf_token,
                                    "-X", "GET",
                                    "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)

		assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:getAllDomainMembersMetadata: Error while trying to get the list of members metadata for domain \"$domainCode\" . CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  getAllDomainMembersMetadata  [][]  Members metadata list for domain \"$domainCode\" retrieved successfully.", log)	
		return commandResult[0]
	}

//------------------ get single domain Member Metadata --------------------
	def static getDomainMemberMetadata(log, context, domainCode, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainMemberMetadata\".", log)
		debugLog("  getDomainMemberMetadata  [][]  Get metadata for member \"$username\" (domain \"$domainCode\") ...", log)
		def jsonSlurper = new JsonSlurper()
		def memberMeta=null
		
		def dataMap=jsonSlurper.parseText(getAllDomainMembersMetadata(log, context, domainCode, authenticationUser, authenticationPwd))
		
		dataMap.serviceEntities.each{ memb ->
			if(memb.username.toLowerCase().equals(username.toLowerCase())){
				debugLog("  getDomainMemberMetadata  [][]  Domain member \"$username\" found.", log)
				memberMeta=memb
			}
		}
		
		return memberMeta
	}

//------------------ get single domain Member Id --------------------
	def static getDomainMemberId(log, context, domainCode, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainMemberId\".", log)
		debugLog("  getDomainMemberId  [][]  Get memberId for domain member \"$username\" (domain $domainCode) ...", log)
		
		def dataMap=getDomainMemberMetadata(log, context, domainCode, username, authenticationUser, authenticationPwd)
		
		assert(dataMap!=null),"Error:getDomainMemberId: Error while trying to retrieve memberId for domain member \"$username\" (domain $domainCode): metadata returned is null"	
		return dataMap.memberId
	}

//-------------------- get domain members List ----------------------
	def static getDomainMembersList(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainMembersList\".", log)
		debugLog("  getDomainMembersList  [][]  Get all domain members list ...", log)
		def membersList=[]
		def jsonSlurper = new JsonSlurper()
		
		def dataMap=jsonSlurper.parseText(getAllDomainMembersMetadata(log, context, domainCode, authenticationUser, authenticationPwd))
		dataMap.serviceEntities.each{ memb ->
			membersList<<memb.username
		}		
		return membersList
	}

//----------------- check if domain member exists -------------------
	def static boolean domainMemberExists(log, context, domainCode, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"domainMemberExists\".", log)
		debugLog("  domainMemberExists  [][]  Check if domain member \"$username\" (domain $domainCode) exists ...", log)
		
		def dataMap=getDomainMembersList(log, context, domainCode, authenticationUser, authenticationPwd)
		if(dataMap*.toLowerCase().contains(username.toLowerCase())){
			debugLog("  domainMemberExists  [][]  Domain member \"$username\" exists (domain $domainCode).", log)
			return true
		}
		debugLog("  domainMemberExists  [][]  Domain member \"$username\" does not exist (domain $domainCode).", log)
		return false
	}

//---------------------- Delete domain member -----------------------
	def static deleteDomainMember(log, context, domainCode, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"deleteDomainMember\".", log)
		debugLog("  deleteDomainMember  [][]  Deleting domain member \"$username\" (domain \"$domainCode\") ...", log)
		def userIdent=null
		def domainId=null
		def memberId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=domainMemberExists(log, context, domainCode, username, authenticationUser, authenticationPwd)

		if(exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			memberId=getDomainMemberId(log, context, domainCode, username, authenticationUser, authenticationPwd)		
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/member/$memberId/delete"
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "DELETE",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:deleteDomainMember: Error while trying to delete domain member \"$username\" (domain $domainCode). CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]

			debugLog("  deleteDomainMember  [][]  Domain member \"$username\" (Domain \"$domainCode\") successfully deleted.", log)	
		}else{
			debugLog("  deleteDomainMember  [][]  Domain member \"$username\" (Domain \"$domainCode\") does not exist: skip the deletion ...", log)
		}
	}
	
	
//===================================================================
//========================= Groups functions ========================
//===================================================================
//------------------------ add domain group -------------------------
	def static addGroup(log, context, domainCode, groupName, visibility="PUBLIC", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"addGroup\".", log)
		debugLog("  addGroup  [][]  Adding group \"$groupName\" to domain \"$domainCode\" with visibility \"$visibility\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=groupExists(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
		
		if(!exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			json=ifWindowsEscapeJsonString('{\"visibility\":\"' + "${visibility}" + '\", \"groupName\":\"' + "${groupName}" + '\"}')
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group/create"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:addGroup: Error while trying to link domain \"$domainCode\" to group \"$groupName\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  addGroup  [][]  Domain \"$domainCode\" is now linked to group \"$groupName\".", log)	
		}else{
			debugLog("  addGroup  [][]  Group \"$groupName\" already exist (domain \"$domainCode\"). Skip the creation...", log)
		}
	}

//-------------------------- Delete Group ---------------------------
	def static deleteGroup(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"deleteGroup\".", log)
		debugLog("  deleteGroup  [][]  Deleting group \"$groupName\" (domain \"$domainCode\") ...", log)
		def userIdent=null
		def domainId=null
		def groupId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=groupExists(log, context, domainCode, groupName, authenticationUser, authenticationPwd)

		if(exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			groupId=getGroupId(log, context, domainCode, groupName, authenticationUser, authenticationPwd)		
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group/$groupId/delete"
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "DELETE",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:deleteGroup: Error while trying to delete group \"groupName\" (domain $domainCode). CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]

			debugLog("  deleteGroup  [][]  Group \"$groupName\" (Domain \"$domainCode\") successfully deleted.", log)	
		}else{
			debugLog("  deleteGroup  [][]  Group \"groupName\" (Domain \"$domainCode\") does not exist: skip the deletion ...", log)
		}
	}

//---------------------- get domain groups list ---------------------
	def static getAllGroupsMetadata(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getAllGroupsMetadata\".", log)
		debugLog("  getAllGroupsMetadata  [][]  Getting the groups metadata list for domain \"$domainCode\" ...", log)
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=false

		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID		
		domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group"
		
		commandString=["curl", urlToSMP+urlExt,
                                    "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                    "-H", "Content-Type: application/json",
                                    "-H","X-XSRF-TOKEN: " + xsrf_token,
                                    "-X", "GET",
                                    "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)

		assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:getAllGroupsMetadata: Error while trying to get the list of groups metadata for domain \"$domainCode\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  getAllGroupsMetadata  [][]  Groups metadata list for domain \"$domainCode\" retrieved successfully.", log)	
		return commandResult[0]
	}

//--------------------- get single Group Metadata -------------------
	def static getGroupMetadata(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupMetadata\".", log)
		debugLog("  getGroupMetadata  [][]  Get metadata for group \"$groupName\" (domain \"$domainCode\") ...", log)
		def jsonSlurper = new JsonSlurper()
		def groupMeta=null
		
		def dataMap=jsonSlurper.parseText(getAllGroupsMetadata(log, context, domainCode, authenticationUser, authenticationPwd))
		
		dataMap.each{ grp ->
			if(grp.groupName.toLowerCase().equals(groupName.toLowerCase())){
				debugLog("  getGroupMetadata  [][]  Group \"$groupName\" found.", log)
				groupMeta=grp
			}
		}

		return groupMeta
	}

//------------------------ get single Group Id ----------------------
	def static getGroupId(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupId\".", log)
		debugLog("  getGroupId  [][]  Get groupId for groupName \"$groupName\" (domain \"$domainCode\") ...", log)
		
		def dataMap=getGroupMetadata(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
		
		assert(dataMap!=null),"Error:getGroupId: Error while trying to retrieve groupID for group \"$groupName\" (domain $domainCode): metadata returned is null"	

		return dataMap.groupId
	}

//------------------------ get group names List ---------------------
	def static getGroupNamesList(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupNamesList\".", log)
		debugLog("  getGroupNamesList  [][]  Get all group names list ...", log)
		def groupList=[]
		def jsonSlurper = new JsonSlurper()
		
		def dataMap=jsonSlurper.parseText(getAllGroupsMetadata(log, context, domainCode, authenticationUser, authenticationPwd))
		dataMap.each{ grp ->
			groupList<<grp.groupName
		}		
		return groupList
	}

//------------------------ check if group exists --------------------
	def static boolean groupExists(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"groupExists\".", log)
		debugLog("  groupExists  [][]  Check if group \"$groupName\" (domain $domainCode) exists ...", log)
		
		def dataMap=getGroupNamesList(log, context, domainCode, authenticationUser, authenticationPwd)
		if(dataMap*.toLowerCase().contains(groupName.toLowerCase())){
			debugLog("  groupExists  [][]  Group \"$groupName\" exists (domain $domainCode).", log)
			return true
		}
		debugLog("  groupExists  [][]  Group \"$groupName\" does not exist (domain $domainCode).", log)
		return false
	}
	
	
//=========================================================================
//==================== Group members functions ============================
//=========================================================================
//------------------------- add Group member -----------------------------
	def static addGroupMember(log, context, domainCode, groupName, username, roleType="VIEWER", memberOf="GROUP", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"addGroupMember\".", log)
		debugLog("  addGroupMember  [][]  Adding user \"$username\" to group (domain \"$domainCode\") with role type \"$roleType\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def groupId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 

		def exists=groupMemberExists(log, context, domainCode, groupName, username, authenticationUser, authenticationPwd)
		
		if(!exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			json=ifWindowsEscapeJsonString('{\"memberOf\":\"' + "${memberOf}" + '\", \"roleType\":\"' + "${roleType}" + '\", \"username\":\"' + "${username}" + '\"	}')
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			groupId=getGroupId(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group/$groupId/member/put"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:addGroupMember: Error while trying to add user \"$username\" to group \"$groupName\" (domain \"$domainCode\"). CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  addGroupMember  [][]  User \"$username\" is now member of group \"$groupName\" (domain \"$domainCode\").", log)	
		}else{
			debugLog("  addGroupMember  [][]  Group \"$groupName\" member \"$username\" already exists (domain \"$domainCode\"). Skip the creation ...", log)
		}
	}

//------------------- get group Members metadata list ---------------------
	def static getAllGroupMembersMetadata(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getAllGroupMembersMetadata\".", log)
		debugLog("  getAllGroupMembersMetadata  [][]  Getting the members metadata list for group \"$groupName\" (domain \"$domainCode\") ...", log)
		def userIdent=null
		def domainId=null
		def groupId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID		
		domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
		groupId=getGroupId(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group/$groupId/member?page=0&pageSize=20"
		
		commandString=["curl", urlToSMP+urlExt,
                                    "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                    "-H", "Content-Type: application/json",
                                    "-H","X-XSRF-TOKEN: " + xsrf_token,
                                    "-X", "GET",
                                    "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)

		assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:getAllGroupMembersMetadata: Error while trying to get the list of members metadata for group \"$groupName\" (domain \"$domainCode\"). CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  getAllGroupMembersMetadata  [][]  Members metadata list for group \"$groupName\" (domain \"$domainCode\") retrieved successfully.", log)	
		return commandResult[0]
	}

//------------------- get single group Member Metadata --------------------
	def static getGroupMemberMetadata(log, context, domainCode, groupName, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupMemberMetadata\".", log)
		debugLog("  getGroupMemberMetadata  [][]  Get metadata for member \"$username\" of group \"$groupName\" (domain \"$domainCode\") ...", log)
		def jsonSlurper = new JsonSlurper()
		def memberMeta=null
		
		def dataMap=jsonSlurper.parseText(getAllGroupMembersMetadata(log, context, domainCode, groupName, authenticationUser, authenticationPwd))
		
		dataMap.serviceEntities.each{ memb ->
			if(memb.username.toLowerCase().equals(username.toLowerCase())){
				debugLog("  getGroupMemberMetadata  [][]  Group member \"$username\" found.", log)
				memberMeta=memb
			}
		}
		
		return memberMeta
	}

//------------------- get single group Member Id --------------------
	def static getGroupMemberId(log, context, domainCode, groupName, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupMemberId\".", log)
		debugLog("  getGroupMemberId  [][]  Get memberId of member \"$username\" for group \"$groupName\" (domain $domainCode) ...", log)
		
		def dataMap=getGroupMemberMetadata(log, context, domainCode, groupName, username, authenticationUser, authenticationPwd)
		
		assert(dataMap!=null),"Error:getGroupMemberId: Error while trying to retrieve memberId of group member \"$username\" for group \"$groupName\" (domain \"$domainCode\"): metadata returned is null"	
		return dataMap.memberId
	}

//--------------------- get group members List ----------------------
	def static getGroupMembersList(log, context, domainCode, groupName, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getGroupMembersList\".", log)
		debugLog("  getGroupMembersList  [][]  Get all group members list ...", log)
		def membersList=[]
		def jsonSlurper = new JsonSlurper()
		
		def dataMap=jsonSlurper.parseText(getAllGroupMembersMetadata(log, context, domainCode, groupName, authenticationUser, authenticationPwd))
		dataMap.serviceEntities.each{ memb ->
			membersList<<memb.username
		}		
		return membersList
	}

//------------------ check if group member exists -------------------
	def static boolean groupMemberExists(log, context, domainCode, groupName, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"groupMemberExists\".", log)
		debugLog("  groupMemberExists  [][]  Check if group member \"$username\" for group \"$groupName\" (domain $domainCode) exists ...", log)
		
		def dataMap=getGroupMembersList(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
		if(dataMap*.toLowerCase().contains(username.toLowerCase())){
			debugLog("  groupMemberExists  [][]  Group member \"$username\" exists (group \"$groupName\", domain $domainCode).", log)
			return true
		}
		debugLog("  groupMemberExists  [][]  Group member \"$username\" does not exist (group \"$groupName\", domain $domainCode).", log)
		return false
	}

//----------------------- Delete group member -----------------------
	def static deleteGroupMember(log, context, domainCode, groupName, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"deleteGroupMember\".", log)
		debugLog("  deleteGroupMember  [][]  Deleting domain member \"$username\" (domain \"$domainCode\") ...", log)
		def userIdent=null
		def domainId=null
		def groupId=null
		def memberId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=groupMemberExists(log, context, domainCode, groupName, username, authenticationUser, authenticationPwd)

		if(exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			groupId=getGroupId(log, context, domainCode, groupName, authenticationUser, authenticationPwd)
			memberId=getGroupMemberId(log, context, domainCode, username, authenticationUser, authenticationPwd)		
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/edit/rest/$userIdent/domain/$domainId/group/$groupId/member/$memberId/delete"
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "DELETE",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:deleteGroupMember: Error while trying to delete group member \"$username\" (group \"$groupName\", domain $domainCode). CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]

			debugLog("  deleteGroupMember  [][]  Group member \"$username\" (group \"$groupName\", domain $domainCode) successfully deleted.", log)	
		}else{
			debugLog("  deleteGroupMember  [][]  Group member \"$username\" (group \"$groupName\", domain $domainCode) does not exist: skip the deletion ...", log)
		}
	}

	
//===================================================================
//================ Domain configuration functions ===================
//===================================================================
//----------- Set the domain supported resources types --------------
	def static setDomainResourceTypes(log, context, domainCode, resourceTypes=["edelivery-oasis-smp-1.0-servicegroup","edelivery-oasis-smp-2.0-servicegroup"], authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"setDomainResourceTypes\".", log)
		debugLog("  setDomainResourceTypes  [][]  Setting  resource types \""+resourceTypes+"\" to domain \"$domainCode\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=false
		
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		json=formatJsonForCurl(log,JsonOutput.toJson(resourceTypes).toString())
		domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/internal/rest/$userIdent/domain/$domainId/update-resource-types"
		
		try{
			commandString=["curl", urlToSMP+urlExt,
                                        "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                        "-H", "Content-Type: application/json",
                                        "-H","X-XSRF-TOKEN: " + xsrf_token,
                                        "-X", "POST",
                                        "--data", json,
                                        "-v"]	
										 
			commandResult=runCommandInShell(log, commandString)
		}finally{
			XSFRTOKEN=null
		}
		assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:setDomainResourceTypes: Error while trying to set resource types for domain \"$domainCode\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  setDomainResourceTypes  [][]  Resource types set to \""+resourceTypes+"\" for domain \"$domainCode\".", log)	
	}

//------------------ Set domain default resource type ---------------------
	def static setDomainDefResourceType(log, context, domainCode, defResourceType, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"setDomainDefResourceType\".", log)
		debugLog("  setDomainDefResourceType  [][]  Setting default resource type \"$defResourceType\" for domain \"$domainCode\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=false
		
		if(!exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/$userIdent/domain/$domainId/update"
			json=ifWindowsEscapeJsonString('{\"status\":1, \"index\":0, \"domainId\":\"' + "${domainId}" + '\", \"domainCode\":\"' + "${domainCode}" + '\", \"defaultResourceTypeIdentifier\":\"' + "${defResourceType}" + '\"}')
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "POST",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:setDomainDefResourceType: Error while trying to set the default resource type identifier for domain \"$domainCode\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  setDomainDefResourceType  [][]  Default resource type identifier updated to \"$defResourceType\" for domain \"$domainCode\".", log)	
		}else{
			debugLog("  setDomainDefResourceType  [][]  Unknown error while trying to update Default Resource Type Identifier for domain \"$domainCode\" ...", log)
		}
	}

//---------------------- Add SML integration data -------------------------
	def static addSMLintegrationData(log, context, domainCode, smlSubdomain, smlSmpId, smlClientKeyAlias, smlClientCertAuth, smlRegistered, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"addSMLintegrationData\".", log)
		debugLog("  addSMLintegrationData  [][]  Adding SML integration data for domain \"$domainCode\" ...", log)
		def json=null
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=false
		
		if(!exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/$userIdent/domain/$domainId/update-sml-integration-data"
			json=ifWindowsEscapeJsonString('{\"status\":0, \"index\":0,\"actionMessage\":null,\"domainId\":\"' + "${domainId}" + '\", \"domainCode\":\"' + "${domainCode}" + '\", \"smlSubdomain\":\"' + "${smlSubdomain}" + '\",\"smlSmpId\":\"' + "${smlSmpId}" + '\", \"smlClientKeyAlias\":\"' + "${smlClientKeyAlias}" + '\", \"smlClientCertAuth\":\"' + "${smlClientCertAuth}" + '\",\"smlRegistered\":\"' + "${smlRegistered}" + '\"}')
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "POST",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:addSMLintegrationData: Error while trying to add SML integration data for domain \"$domainCode\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  addSMLintegrationData  [][]  SML integration data added for domain \"$domainCode\".", log)	
		}else{
			debugLog("  addSMLintegrationData  [][]  Unknown error while trying to add SML integration data for domain \"$domainCode\" ...", log)
		}
	}

//---------------------- Register domain in SML ---------------------------
	def static registerDomainSML(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"registerDomainSML\".", log)
		debugLog("  registerDomainSML  [][]  Registering domain \"$domainCode\" in SML ...", log)
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=false
		
		if(!exists){
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="ui/internal/rest/domain/$domainId/sml-register/$domainCode"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:registerDomainSML: Error while trying to register domain \"$domainCode\" in the SML. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  registerDomainSML  [][]  Domain \"$domainCode\" is now registered in the SML.", log)	
		}else{
			debugLog("  registerDomainSML  [][]  Unknown error while trying to register domain \"$domainCode\" in the SML ...", log)
		}
	}
	
	
//=========================================================================
//========================== Domains functions ============================
//=========================================================================
//--------------------------- Create a domain -----------------------------
	def static createDomain(log, context, domainCode, signatureKeyAlias, visibility="PUBLIC", smlSmpId="", smlPartIdRegexp="", smlClientKeyAlias="", smlSubdomain="", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"createDomain\".", log)
		debugLog("  createDomain  [][]  Creating domain \"$domainCode\" ...", log)
		def json=null
		def userIdent=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=domainExists(log, context, domainCode, authenticationUser, authenticationPwd)
		
		if(!exists){
			json=ifWindowsEscapeJsonString('{\"index\":null, \"visibility\":\"' + "${visibility}" + '\", \"domainCode\":\"' + "${domainCode}" + '\",\"smlSubdomain\":\"' + "${smlSubdomain}" + '\",\"smlSmpId\":\"' + "${smlSmpId}" + '\",\"smlParticipantIdentifierRegExp\":\"' + "${smlPartIdRegexp}" + '\",\"smlClientKeyAlias\":\"' + "${smlClientKeyAlias}" + '\",\"signatureKeyAlias\":\"' + "${signatureKeyAlias}" + '\",\"status\":2,\"smlRegistered\":false,\"smlClientCertAuth\":false	}')
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/$userIdent/domain/create"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:createDomain: Error while trying to create domain $domainCode. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  createDomain  [][]  Domain \"$domainCode\" created successfully.", log)	
		}else{
			debugLog("  createDomain  [][]  Domain \"$domainCode\" already exists: skip the creation ...", log)
		}
	}

//--------------------------- Delete a domain -----------------------------
	def static deleteDomain(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"deleteDomain\".", log)
		debugLog("  deleteDomain  [][]  Deleting domain \"$domainCode\" ...", log)
		def userIdent=null
		def domainId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=domainExists(log, context, domainCode, authenticationUser, authenticationPwd)

		if(exists){
			domainId=getDomainId(log, context, domainCode, authenticationUser, authenticationPwd)
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID		
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/$userIdent/domain/$domainId/delete"
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "DELETE",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:deleteDomain: Error while trying to delete domain $domainCode. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]

			debugLog("  deleteDomain  [][]  Domain \"$domainCode\" successfully deleted.", log)	
		}else{
			debugLog("  deleteDomain  [][]  Domain \"$domainCode\" does not exist: skip the deletion ...", log)
		}
	}

//----------------- retrieve metadata for all domains ---------------------
	def static getAllDomainsMetadata(log, context, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getAllDomainsMetadata\".", log)
		debugLog("  getAllDomainsMetadata  [][]  Get json list of all domains metadata ...", log)
		def userIdent=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/internal/rest/$userIdent/domain"
		
		commandString=["curl", urlToSMP+urlExt,
                                    "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                    "-H","X-XSRF-TOKEN: " + xsrf_token,
                                    "-X", "GET",
                                    "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)
			
        assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:getAllDomainsMetadata: Error while trying to retrieve all domains metadata. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  getAllDomainsMetadata  [][]  Domains metadata retrieved successfully.", log)	
		return commandResult[0]
	}

//-------------------- get single Domain Metadata -------------------------
	def static getDomainMetadata(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainMetadata\".", log)
		debugLog("  getDomainMetadata  [][]  Get metadata for domain $domainCode ...", log)
		def jsonSlurper = new JsonSlurper()
		def domMeta=null
		
		def dataMap=jsonSlurper.parseText(getAllDomainsMetadata(log, context, authenticationUser, authenticationPwd))
		
		dataMap.each{ dom ->
			if(dom.domainCode.toLowerCase().equals(domainCode.toLowerCase())){
				debugLog("  getDomainMetadata  [][]  Domain \"$domainCode\" found.", log)
				domMeta=dom
			}
		}
		
		return domMeta
	}

//----------------------- get single Domain Id ----------------------------
	def static getDomainId(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainId\".", log)
		debugLog("  getDomainId  [][]  Get domainId for domain \"$domainCode\" ...", log)

		def dataMap=getDomainMetadata(log, context, domainCode, authenticationUser, authenticationPwd)
		
		assert(dataMap!=null),"Error:getDomainId: Error while trying to retrieve domainID for domain $domainCode: metadata returned is null"	
		return dataMap.domainId
	}

//----------------------- get Domains codes List --------------------------
	def static getDomainCodesList(log, context, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getDomainCodesList\".", log)
		debugLog("  getDomainCodesList  [][]  Get all domainCodes list ...", log)
		def domList=[]
		def jsonSlurper = new JsonSlurper()
		
		def dataMap=jsonSlurper.parseText(getAllDomainsMetadata(log, context, authenticationUser, authenticationPwd))
		dataMap.each{ dom ->
			domList<<dom.domainCode
		}		
		return domList
	}

//----------------------- check if domain exists --------------------------
	def static boolean domainExists(log, context, domainCode, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"domainExists\".", log)
		debugLog("  domainExists  [][]  Check if domain \"$domainCode\" exists ...", log)
		
		def dataMap=getDomainCodesList(log, context, authenticationUser, authenticationPwd)
		if(dataMap*.toLowerCase().contains(domainCode.toLowerCase())){
			debugLog("  domainExists  [][]  Domain \"$domainCode\" exists.", log)
			return true
		}
		debugLog("  domainExists  [][]  Domain \"$domainCode\" does not exist.", log)
		return false
	}
	
//=========================================================================
//============================ Users functions ============================
//=========================================================================

//------------------- retrieve metadata for all users ---------------------
	def static getAllUsersMetadata(log, context, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getAllUsersMetadata\".", log)
		debugLog("  getAllUsersMetadata  [][]  Get json list of all users metadata ...", log)
		def userIdent=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/internal/rest/user/$userIdent/search"
		
		commandString=["curl", urlToSMP+urlExt,
                                    "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                    "-H","X-XSRF-TOKEN: " + xsrf_token,
                                    "-X", "GET",
                                    "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)
			
        assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:getAllUsersMetadata: Error while trying to retrieve all users metadata. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  getAllUsersMetadata  [][]  Users metadata retrieved successfully.", log)	
		return commandResult[0]
	}
//---------------------- get single user Metadata -------------------------
	def static getUserMetadata(log, context, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getUserMetadata\".", log)
		debugLog("  getUserMetadata  [][]  Get metadata for user \"$username\" ...", log)
		def jsonSlurper = new JsonSlurper()
		def userMeta=null
		
		def dataMap=jsonSlurper.parseText(getAllUsersMetadata(log, context, authenticationUser, authenticationPwd))
		
		dataMap.serviceEntities.each{ use ->
			if(use.username.toLowerCase().equals(username.toLowerCase())){
				debugLog("  getUserMetadata  [][]  User \"$username\" found.", log)
				userMeta=use
			}
		}
		
		return userMeta
	}

//------------------------- get single user Id ----------------------------
	def static getUserId(log, context, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getUserId\".", log)
		debugLog("  getUserId  [][]  Get userId for username \"$username\" ...", log)

		def dataMap=getUserMetadata(log, context, username, authenticationUser, authenticationPwd)
		
		assert(dataMap!=null),"Error:getUserId: Error while trying to retrieve userId for user $username: metadata returned is null"	
		return dataMap.userId
	}
	
//-------------------------- retrieve user Id -----------------------------
	def static retrieveUserId(log, context, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"retrieveUserId\".", log)
		debugLog("  retrieveUserId  [][]  Retrieve userId for username \"$username\" ...", log)
		
		def userIdent=null
		def usId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def dataMap=null
		def jsonSlurper = new JsonSlurper()

		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		usId=getUserId(log, context, username)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/internal/rest/user/$userIdent/$usId/retrieve"
		
        commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-v"]	
										 
		commandResult=runCommandInShell(log, commandString)
		        assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:retrieveUserId: Error while trying to retrieving metadata for user $username. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
		debugLog("  retrieveUserId  [][]  Metadata retrieved successfully.", log)	
		dataMap=jsonSlurper.parseText(commandResult[0])
		
		return dataMap.userId
	}

//---------------------------- get users List -----------------------------
	def static getUsersList(log, context, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"getUsersList\".", log)
		debugLog("  getUsersList  [][]  Get all users list ...", log)
		def usList=[]
		def jsonSlurper = new JsonSlurper()
		
		def dataMap=jsonSlurper.parseText(getAllUsersMetadata(log, context, authenticationUser, authenticationPwd))
		dataMap.serviceEntities.each{ use ->
			usList<<use.username
		}		
		return usList
	}



//----------------------- check if a user exists --------------------------
	def static boolean userExists(log, context, username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"userExists\".", log)
		debugLog("  userExists  [][]  Check if user \"$username\" exists ...", log)
		
		def dataMap=getUsersList(log, context, authenticationUser, authenticationPwd)
		if(dataMap*.toLowerCase().contains(username.toLowerCase())){
			debugLog("  userExists  [][]  User \"$username\" exists.", log)
			return true
		}
		debugLog("  userExists  [][]  User \"$username\" does not exist.", log)
		return false
	}

//----------------------------- Create a user -----------------------------
	def static createUser(log, context, String username, String role="USER", String smpTheme="default_theme", String smpLocale="en", authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"createUser\".", log)
		debugLog("  createUser  [][]  Creating user \"$username\" ...", log)
		def json=null
		def userIdent=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=userExists(log, context, username, authenticationUser, authenticationPwd)
		
		if(!exists){
			json=ifWindowsEscapeJsonString('{\"active\":true, \"username\":\"' + "${username}" + '\", \"role\":\"' + "${role}" + '\",\"smpTheme\":\"' + "${smpTheme}" + '\",\"smpLocale\":\"' + "${smpLocale}"+ '\"	}')
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/user/$userIdent/create"
		
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:createUser: Error while trying to create user $username. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  createUser  [][]  User \"$username\" created successfully.", log)	
		}else{
			debugLog("  createUser  [][]  User \"$username\" already exists: skip the creation ...", log)
		}
	}	

//----------------------------- Delete a user -----------------------------
	def static deleteUser(log, context, String username, authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"deleteUser\".", log)
		debugLog("  deleteUser  [][]  Deleting user \"$username\" ...", log)
		def userIdent=null
		def userId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=userExists(log, context, username, authenticationUser, authenticationPwd)

		if(exists){
			userId=retrieveUserId(log, context, username, authenticationUser, authenticationPwd)
			xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
			userIdent=USERID		
			urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
			urlExt="/ui/internal/rest/user/$userIdent/$userId/delete"
			try{
				commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "DELETE",
                                         "-v"]	
										 
				commandResult=runCommandInShell(log, commandString)
			}finally{
				XSFRTOKEN=null
			}
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:deleteUser: Error while trying to delete user $username. CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]

			debugLog("  deleteUser  [][]  User \"$username\" successfully deleted.", log)	
		}else{
			debugLog("  deleteUser  [][]  User \"$username\" does not exist: skip the deletion ...", log)
		}
	}

//----------------------------- Create a user -----------------------------
	def static UpdateUserPasswordFor(log, context, String username, String oldPass="123456", String newPass="DomibusEdel-12345", String outcome="success", String message="",authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"UpdateUserPasswordFor\".", log)
		debugLog("  UpdateUserPasswordFor  [][]  Updating password for user \"$username\" from \"$oldPass\" to \"$newPass\" ...", log)
		def json=null
		def userIdent=null
		def usId=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 
		def exists=userExists(log, context, username, authenticationUser, authenticationPwd)
		
		assert(exists),"Error:UpdateUserPasswordFor: User $username does not exist ..."

		
		json=ifWindowsEscapeJsonString('{\"currentPassword\":\"' + "${oldPass}" + '\", \"newPassword\":\"' + "${newPass}" + '\"	}')
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		usId=retrieveUserId(log, context, username, authenticationUser, authenticationPwd)
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/internal/rest/user/$userIdent/change-password-for/$usId"
		
		try{
			commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
			commandResult=runCommandInShell(log, commandString)
		}finally{
			XSFRTOKEN=null
		}
		if(outcome.toLowerCase().equals("success")){
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:UpdateUserPasswordFor: Error while trying to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  UpdateUserPasswordFor  [][]  Password for user \"$username\" updated successfully.", log)
		}else{
			assert(!(commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/)),"Error:UpdateUserPasswordFor: Error while running command to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			if(!message.equals("")){
				assert(commandResult[1].contains(message)),"Error:UpdateUserPasswordFor: Error while running command to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			}
			debugLog("  UpdateUserPasswordFor  [][]  Password for user \"$username\" was not updated (as expected).", log)
		}
			
	}	
	
//----------------------------- Create a user -----------------------------
	def static UpdateUserPassword(log, context, String username, String oldPass="123456", String newPass="DomibusEdel-12345", String outcome="success", String message="",authenticationUser=SYSTEM_USER, authenticationPwd=SYSTEM_PWD){
		debugLog("Calling \"UpdateUserPassword\".", log)
		debugLog("  UpdateUserPassword  [][]  Updating password for user \"$username\" from \"$oldPass\" to \"$newPass\" ...", log)
		def json=null
		def userIdent=null
		def xsrf_token=null
		def urlToSMP=null
		def urlExt=null
		def commandString=null
		def commandResult=null 

		
		json=ifWindowsEscapeJsonString('{\"currentPassword\":\"' + "${oldPass}" + '\", \"newPassword\":\"' + "${newPass}" + '\"	}')
		xsrf_token=returnXsfrToken(log, context, authenticationUser, authenticationPwd)
		userIdent=USERID
		urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		urlExt="/ui/public/rest/user/$userIdent/change-password"
		
		try{
			commandString=["curl", urlToSMP+urlExt,
                                         "--cookie", context.expand('${projectDir}') + File.separator + "cookie.txt",
                                         "-H", "Content-Type: application/json",
                                         "-H","X-XSRF-TOKEN: " + xsrf_token,
                                         "-X", "PUT",
                                         "--data", json,
                                         "-v"]	
										 
			commandResult=runCommandInShell(log, commandString)
		}finally{
			XSFRTOKEN=null
		}
		if(outcome.toLowerCase().equals("success")){
			assert((commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/) || commandResult[1].contains("successfully")),"Error:UpdateUserPassword: Error while trying to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			debugLog("  UpdateUserPassword  [][]  Password for user \"$username\" updated successfully.", log)
		}else{
			assert(!(commandResult[1]==~ /(?s).*HTTP\/\d.\d\s*200.*/)),"Error:UpdateUserPassword: Error while running command to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			if(!message.equals("")){
				assert(commandResult[0].contains(message)),"Error:UpdateUserPassword: Error while running command to update the password for the user \"$username\". CommandResult[0]:" +commandResult[0] + "| commandResult[1]:" + commandResult[1]
			}
			debugLog("  UpdateUserPassword  [][]  Password for user \"$username\" was not updated (as expected).", log)
		}
			
	}
//=========================================================================
//========================= Curl command functions ========================
//=========================================================================
//-------------------------- Run curl command -----------------------------
    static def runCommandInShell(log, inputCommand) {
        debugLog("Calling \"runCommandInShell\".", log)
        def outputCatcher = new StringBuffer()
        def errorCatcher = new StringBuffer()
		debugLog("  runCommandInShell  [][]  Run curl command: " + inputCommand, log)
        if (inputCommand) {
            def proc = inputCommand.execute()
            if (proc != null) {
                proc.waitForProcessOutput(outputCatcher, errorCatcher)
            }
        }
		debugLog("  runCommandInShell  [][]  outputCatcher: " + outputCatcher.toString(), log)
		debugLog("  runCommandInShell  [][]  errorCatcher: " + errorCatcher.toString(), log)
        return ([outputCatcher.toString(), errorCatcher.toString()])
    }
	
//------------------------ Get returnXsfr Token ---------------------------
	static String returnXsfrToken(log, context, String userLogin=SYSTEM_USER, passwordLogin=SYSTEM_PWD) {
		def String output=""
		if ((XSFRTOKEN == null) || !LOGGED_USER.toLowerCase().equals(userLogin)) {
			output=fetchCookieHeader(log, context, userLogin, passwordLogin)
			XSFRTOKEN=output.find("XSRF-TOKEN.*;").replace("XSRF-TOKEN=", "").replace(";", "")
			USERID=output.find("userId.*,").split(",")[0].replace("\"","").replace("userId:","")
			LOGGED_USER=userLogin
		}
		return XSFRTOKEN
	}

//-------------------------- Get Cookie Header ----------------------------
	static String fetchCookieHeader(log, context, String userLogin = SYSTEM_USER, passwordLogin = SYSTEM_PWD) {
		def json=null
		json = ifWindowsEscapeJsonString('{\"username\":\"' + "${userLogin}" + '\",\"password\":\"' + "${passwordLogin}" + '\"}')
		def urlToSMP=getSoapUiCustomProperty(log, context, "url", "project",false)
		def urlExt="/ui/public/rest/security/authentication"
        def commandString = ["curl", urlToSMP+urlExt,
                             "-i",
                             "-H",  "Content-Type: application/json",
                             "--data-binary", json, "-c", context.expand('${projectDir}') + File.separator + "cookie.txt",
                             "--trace-ascii", "-"]		
        def commandResult = runCommandInShell(log, commandString)
        assert(commandResult[0].contains("XSRF-TOKEN")),"Error:Authenticating user: Error while trying to connect to the DomiSMP."
        return commandResult[0]    
	}

//---------------------- Format json input for windows --------------------
    static def ifWindowsEscapeJsonString(json) {
        if (System.properties['os.name'].toLowerCase().contains('windows'))
            json = json.replace("\"", "\\\"")
        return json
    }

//------------------------ Format json input for curl ---------------------	
    static def formatJsonForCurl(log, input) {
        if (System.properties['os.name'].toLowerCase().contains('windows')) {
            assert(input != null),"Error:formatJsonForCurl: input string is null."
            assert(input.contains("[") && input.contains("]")),"Error:formatJsonForCurl: input string is corrupted."
            def intermediate = input.substring(input.indexOf("[") + 1, input.lastIndexOf("]")).replace("\"", "\"\"\"")
            return "[" + intermediate + "]"
        }
        return input
    }
	
//---------------------- url code space char for curl ---------------------	
    def static urlEncode(log, toEncode,format="UTF-8"){	
	
		debugLog("  ====  Calling \"urlEncode\".",log)
		return java.net.URLEncoder.encode(toEncode, format).replaceAll("\\+", "%20")
			
    }

//----------------------------- Compare 2 tables --------------------------
	def String compareTables(tab1,tab2){
		def found = 0;
		if(tab1.size()!=tab2.size()){
			return "false";
		}
		for (String item1 : tab1) {
			for (String item2 : tab2) {
				if(item1 == item2){
					found = 1;
				}
			}
			if(found==0){
				return "false";
			}
			found = 0;
		}
		found = 0;
		for (String item2 : tab2) {
			for (String item1 : tab1) {
				if(item1 == item2){
					found = 1;
				}
			}
			if(found==0){
				return "false";
			}
			found = 0;
		}
		return "true";
	}

//------------------------------- Locate test -----------------------------
// To be deprecated
	def String locateTest(){
		// Returns: "--TestCase--testStep--"
		return("--"+context.testCase.name+"--"+context.testCase.getTestStepAt(context.getCurrentStepIndex()).getLabel()+"--  ");
	}

//------------------------- Dump request table ----------------------------
// To be deprecated	
	def String dumpRequestTable(){
		def ii = 0;
		log.info("== Request Table ==");
		while(ii<requestDataTable.size()){
			log.info "--"+requestDataTable[ii][1]+"--"+requestDataTable[ii][0]+"--";
			ii=ii+1;
		}
		log.info("================================");
	}

//--------------------------- Dump response table -------------------------
// To be deprecated
	def String dumpResponseTable(){
		def ii = 0;
		log.info("== Response Table ==");
		while(ii<responseDataTable.size()){
			log.info "--"+responseDataTable[ii][1]+"--"+responseDataTable[ii][0]+"--";
			ii=ii+1;
		}
		log.info("================================");
	}

//------------------------------- Dump table ------------------------------
// To be deprecated
	def dumpTable(tableToDump, String name, dimension){
		def ii = 0;
		if(dimension=='2'){
			log.info("== "+name+" Table ==");
			while(ii<tableToDump.size()){
				log.info "--"+tableToDump[ii][1]+"--"+tableToDump[ii][0]+"--";
				ii=ii+1;
			}
		}
		if(dimension=='1'){
			log.info("== "+name+" Table ==");
			while(ii<tableToDump.size()){
				log.info "--"+tableToDump[ii]+"--";
				ii=ii+1;
			}
		}
		log.info("================================");
	}
	
	
//=========================================================================
//=========================== Signature functions =========================
//=========================================================================
//-------------------------- Return Dom document --------------------------
	def Document returnDOMDocument(String input){
		def Document doc = null;
		try {
			def DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			def DocumentBuilder db = dbf.newDocumentBuilder();
			def InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(input));
			doc = db.parse(is);
		}catch(Exception ex) {
			assert (0),"-- returnDOMDocument function -- Error occurred while trying to build document from String: "+ex;
		}
		return(doc);
	}

//------------------------ Decode X509 Certificate ------------------------
	def Certificate decodeX509Certificate(Document doc){
		def Certificate cert = null
		def String certMessage = null

		// Check Certificate
		def Element smpCert = findElement(doc,"X509Certificate","SMP",null)
		assert (smpCert != null),locateTest()+"Error: SMP X509Certificate Signature not found in the response."

		certMessage=smpCert.getTextContent()
		debugLog("decodeX509Certificate	[][] smpCert="+smpCert, log)
		debugLog("decodeX509Certificate	[][] certMessage="+certMessage, log)
		def CertificateFactory cf = CertificateFactory.getInstance("X509")
		def InputStream is = new ByteArrayInputStream(Base64.getMimeDecoder().decode(certMessage))
		cert =cf.generateCertificate(is)
		return (cert)
	}

//--------------------------- Validate Signature --------------------------
	def Boolean validateSignature(Document doc){
		def Boolean coreValidity = true;
		def Boolean svValidity = true;
		def Boolean refValidity = true;
		def PublicKey publicKey=null
		def XMLSignatureFactory fac=null
		def DOMValidateContext valContext=null 
		def XMLSignature signature=null 
		
		debugLog("Entering function \"validateSignature\".", log)
		// Find the signature of the SMP node to extract the signature algorithm
		def Element smpSig = findElement(doc,"Signature","SMP",SIGNATURE_XMLNS);
		assert (smpSig != null),locateTest()+"Error: SMP Signature not found in the response.";
		try{
			publicKey = decodeX509Certificate(doc).getPublicKey();
		}catch(Exception ex) {
			assert (0),"validateSignature	[][] Error occurred while trying to get the public key: "+ex;
		}

		fac=XMLSignatureFactory.getInstance("DOM");
		
		try{
			valContext=new DOMValidateContext(publicKey, smpSig);
		}catch(Exception ex) {
			assert (0),"validateSignature	[][] Error occurred while trying to get the context: "+ex;
		}
		valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
		// Unmarshal the XMLSignature.
		try{
			signature=fac.unmarshalXMLSignature(valContext);
		}catch(Exception ex) {
			assert (0),"validateSignature	[][] Error occurred while trying to unmarshal the xml signature: "+ex;
		}		
		try{
			coreValidity = signature.validate(valContext);
		}catch(Exception ex) {
			assert (0),"validateSignature	[][] Error occurred while trying to validate the signature: "+ex;
		}
		debugLog("validateSignature	[][] core validation="+coreValidity, log)
		// Check core validation status
        if (coreValidity == false) {
			debugLog("validateSignature	[][] Signature failed core validation ...", log)
            svValidity=signature.getSignatureValue().validate(valContext)
			debugLog("validateSignature	[][] Signature value validation status: "+svValidity, log)
            // check the validation status of each Reference
			signature.getSignedInfo().getReferences().each{ ref ->
				InputStream is2= ref.getDigestInputStream()
				debugLog("validateSignature	[][] Reference \"$ref\" content="+is2, log)
				refValidity=ref.validate(valContext)
				debugLog("validateSignature	[][] Reference \"$ref\" validation status: "+svValidity, log)
			}
        }		
		return (coreValidity);
	}

//-------------------- Validate Signature Extension -----------------------
	def Boolean validateSignatureExtension(Document doc){
		def Boolean validFlag = true;

		// Find the signature of the SMP node to extract the signature algorithm
		def Element smpSig = findElement(doc,"Signature","Extension",SIGNATURE_XMLNS);
		if(smpSig==null){
			log.info "No extension Signature.";
			return(true);
		}

		def PublicKey publicKey = decodeX509Certificate(doc).getPublicKey();
		def XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		def DOMValidateContext valContext = new DOMValidateContext(publicKey, smpSig);
		valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);


		// Unmarshal the XMLSignature.
		def XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		//displaySignatureInfo(signature,valContext);

		try {
			validFlag = signature.validate(valContext);
		}catch(Exception ex) {
			assert (0),"-- validateSignatureExtension function -- Error occurred while trying to validate the signature: "+ex;
		}
		if(validFlag==false){
			printErrorSigValDetails(valContext,signature);
		}
		return (validFlag);
	}

//----------------------------- Find Element ------------------------------

	def Element findElement(Document doc, String elementName, String target, String nameSpace){
		def elements =null;
		if(nameSpace!=null){
			elements = doc.getElementsByTagNameNS(nameSpace, elementName);
		}else{
			elements = doc.getElementsByTagName(elementName);
		}
		if(target=="SMP"){
			if(elements.getLength()>1){
				return (Element) elements.item(1);
			}
			if(elements.getLength()==1){
				return (Element) elements.item(0);
			}
			if(elements.getLength()<1){
				return null;
			}
		}else{
			if(elements.getLength()>1){
				return (Element) elements.item(0);
			}else{
				return null;
			}
		}
	}

//----------------- Print signature valiation errors ----------------------
	def printErrorSigValDetails(DOMValidateContext valContext, XMLSignature signature){
		boolean sv = signature.getSignatureValue().validate(valContext);
		log.info("signature validation status: " + sv);
		if (sv == false) {
			// Check the validation status of each Reference.
			Iterator i1 = signature.getSignedInfo().getReferences().iterator();
			//log.info i1.getAt(0);
			//log.info i1.getAt(1);
			//log.info i1.toString();
			for (int j = 0; i1.hasNext(); j++) {
				boolean refValid = ((org.jcp.xml.dsig.internal.dom.DOMReference) i1.next()).validate(valContext);
				log.info("ref[" + j + "] validity status: " + refValid);
			}
		}
	}

//----------------------- Display signature info --------------------------
	def displaySignatureInfo(XMLSignature signature,DOMValidateContext valContext){
		log.info"======== Signature ========";
		log.info "- Signature Value: "+signature.getSignatureValue().getValue();
		log.info"===========================";
	}


//=========================================================================
//========================== Utilities functions ==========================
//=========================================================================
//---------------------- Converta Json string to Map ----------------------
	def static convertJsonStrToMap(log,input=""){	

		def jsonSlurper=new JsonSlurper()
		def resultMap=null
		
		try{
			resultMap=jsonSlurper.parseText(input)
		}catch(Exception ex){
				assert 0,"convertJsonStrToMap: exception occurred while parsing input \"$input\" value as json: " + ex
		}

		return resultMap
    }

//-------------------- Sleep for a specific duration ----------------------
	static def waitFor(log,String duration="0", String unit="min", message=""){
		def totalDur=0
		def valueArr=[]
		def slpMes="$duration $unit $message"

		try{
			valueArr=duration.split("\\.")
			if(unit.toLowerCase().equals("sec")){
				totalDur=(valueArr[0] as Integer)*1000
			}else{
				if(valueArr.size()==2){
					totalDur=( ((valueArr[0] as Integer)*60)+(valueArr[1] as Integer) )*1000
					slpMes=valueArr[0]+" min and "+valueArr[1]+" sec  $message"
				}else{
					totalDur=(duration as Integer)*60*1000
				}
			}
		}catch(NumberFormatException ex){
			debugLog("waitFor:Error: Please verify the duration format. Must be int (min or sec) or int.int (only min)", log)
			assert 0,"Exception occurred: " + ex
		}
		debugLog("------------ Sleeping for $slpMes ...", log)
		sleep(totalDur)
		debugLog("------------ Sleeping for $slpMes DONE", log)
	}

//-------------------- Extract node value from input ----------------------	
	def String extractNodeValue(String nodeName, String input,String parent=null, String attribute=null){
		def String result = "0";
		if(nodeName=="Extension"){
			result="";
		}
		def rootNode = new XmlSlurper().parseText(input);
		def allNodes = rootNode.depthFirst().each{
			if((it.name()== nodeName)&&((parent==null)||(it.parent().name()==parent))){
				if(attribute==null){
					if(nodeName=="Extension"){
						result=result+it.text();
					}else{
						result=it.text();
					}
				}
				else{
					result=it.@{attribute.toString()}.text();
				}
			}
		}
		if(result==""){
			result="0";
		}
		return result;
	}

//------------------------ Extract extension values -----------------------	
// To be deprecated ?
	// Extensions are extracted in a different way
	def String extractExtValues(String extInput){
		def String extResult = "";
		def String inputTrimmed=extInput.replaceAll("\n","").replaceAll("\r", "").replaceAll(">\\s+<", "><").replaceAll("%23","#").replaceAll("%3A",":");
		def containerExt = (inputTrimmed =~ /<Extension>((?!<Extension>).)*<\/Extension>/);
		while(containerExt.find()){
			extResult = extResult+containerExt.group();
		}
		if(extResult==""){
			extResult="0";
		}
		//log.info "<AllExtensionsRoot>"+extResult+"</AllExtensionsRoot>";
		return "<AllExtensionsRoot>"+extResult+"</AllExtensionsRoot>";
	}

//----------------------------- Compare 2 XMLs ----------------------------	
// To be deprecated
	def Boolean compareXMLs(String request, String response){
		def DetailedDiff myDiff = new DetailedDiff(new Diff(request, response));
		def List allDifferences = myDiff.getAllDifferences();

		if(!myDiff.similar()){
			// Enable for more logs
			for (Object object : allDifferences){
				Difference difference = (Difference)object;
				log.error(difference);
				log.error("============================");
			}
			return false;
		}
		return true;
	}

//----------------------------- Compare 2 XMLs ----------------------------	
	// Difference between XMLs
	def static Boolean compare2XMLs(log,String request, String response){
		XMLUnit.setIgnoreWhitespace(true);
		def DetailedDiff myDiff = new DetailedDiff(new Diff(request, response));
		def List allDifferences = myDiff.getAllDifferences();

		if(!myDiff.similar()){
			// Enable for more logs
			for (Object object : allDifferences){
				Difference difference = (Difference)object;
				log.error(difference);
				log.error("============================");
			}
			return false;
		}
		return true;
	}

//--------------------- Remove namespaces from input ----------------------	
	def static String removeNamespaces(String input){
		def String result = null;
		result = input.replaceAll(/<\/.{0,4}:/,"</");
		result = result.replaceAll(/<.{0,4}:/,"<");
		result = result.replace("%23","#");
		result = result.replace("%3A",":");
		return result;
	}

//------------------------- Extract part from XML -------------------------	
	def String extractPartFromXML(String input, String requestName){
		def String startTag = null;
		def String endTag = null;
		def String result = null;

		//if(requestName.toLowerCase()=="servicegroup"){
		//	startTag = "<ServiceMetadataReferenceCollection>";
		//	endTag = "</ServiceGroup>";
		//}
		if(requestName.toLowerCase()=="signature"){
			startTag = "<Signature";
			endTag = "</SignedServiceMetadata>";
		}
		if(requestName.toLowerCase()=="servicemetadata"){
			startTag = "<ProcessList>";
			endTag = "</ServiceInformation>";
		}
		result = input.substring(input.indexOf(startTag), input.indexOf(endTag));
		return result;
	}
	def static String extractFromXML(String input, String target){
		def String startTag = "<"+target+">";
		def String endTag = "</"+target+">";
		def targetSize=endTag.length()
		def String result = null;

		if(input.indexOf(startTag)<0){
			return ""
		}
		result = input.substring(input.indexOf(startTag), input.lastIndexOf(endTag)+targetSize);
		return result;
	}

//---------------------- Return hash value of input -----------------------	
	def String returnHash(String input){
		def String result = MessageDigest.getInstance("MD5").digest(input.toLowerCase(Locale.US).bytes).encodeHex().toString()
		return result;
	}


//=========================================================================
//======================= Handle soapui properties ========================
//=========================================================================
//---------------- Get the SoapUi custom property value -------------------
	def static getSoapUiCustomProperty(log, context, String custPropName="", String level="testcase",canBeNull=false){	

		def retPropVal=null
		
        switch (level.toLowerCase()) {
            case  "testcase":
                retPropVal=context.expand('${#TestCase#'+custPropName+'}')
                break
            case "testsuite":
                retPropVal=context.expand('${#TestSuite#'+custPropName+'}')
                break
            case "project":
                retPropVal=context.expand('${#Project#'+custPropName+'}')
                break
            default:
				debugLog("Warning:getSoapUiCustomProperty: Unknown type of custom property level: \"$level\". Assuming test case level property", log)
				retPropVal=context.expand('${#TestCase#'+custPropName+'}')
        }
		
		if(!canBeNull){
			assert(retPropVal!= null),"Error:getSoapUiCustomProperty: Couldn't fetch property \"$custPropName\" value at level \"$level\""
			assert(retPropVal.trim()!= ""),"Error:getSoapUiCustomProperty: Property \"$custPropName\" at level \"$level\" returned value is empty."		
		}
		return retPropVal
    }

//------------ Get the SoapUi custom property value as a map --------------
	def static getSoapUiCustomPropAsMap(log, context, String custPropName="", String level="testcase"){	

		def textInput=getSoapUiCustomProperty(log,context, custPropName, level)
		def resultMap=convertJsonStrToMap(log,textInput)

		return resultMap
    }

//---------- Get the SoapUi custom property value as a Integer ------------
	def static getSoapUiCustomPropAsInt(log, context, String custPropName="", String level="testcase"){	

		def resultInt=null
		
		resultInt=(getSoapUiCustomProperty(log,context, custPropName, level)  as Integer)

		return resultInt
    }

//------------ Get the SoapUi custom property value as a Long -------------
	def static getSoapUiCustomPropAsLong(log, context, String custPropName="", String level="testcase"){	

		def resultInt=null
		
		resultInt=(getSoapUiCustomProperty(log,context, custPropName, level)  as Long)

		return resultInt
    }
	
//---------- Get the SoapUi custom property value as a Boolean ------------
	def static getSoapUiCustomPropAsBoolean(log, context, String custPropName="", String level="testcase"){	

		def resultInt=null
		
		resultInt=Boolean.valueOf(getSoapUiCustomProperty(log,context, custPropName, level))

		return resultInt
    }

//----------------- Set the SoapUi custom property value ------------------
	def static setSoapUiCustomProperty(log,testRunner, String custPropName="", String custPropValue="", level="testcase"){	
        switch (level.toLowerCase()) {
            case  "testcase":
                testRunner.testCase.setPropertyValue(custPropName,custPropValue)
                break
            case "testsuite":
                testRunner.testCase.testSuite.setPropertyValue(custPropName,custPropValue)
                break
            case "project":
                testRunner.testCase.testSuite.project.setPropertyValue(custPropName,custPropValue)
                break
            default:
				debugLog("Warning:setSoapUiCustomProperty: Unknown type of custom property level: \"$level\". Assuming test case level property", log)
				testRunner.testCase.setPropertyValue(custPropName,custPropValue)
        }	
		debugLog("  ====  \"setSoapUiCustomProperty\" DONE.", log)
    }


// To be deprecated
//// filterForTestSuite = /PASSING_AUTO_BAMBOO/   // for multiple test suite use more advanced regexp like for example:  /PASSING_AUTO_BAMBOO|PASSING_NOT_FOR_BAMBOO/
//// filterForTestCases = /SMP001.*/   //for single test case use simple regexp like /SMP001.*/

	def cleanAndAddHeaderElement(filterForTestSuite,  filterForTestCases, String fieldName, String newValue = null, restMethodName = 'PUT ServiceGroup') {

		debugLog("START: modyfication of test requests", log)
		context.testCase.testSuite.project.getTestSuiteList().each { testSuite ->
			if (testSuite.getLabel() =~ filterForTestSuite) {
				debugLog("test suite: " + testSuite.getLabel(), log)
				testSuite.getTestCaseList().each { testCase ->
					if (testCase.getLabel() =~ filterForTestCases) {
						debugLog("test label:" + testCase.getLabel(), log)
						testCase.getTestStepList().each {testStep ->
							if (testStep instanceof RestTestRequestStep && testStep.getRestMethod().name == restMethodName) {

								def hOld = testStep.getHttpRequest().getRequestHeaders()
								hOld.remove(fieldName)
								hOld.remove(fieldName.capitalize())
								hOld.remove(fieldName.toUpperCase())
								if (newValue)
									hOld[fieldName] = [newValue]
								testStep.getHttpRequest().setRequestHeaders(hOld)
								debugLog("For testStep:" + testStep.name + "; Header: "  + testStep.getHttpRequest().getRequestHeaders(), log)
							}
						}
					}

				}
			}
		}
		debugLog("END: Modification of requests hedears finished", log)
	}

}
