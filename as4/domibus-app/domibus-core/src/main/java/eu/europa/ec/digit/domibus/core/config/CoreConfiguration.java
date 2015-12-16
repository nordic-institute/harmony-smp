package eu.europa.ec.digit.domibus.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import eu.domibus.common.validators.PayloadProfileValidator;
import eu.domibus.common.validators.PropertyProfileValidator;
import eu.domibus.ebms3.common.CompressionService;
import eu.domibus.submission.handler.DatabaseMessageHandler;
import eu.domibus.submission.transformer.impl.SubmissionAS4Transformer;

@ComponentScan({
	"eu.europa.ec.digit.domibus.core.mapper",
	"eu.europa.ec.digit.domibus.core.validator",
	"eu.europa.ec.digit.domibus.core.policy",
	"eu.europa.ec.digit.domibus.core.service"
})
@PropertySource({
    "classpath:/ebms-message.properties"
})
public abstract class CoreConfiguration {

	/* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */

    @Bean
    public PropertyProfileValidator propertyProfileValidator() {
    	return new PropertyProfileValidator();
    }

    @Bean
    public PayloadProfileValidator payloadProfileValidator() {
    	return new PayloadProfileValidator();
    }

    @Bean
    public CompressionService  compressionService() {
    	return new CompressionService();
    }

    @Bean
    public SubmissionAS4Transformer submissionAS4Transformer() {
    	return new SubmissionAS4Transformer();
    }

    @Bean
    public DatabaseMessageHandler databaseMessageHandler() {
    	return new DatabaseMessageHandler();
    }


    /* ---- Getters and Setters ---- */


}
