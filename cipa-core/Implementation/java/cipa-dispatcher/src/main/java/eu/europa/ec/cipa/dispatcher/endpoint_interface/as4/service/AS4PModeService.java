package eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.domibus.ebms3.config.As4Receipt;
import eu.domibus.ebms3.config.As4Reliability;
import eu.domibus.ebms3.config.Binding;
import eu.domibus.ebms3.config.CollaborationInfo;
import eu.domibus.ebms3.config.Endpoint;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.MEP;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.config.PModePool;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.config.Service;
import eu.domibus.ebms3.config.ToParty;
import eu.domibus.ebms3.config.UserService;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.AS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class AS4PModeService {
	
	private static final Logger s_aLogger = LoggerFactory.getLogger (AS4PModeService.class);
	
	private PModePool pmodePool;
	private Properties properties;
	private File pmodeFile;

	public AS4PModeService() {
	}

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

	public void initPModePool() throws DispatcherConfigurationException {
		properties = PropertiesUtil.getProperties(null);
		if (pmodePool == null) {
			String pmodeFilePath = properties.getProperty(PropertiesUtil.AS4_PMODE_FILEPATH);
			pmodeFile = new File(pmodeFilePath);
			if (!pmodeFile.exists()) {
				try {
					if (!pmodeFile.createNewFile()) {
						s_aLogger.error("Unable to create pmode file");
						throw new DispatcherConfigurationException("Unable to create pmode file");
					}
				} catch (IOException e) {
					s_aLogger.error("Unable to create pmode file");
					throw new DispatcherConfigurationException("Unable to create pmode file");
				}
			}
			pmodePool = PModePool.load(pmodeFile);
			if (pmodePool == null) {
				s_aLogger.error("Unable to create pmode file");
				throw new DispatcherConfigurationException("Unable to load PMODEFile");
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
	 * @param as4GatewayInterface
	 *            TODO
	 * @param senderGatewayId
	 *            --> the Sender identifier of the SBDH header
	 * @param receiverGatewayId
	 *            --> the Receiver identifier of the SBDH header
	 * @param processId
	 *            TODO
	 * @param documentId
	 *            -->
	 * @param receiverGWUlr
	 *            TODO
	 */
	public void createPartner(String senderGatewayId, String receiverGatewayId, String processId, String documentId, String receiverGWUlr) throws DispatcherConfigurationException {
		initPModePool();

		String producerName = senderGatewayId.concat("_").concat(AS4GatewayInterface.PMODE_ROLE);

		Producer producer = getProducer(producerName);

		String userServiceName = processId.concat("_").concat(documentId).concat("_").concat(receiverGatewayId).concat("_").concat(AS4GatewayInterface.PMODE_ROLE);
		UserService userservice = getUserService(userServiceName);
		String bindingName = senderGatewayId.concat("_").concat(receiverGatewayId).concat("_").concat(processId).concat("_").concat(documentId);
		Binding binding = getBinding(bindingName);
		String oldGWAddress = null;
		if (binding != null){
			oldGWAddress = binding.getMep().getLegs().get(0).getEndpoint().getAddress();
		}
		String pmodeName = bindingName;
		PMode pmode = getPMode(pmodeName);

		Party party = new Party();
		String defaultSecurity = properties.getProperty("as4_default_security_name");

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pmodeFile);
			if (producer == null) {
				producer = new Producer();
				producer.setName(producerName);
				producer.setRole(AS4GatewayInterface.PMODE_ROLE);
				party.setPartyId(senderGatewayId);
				party.setType(AS4GatewayInterface.ID_TYPE);
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
			p.setType(AS4GatewayInterface.ID_TYPE);
			toParty.addParty(p);
			toParty.setRole(AS4GatewayInterface.PMODE_ROLE);
			userservice.setToParty(toParty);
			CollaborationInfo collabInfo = new CollaborationInfo();
			collabInfo.setAction(documentId);
			Service service = new Service();
			service.setValue(processId);
			collabInfo.setService(service);
			userservice.setCollaborationInfo(collabInfo);
			// we always overwrite the bindind
			if (binding == null) {
				binding = new Binding();
				pmodePool.getBindings().add(binding);
			}
			binding.setName(bindingName);
			MEP mep = new MEP();
			mep.setName(AS4GatewayInterface.MEP_NAME);
			Leg leg = new Leg();
			leg.setNumber(1);
			leg.setProducer(producer);
			leg.setProducerName(producer.getName());
			leg.setMpc(AS4GatewayInterface.MPC);
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
			endpoint.setSoapVersion(AS4GatewayInterface.SOAP_VERSION);
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
			DefaultHttpClient httpclient = new DefaultHttpClient();
			URIBuilder builder = new URIBuilder(properties.getProperty("as4_pmodereload_url"));
			builder.addParameter("pmodefile", pmodeFile.getAbsolutePath());
			HttpGet get = new HttpGet(builder.build());
			httpclient.execute(get);
		} catch (FileNotFoundException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new DispatcherConfigurationException("Unable to update PMODEFile");
		} catch (JAXBException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new DispatcherConfigurationException("Unable to update PMODEFile");
		} catch (IOException e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new DispatcherConfigurationException("Unable to update PMODEFile");
		} catch (Exception e) {
			s_aLogger.error("error occured while creating PMODE", e);
			throw new DispatcherConfigurationException("Unable to update PMODEFile");
		}
	}

	public PModePool getPmodePool() {
		return pmodePool;
	}

	public void setPmodePool(PModePool pmodePool) {
		this.pmodePool = pmodePool;
	}
}