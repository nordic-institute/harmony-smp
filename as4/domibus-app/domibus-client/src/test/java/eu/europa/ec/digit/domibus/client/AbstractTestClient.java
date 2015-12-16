package eu.europa.ec.digit.domibus.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.europa.ec.digit.domibus.client.config.ClientConfiguration;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.DomibusBasicInterface;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    loader = AnnotationConfigContextLoader.class,
    classes = {ClientConfiguration.class}
)
@ActiveProfiles ("testing")
@TransactionConfiguration (
    defaultRollback = true
)
public abstract class AbstractTestClient {

    /* ---- Constants ---- */

	public final static String GATEWAY_BASIC_NAMESPACE_URI = "http://ec.europa.eu/digit/domibus/wsdl/endpoint/basic";
	public final static String GATEWAY_BASIC_LOCAL_PART = "DomibusBasicService";

    /* ---- Instance Variables ---- */

	@Autowired
	private Environment environment = null;

	@Autowired
	private JAXBContext jaxbContext = null;

    /* ---- Business Methods ---- */


	protected DomibusBasicInterface getDomibusBasicInterface() throws MalformedURLException {
		URL url = new URL(environment.getProperty("basic.domibus.wsdl"));
		QName qName = new QName(GATEWAY_BASIC_NAMESPACE_URI, GATEWAY_BASIC_LOCAL_PART);
		Service service = Service.create(url, qName);
		return service.getPort(DomibusBasicInterface.class);
	}

	protected Element getElement(Object jaxbObject) {
	    try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document document = factory.newDocumentBuilder().newDocument();
			marshaller.marshal(jaxbObject, document);
			return document.getDocumentElement();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	    return null;
	}

    /* ---- Getters and Setters ---- */

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

}
