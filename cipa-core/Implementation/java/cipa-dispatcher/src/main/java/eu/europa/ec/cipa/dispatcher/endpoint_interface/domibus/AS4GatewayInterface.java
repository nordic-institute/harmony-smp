package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

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
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class AS4GatewayInterface implements IAS4GatewayInterface {

	public final static String PMODE_ROLE="GW";
	public final static String ID_TYPE="urn:oasis:names:tc:ebcore:partyid-type:iso3166-1";
	public final static String MPC= "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC";
	public final static String MEP_NAME = "One-Way/Push";
	public final static String SOAP_VERSION = "1.2";
	private PModePool pmodePool;
	private File pmodeFile = null; 
	private Properties properties=null;
	
	private void initPModePool() throws DispatcherConfigurationException{
	 properties = PropertiesUtil.getProperties(null);
		if (pmodePool == null){
			String pmodeFilePath = properties.getProperty(PropertiesUtil.AS4_PMODE_FILEPATH);
			pmodeFile = new File(pmodeFilePath);
			if (!pmodeFile.exists()){
				try {
					if (!pmodeFile.createNewFile()){
						throw new DispatcherConfigurationException("Unable to create pmode file");
					}
				} catch (IOException e) {
					throw new DispatcherConfigurationException("Unable to create pmode file");
				}
			}
			pmodePool = PModePool.load(pmodeFile);
			if (pmodePool == null){
				throw new DispatcherConfigurationException("Unable to load PMODEFile");
			}
			
		}
	}
	
	private Producer getProducer(String producerName){
		List<Producer>  producers = pmodePool.getProducers();
		for (Producer producer : producers) {
			if (producer.getName().equalsIgnoreCase(producerName)){
				return producer;
			}
		}
		return null;
	}
	
	private UserService getUserService(String userServiceName){
		
		List<UserService>  useservices = pmodePool.getUserServices();
		
		for (UserService userservice : useservices) {
			if (userservice.getName().equalsIgnoreCase(userServiceName)){
				return userservice;
			}
		}
		return null;
	}
	
	private Binding getBinding(String bindingName){
		
		List<Binding>  bindings = pmodePool.getBindings();
		
		for (Binding binding : bindings) {
			if (binding.getName().equalsIgnoreCase(bindingName)){
				return binding;
			}
		}
		return null;
	}
	
	private PMode getPMode(String pmodeName){
		
		List<PMode>  pmodes = pmodePool.getPmodes();
		
		for (PMode pmode : pmodes) {
			if (pmode.getName().equalsIgnoreCase(pmodeName)){
				return pmode;
			}
		}
		return null;
	}
	
	/**
	 * <tns:Binding name="CZ_EC_EPO_Form_E">
      <tns:MEP name="One-Way/Push">
         <tns:Leg number="1" producer="CZ-GW"
                  mpc="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC"
                  userService="EPO_Form_E_EC_GW"
                  security="sign-body-header_CZ_EC">
            <tns:Endpoint address="https://webgate.acceptance.ec.europa.eu/eprior/Phase2a/holodeck/services/msh"
                          soapVersion="1.2"/>
            <tns:As4Receipt method="response">
               <tns:As4Reliability duplicateElimination="true" maxRetries="3" interval="5" shutdown="10"/>
            </tns:As4Receipt>
         </tns:Leg>
      </tns:MEP>
   </tns:Binding>
	 */
	@Override
	public void createPartner(String senderGatewayId,String receiverGatewayId,String processId,
			String documentId, String receiverGWUlr) throws DispatcherConfigurationException {
		initPModePool();
		
		String producerName =senderGatewayId.concat("_").concat(PMODE_ROLE); 
		
		Producer producer = getProducer(producerName);
		
		String userServiceName = processId.concat("_").concat(documentId).concat("_").concat(receiverGatewayId).concat("_").concat(PMODE_ROLE);
		UserService userservice = getUserService(userServiceName);
		String bindingName = senderGatewayId.concat("_").concat(receiverGatewayId).concat("_").concat(processId).concat("_").concat(documentId);
		Binding binding = getBinding(bindingName);
		String pmodeName = bindingName;
		PMode pmode = getPMode(pmodeName);
		
		Party party = new Party();
		String defaultSecurity = properties.getProperty("as4_default_security_name");
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pmodeFile);
			if (producer == null){
				producer= new Producer();
				producer.setName(producerName);
				producer.setRole(PMODE_ROLE);
				party.setPartyId(senderGatewayId);
				party.setType(ID_TYPE);
				producer.addParty(party);
				pmodePool.getProducers().add(producer);
			}
			if (userservice == null){
				userservice= new UserService();
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
				collabInfo.setAction(documentId);
				Service service = new Service();
				service.setValue(processId);
				collabInfo.setService(service);
				userservice.setCollaborationInfo(collabInfo);
			// we always overwrite the bindind 
			if (binding == null){
				binding = new Binding();
				pmodePool.getBindings().add(binding);
			}
				binding.setName(bindingName);
				MEP mep= new MEP();
				mep.setName(MEP_NAME);
				Leg leg = new Leg();
				leg.setNumber(1);
				leg.setProducerName(producer.getName());
				leg.setMpc(MPC);
				leg.setUserServiceName(userservice.getName());
				leg.setSecurity(defaultSecurity);
				Endpoint endpoint = new Endpoint();
				endpoint.setAddress(receiverGWUlr);
				endpoint.setSoapVersion(SOAP_VERSION);
				leg.setEndpoint(endpoint);
				As4Receipt receipt = new As4Receipt();
				receipt.setValue("response");
				As4Reliability reliability = new As4Reliability();
				reliability.setDuplicateElimination(true);
				reliability.setMaxRetries(3);
				reliability.setInterval(5);
				reliability.setShutdown(10);
				receipt.setAs4Reliability(reliability);
				leg.setAs4Receipt(receipt);
				mep.getLegs().add(leg);
				binding.setMep(mep);
			if (pmode == null){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DispatcherConfigurationException("Unable to load PMODEFile");
		} catch (IOException e) {
			throw new DispatcherConfigurationException("Unable to load PMODEFile");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}
	
	public PModePool getPmodePool() {
		return pmodePool;
	}

	public void setPmodePool(PModePool pmodePool) {
		this.pmodePool = pmodePool;
	}


}
