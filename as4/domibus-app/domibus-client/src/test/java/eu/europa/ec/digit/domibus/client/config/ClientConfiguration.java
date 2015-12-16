package eu.europa.ec.digit.domibus.client.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource ({
	"classpath:/endpoint.properties"
})
public class ClientConfiguration {

    /*---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */

	@Bean
	public JAXBContext jaxbContext() {
	  	JAXBContext context = null;
	   	try {
	   		context = JAXBContext.newInstance(
	   		// define here your JAXB beans
	   		);
	   	} catch (JAXBException exception) {
	   		exception.printStackTrace();
	   	}
	   	return context;
	}
    /* ---- Getters and Setters ---- */

}
