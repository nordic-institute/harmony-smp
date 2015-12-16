package eu.europa.ec.digit.domibus.base.dao.log;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.europa.ec.digit.domibus.base.dao.AbstractDAOTest;

public class MessageLogDAOTest extends AbstractDAOTest {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	@PersistenceContext
	private EntityManager entityManager = null;

	@Autowired
	private MessageLogDao messageLogDAO = null;
	
	@Autowired
	private MessagingDao messagingDAO = null;

	/* ---- Test Methods ---- */

	@Test
	public void creationTest() {
		long count = messageLogDAO.countEntries();
		System.out.println("Eureka");
	}

	/* ---- Getters and Setters ---- */

	public MessageLogDao getMessageLogDAO() {
		return messageLogDAO;
	}

	public void setMessageLogDAO(MessageLogDao messageLogDAO) {
		this.messageLogDAO = messageLogDAO;
	}
}
