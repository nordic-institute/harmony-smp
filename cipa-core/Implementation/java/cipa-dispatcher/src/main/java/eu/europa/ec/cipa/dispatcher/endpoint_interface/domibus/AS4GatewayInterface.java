package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import eu.domibus.ebms3.config.PModePool;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class AS4GatewayInterface implements IAS4GatewayInterface {

	public final static String PMODE_ROLE="GW";
	public final static String ID_TYPE="urn:oasis:names:tc:ebcore:partyid-type:iso3166-1";
	private PModePool pmodePool;
	private File pmodeFile = null;
	
	
	private void initPModePool() throws DispatcherConfigurationException{
		Properties properties = PropertiesUtil.getProperties(null);
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
	
	@Override
	public void createPartner(String partnerGatewayId, String processId,
			String documentId, String gatewayURL, X509Certificate cert) throws DispatcherConfigurationException {
		initPModePool();
		Producer producer = getProducer(partnerGatewayId);
		Party party = new Party();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pmodeFile);
			if (producer == null){
				producer= new Producer();
				producer.setName(partnerGatewayId.concat("-").concat(PMODE_ROLE));
				producer.setRole(PMODE_ROLE);
				party.setPartyId(partnerGatewayId);
				party.setType(ID_TYPE);
				producer.addParty(party);
				pmodePool.getProducers().add(producer);
			}
            final Serializer serializer = new Persister();
            serializer.write(pmodePool, fos);
			fos.close();
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
