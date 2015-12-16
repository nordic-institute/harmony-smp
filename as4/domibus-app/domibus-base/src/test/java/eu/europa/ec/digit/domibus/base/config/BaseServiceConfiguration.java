package eu.europa.ec.digit.domibus.base.config;

import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBContext;

import org.easymock.EasyMock;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.domibus.common.dao.AttachmentDAO;
import eu.domibus.common.dao.ConfigurationDAO;
import eu.domibus.common.dao.ErrorLogDao;
import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.ebms3.common.CompressionMimeTypeBlacklist;
import eu.domibus.ebms3.common.MessageIdGenerator;
import eu.domibus.ebms3.common.dao.PModeDao;
import eu.europa.ec.digit.domibus.base.dao.ws.WSDAO;

/**
 * @author dijksvi
 *
 */

@Configuration
public class BaseServiceConfiguration {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Service Beans ---- */

	@Bean (name = "attachmentDAO")
	public AttachmentDAO attachmentDAO() {
		return EasyMock.createMock(AttachmentDAO.class);
	}
	
    @Bean (name = "configurationDAO")
    public ConfigurationDAO configurationDAO() {
    	return EasyMock.createMock(ConfigurationDAO.class);
    }

    @Bean
    public CompressionMimeTypeBlacklist compressionMimeTypeBlacklist() {
    	return EasyMock.createMock(CompressionMimeTypeBlacklist.class);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
    	return EasyMock.createStrictMock(EntityManagerFactory.class);
    }

    @Bean (name = "errorLogDao")
    public ErrorLogDao errorLogDAO() {
    	return EasyMock.createMock(ErrorLogDao.class);
    }

    @Bean (name = "jaxbContextConfig")
    public JAXBContext jaxbContext() {
    	return EasyMock.createMock(JAXBContext.class);
    }

    @Bean
    public MessageIdGenerator messageIdGenerator() {
    	return EasyMock.createMock(MessageIdGenerator.class);
    }

    @Bean (name = "messageLogDao")
    public MessageLogDao messageLogDAO() {
    	return EasyMock.createMock(MessageLogDao.class);
    }

    @Bean
    public MessageSource messageSource() {
    	return EasyMock.createMock(MessageSource.class);
    }

    @Bean (name = "messagingDao")
    public MessagingDao messagingDAO() {
    	return EasyMock.createMock(MessagingDao.class);
    }

    @Bean (name = "pModeProvider")
    public PModeDao pmodeDAO() {
    	return EasyMock.createMock(PModeDao.class);
    }

    @Bean (name = "wsDAO")
    public WSDAO<?, ?> wsDAO() {
    	return EasyMock.createMock(WSDAO.class);
    }



}
