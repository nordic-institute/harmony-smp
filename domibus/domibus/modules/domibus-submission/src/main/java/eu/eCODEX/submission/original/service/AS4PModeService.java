package eu.eCODEX.submission.original.service;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.module.Configuration;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class AS4PModeService {
	public final static String PMODE_ROLE = "GW";
	public final static String ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1";
	public final static String MPC = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC";
	public final static String MEP_NAME = "One-Way/Push";
	public final static String SOAP_VERSION = "1.2";
	
	
	private static final Logger s_aLogger = Logger.getLogger (AS4PModeService.class);
	
	private volatile PModePool pmodePool;
	private volatile File pmodeFile;

	public Producer getProducer(String producerName) {
		List<Producer> producers = pmodePool.getProducers();
		for (Producer producer : producers) {
			if (producer.getName().equalsIgnoreCase(producerName)) {
				return producer;
			}
		}
		return null;
	}

	public UserService getUserService(String userServiceName) {

		List<UserService> useservices = pmodePool.getUserServices();

		for (UserService userservice : useservices) {
			if (userservice.getName().equalsIgnoreCase(userServiceName)) {
				return userservice;
			}
		}
		return null;
	}

	public synchronized void initPModePool(final String pmodeFileName) throws ConfigurationException {
		if (pmodePool == null) {
			String directory = Configuration.getPModesDir();
			String pmodeFilePath = directory.concat("/").concat(pmodeFileName);
            try {
                pmodeFile = new File(pmodeFilePath);
            } catch (Exception exc) {
                s_aLogger.warn("Unable to locate pmode file: " + pmodeFilePath + ". Gonna try to create it");
                pmodeFile = new File(pmodeFilePath);
            }
			if (!pmodeFile.exists()) {
                try {
					if (!pmodeFile.createNewFile()) {
						s_aLogger.error("Unable to create pmode file");
						throw new ConfigurationException("Unable to create pmode file");
					}
                    Files.write(Paths.get(pmodeFile.toURI()), "<?xml version=\"1.0\" encoding=\"utf-8\"?><PModes></PModes>".getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				} catch (IOException e) {
					s_aLogger.error("Unable to create pmode file");
					throw new ConfigurationException("Unable to create pmode file");
				}
			}
			pmodePool = PModePool.load(pmodeFile.getAbsolutePath());
			if (pmodePool == null) {
				s_aLogger.error("Unable to create pmode file");
				throw new ConfigurationException("Unable to load PMODEFile");
			}

		}
	}

	public Binding getBinding(String bindingName) {

		List<Binding> bindings = pmodePool.getBindings();

		for (Binding binding : bindings) {
			if (binding.getName().equalsIgnoreCase(bindingName)) {
				return binding;
			}
		}
		return null;
	}

	public PMode getPMode(String pmodeName) {

		List<PMode> pmodes = pmodePool.getPmodes();

		for (PMode pmode : pmodes) {
			if (pmode.getName().equalsIgnoreCase(pmodeName)) {
				return pmode;
			}
		}
		return null;
	}

	/**
	 * <tns:Binding name="CZ_EC_EPO_Form_E"> <tns:MEP name="One-Way/Push">
	 * <tns:Leg number="1" producer="CZ-GW" mpc=
	 * "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC"
	 * userService="EPO_Form_E_EC_GW" security="sign-body-header_CZ_EC">
	 * <tns:Endpoint address=
	 * "https://webgate.acceptance.ec.europa.eu/eprior/Phase2a/holodeck/services/msh"
	 * soapVersion="1.2"/> <tns:As4Receipt method="response">
	 * <tns:As4Reliability duplicateElimination="true" maxRetries="3"
	 * interval="5" shutdown="10"/> </tns:As4Receipt> </tns:Leg> </tns:MEP>
	 * </tns:Binding>
	 * 
	 * @param senderGatewayId
	 *            --> the Sender identifier of the SBDH header
	 * @param receiverGatewayId
	 *            --> the Receiver identifier of the SBDH header
	 * @param serviceName
	 *            TODO
	 * @param action
	 *            -->
	 * @param receiverGWUlr
	 *            TODO
	 */
	public synchronized void createPartner(String senderGatewayId, String receiverGatewayId, String serviceName, String action, String receiverGWUlr) throws ConfigurationException {

		String pmodeFileName = senderGatewayId + "." + receiverGatewayId + "." + serviceName + "." + action + ".xml";

		initPModePool(pmodeFileName);

		String producerName = senderGatewayId.concat("_").concat(PMODE_ROLE);

		Producer producer = getProducer(producerName);

		String userServiceName = serviceName.concat("_").concat(receiverGatewayId).concat("_").concat(PMODE_ROLE);
		UserService userservice = getUserService(userServiceName);
		String bindingName = senderGatewayId.concat("_").concat(receiverGatewayId).concat("_").concat(serviceName);
		Binding binding = getBinding(bindingName);
		String oldGWAddress = null;
		if (binding != null){
			oldGWAddress = binding.getMep().getLegs().get(0).getEndpoint().getAddress();
		}
		String pmodeName = bindingName;
		PMode pmode = getPMode(pmodeName);

		Party party = new Party();
		String defaultSecurity = "policy-sign-body-header";

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pmodeFile);
			if (producer == null) {
				producer = new Producer();
				producer.setName(producerName);
				producer.setRole(PMODE_ROLE);
				party.setPartyId(senderGatewayId);
				party.setType(ID_TYPE);
				producer.addParty(party);
				pmodePool.getProducers().add(producer);
			}
			if (userservice == null) {
				userservice = new UserService();
				pmodePool.getUserServices().add(userservice);
			}
			userservice.setName(userServiceName);
			ToParty toParty = new ToParty();
			Party p = new Party();
			p.setPartyId(receiverGatewayId);
			p.setType(ID_TYPE);
			toParty.addParty(p);
			toParty.setRole(PMODE_ROLE);
			userservice.setToParty(toParty);
			CollaborationInfo collabInfo = new CollaborationInfo();
			collabInfo.setAction(action);
			Service service = new Service();
			service.setValue(serviceName);
			collabInfo.setService(service);
			userservice.setCollaborationInfo(collabInfo);
			// we always overwrite the binding
			if (binding == null) {
				binding = new Binding();
				pmodePool.getBindings().add(binding);
			}
			binding.setName(bindingName);
			MEP mep = new MEP();
			mep.setName(MEP_NAME);
			Leg leg = new Leg();
			leg.setNumber(1);
			leg.setProducer(producer);
			leg.setProducerName(producer.getName());
			leg.setMpc(MPC);
			leg.setUserServiceName(userservice.getName());
			leg.setSecurity(defaultSecurity);
			Endpoint endpoint = new Endpoint();
			if (receiverGWUlr != null && ! receiverGWUlr.isEmpty()){
				endpoint.setAddress(receiverGWUlr);
			}else if (oldGWAddress != null){
				endpoint.setAddress(oldGWAddress);
			}else{
				endpoint.setAddress("Undefined address");
			}
			endpoint.setSoapVersion(SOAP_VERSION);
			leg.setEndpoint(endpoint);
			As4Receipt receipt = new As4Receipt();
			receipt.setValue("response");
			receipt.setNonRepudiation(true);
			
			
			As4Reliability reliability = new As4Reliability();
			reliability.setDuplicateElimination(true);
			reliability.setMaxRetries(3);
			reliability.setInterval(5);
			reliability.setShutdown(10);
			receipt.setAs4Reliability(reliability);
			
			leg.setAs4Receipt(receipt);
			mep.getLegs().add(leg);
			binding.setMep(mep);
			if (pmode == null) {
				pmode = new PMode();
				pmode.setName(pmodeName);
				pmode.setBindingName(bindingName);
				pmodePool.getPmodes().add(pmode);
			}

			final Serializer serializer = new Persister(new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
			serializer.write(pmodePool, fos);
			fos.close();
			String directory = Configuration.getPModesDir();
			String pmodeFilePath = directory.concat("/").concat(pmodeFileName);
			final PModePool pool = PModePool.load(pmodeFilePath);
			if (pool != null) {
				Configuration.addPModePool(pool);
			}
		} catch (FileNotFoundException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new ConfigurationException("Unable to update PMODEFile");
		} catch (JAXBException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new ConfigurationException("Unable to update PMODEFile");
		} catch (IOException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new ConfigurationException("Unable to update PMODEFile");
		} catch (Exception e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new ConfigurationException("Unable to update PMODEFile");
		}
	}
}