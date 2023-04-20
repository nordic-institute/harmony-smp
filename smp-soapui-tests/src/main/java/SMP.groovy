import groovy.sql.Sql;
import java.sql.SQLException;
import java.security.MessageDigest;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory
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
import sun.misc.IOUtils;
import java.text.SimpleDateFormat
import com.eviware.soapui.support.GroovyUtils
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep



class SMP
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
    void finalize(){
        log.info "Test finished."
    }	

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
	// Log information wrapper 
	static def void debugLog(logMsg, logObject,  logLevel = DEFAULT_LOG_LEVEL) {
		if (logLevel.toString()=="1" || logLevel.toString() == "true") 
			logObject.info (logMsg)
	}
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
    // Simply open DB connection (dev or test depending on testEnvironment variable)	
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
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
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
	
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
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

	//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
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

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
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
//=================================================================================
//======================== Initialize the parameters names ========================
//=================================================================================	
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
//=================================================================================


	
//=================================================================================
//========================== Extract request parameters ===========================
//=================================================================================
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
//=================================================================================




//=================================================================================
//========================== Extract response parameters ==========================
//=================================================================================
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
//=================================================================================



//=================================================================================
//========================== Perform test verifications ===========================
//=================================================================================
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
		debugLog("After extractResponseParameters(testType)", log)		
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
				sigAlgo = extractNodeValue("SignatureMethod", tempoString,null, "Algorithm");
				assert(sigAlgo!= "0"), locateTest()+"Error: Signature Algorithm couldn't be extracted from the response."
				assert(SIGNATURE_ALGORITHM==sigAlgo), locateTest()+"Error: Signature Algorithm is "+sigAlgo+" instead of "+SIGNATURE_ALGORITHM+".";
				// Verify the SMP signature validity	
				def Boolean validResult = validateSignature(returnDOMDocument(tempoString));
				assert (validResult == true),locateTest()+"Error: Signature of the SMP is not valid.";
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
//=================================================================================



//=================================================================================
//=========================== Extract PUT XML contents ============================
//=================================================================================
	def String extractTextFromReq(String testStepName){
	    def fullRequest = context.testCase.getTestStepByName(testStepName);
		assert (fullRequest != null), locateTest()+"Error in function \"extractTextFromReq\": can't find test step name: \""+testStepName+"\"";
        def request = fullRequest.getProperty( "request" );
		def result = request.value.toString();
		result = result.replace("%23","#");
		result = result.replace("%3A",":");
        return result;        
	}
//=================================================================================


//=================================================================================
//============================== Extract Node Value ===============================
//=================================================================================
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
	
	// Difference between XMLs
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
	
//=================================================================================



//=================================================================================
//=========================== Remove namespaces in XML ============================
//=================================================================================
	def String removeNamespaces(String input){
		def String result = null;
		result = input.replaceAll(/<\/.{0,4}:/,"</");
		result = result.replaceAll(/<.{0,4}:/,"<");
		result = result.replace("%23","#");
		result = result.replace("%3A",":");
		return result;
	}
//=================================================================================




//=================================================================================
//========================= Extract part of XML contents ==========================
//=================================================================================
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
//=================================================================================



//=================================================================================
//========================= Return hash value of a string =========================
//=================================================================================
	def String returnHash(String input){
		def String result = MessageDigest.getInstance("MD5").digest(input.toLowerCase(Locale.US).bytes).encodeHex().toString()
		return result;
	}
//=================================================================================




//=================================================================================
//========================= Extract duplet or quadruplet ==========================
//=================================================================================
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
//=================================================================================


//=================================================================================
//=============================== Compare 2 Metadata ==============================
//=================================================================================
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
//=================================================================================	


//=================================================================================
//====================== Parse Metadata and store hash values =====================
//=================================================================================
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
//=================================================================================	



//=================================================================================
//================================ Compare 2 tables ===============================
//=================================================================================
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
//=================================================================================	



//=================================================================================
//============= Locate the test case for display it in the error logs =============
//=================================================================================	 
    def String locateTest(){
		// Returns: "--TestCase--testStep--" 
        return("--"+context.testCase.name+"--"+context.testCase.getTestStepAt(context.getCurrentStepIndex()).getLabel()+"--  ");
    }
//=================================================================================	



//=================================================================================
//============================== Dump request table ===============================
//=================================================================================	 
    def String dumpRequestTable(){
		def ii = 0;
		log.info("== Request Table ==");
		while(ii<requestDataTable.size()){
			log.info "--"+requestDataTable[ii][1]+"--"+requestDataTable[ii][0]+"--";
			ii=ii+1;
		} 
		log.info("================================");
    }
//=================================================================================	


//=================================================================================
//============================== Dump response table ==============================
//=================================================================================	 
    def String dumpResponseTable(){
		def ii = 0;
		log.info("== Response Table ==");
		while(ii<responseDataTable.size()){
			log.info "--"+responseDataTable[ii][1]+"--"+responseDataTable[ii][0]+"--";
			ii=ii+1;
		} 
		log.info("================================");
    }
//=================================================================================	


//=================================================================================
//================================== Dump table ===================================
//=================================================================================	 
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
//=================================================================================

//====================== Signature code ===================================
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

	def Certificate decodeX509Certificate(Document doc){
		def Certificate cert = null;
		def String certMessage = null;
    
		// Check Certificate
		def Element smpSig = findElement(doc,"X509Certificate","SMP",null);
		assert (smpSig != null),locateTest()+"Error: SMP X509Certificate Signature not found in the response.";
		
		certMessage=smpSig.getTextContent();
		def CertificateFactory cf = CertificateFactory.getInstance("X509");
		def InputStream is = new ByteArrayInputStream(new sun.misc.BASE64Decoder().decodeBuffer(certMessage));
		cert =cf.generateCertificate(is);
		return (cert);
	}
	
	def Boolean validateSignature(Document doc){
		def Boolean validFlag = true;
		
		// Find the signature of the SMP node to extract the signature algorithm
		def Element smpSig = findElement(doc,"Signature","SMP",SIGNATURE_XMLNS);
		assert (smpSig != null),locateTest()+"Error: SMP Signature not found in the response.";
		
		def PublicKey publicKey = decodeX509Certificate(doc).getPublicKey();
		def XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		def DOMValidateContext valContext = new DOMValidateContext(publicKey, smpSig);
		valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
		
		// Unmarshal the XMLSignature.
		def XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		try {
			validFlag = signature.validate(valContext);
		}catch(Exception ex) {
			assert (0),"-- validateSignature function -- Error occurred while trying to validate the signature: "+ex;
		}

		return (validFlag);
	}
	
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
	
	def displaySignatureInfo(XMLSignature signature,DOMValidateContext valContext){
		log.info"======== Signature ========";
		log.info "- Signature Value: "+signature.getSignatureValue().getValue();
		log.info"===========================";
	}
	
	
}