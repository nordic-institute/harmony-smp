/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.ebms3.common.dao;

import eu.domibus.common.configuration.model.*;
import eu.domibus.common.configuration.model.Process;
import eu.domibus.common.exception.ConfigurationException;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kochc01 on 02.03.2015.
 */
public class CachingPModeProvider extends PModeProvider {

    private static final Log LOG = LogFactory.getLog(CachingPModeProvider.class);


    //Dont access directly, use getter instead
    private Configuration configuration;


    protected Configuration getConfiguration() {
        if (this.configuration == null) {
            this.init();
        }
        return this.configuration;
    }

    @Override
    public void init() {
        if (!this.configurationDAO.configurationExists()) {
            CachingPModeProvider.LOG.warn("No processing modes found. To exchange messages, upload configuration file through the web gui.");
            return;
        }
        this.configuration = this.configurationDAO.read();
    }


    @Override
    //FIXME: only works for the first leg, as sender=initiator
    protected String findLegName(String agreementRef, String senderParty, String receiverParty, String service, String action) throws EbMS3Exception {
        List<LegConfiguration> candidates = new ArrayList<>();
        for (Process process : this.getConfiguration().getBusinessProcesses().getProcesses()) {
            for (Party party : process.getInitiatorParties()) {
                if (party.getName().equals(senderParty)) {
                    for (Party responder : process.getResponderParties()) {
                        if (responder.getName().equals(receiverParty)) {
                            if (process.getAgreement().getName().equals(agreementRef)) {
                                candidates.addAll(process.getLegs());
                            }
                        }
                    }
                }
            }
        }
        if (candidates == null || candidates.isEmpty()) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "No Candidates for Legs found", null, null, null);
        }
        for (LegConfiguration candidate : candidates) {
            if (candidate.getService().getName().equals(service) && candidate.getAction().getName().equals(action)) {
                return candidate.getName();
            }
        }
        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "No matching leg found", null, null, null);
    }

    @Override
    protected String findActionName(String action) throws EbMS3Exception {
        for (Action action1 : this.getConfiguration().getBusinessProcesses().getActions()) {
            if (action1.getValue().equals(action)) {
                return action1.getName();
            }
        }
        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "No matching action found", null, null, null);
    }

    @Override
    protected String findServiceName(eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service service) throws EbMS3Exception {
        for (Service service1 : this.getConfiguration().getBusinessProcesses().getServices()) {
            if (service1.getServiceType().equals(service.getType()) && service1.getValue().equals(service.getValue())) {
                return service1.getName();
            }
        }
        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "No machting service found", null, null, null);
    }

    @Override
    protected String findPartyName(List<PartyId> partyId) throws EbMS3Exception {
        for (Party party : this.getConfiguration().getBusinessProcesses().getParties()) {
            for (PartyId id : partyId) {
                for (Identifier identifier : party.getIdentifiers()) {
                    if (id.getType().equals(identifier.getPartyIdType().getValue()) && id.getValue().equals(identifier.getPartyId())) {
                        return party.getName();
                    }
                }
            }
        }
        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0003, "No matching party found", null, null, null);
    }

    @Override
    protected String findAgreementRef(AgreementRef agreementRef) throws EbMS3Exception {
        if (agreementRef == null || agreementRef.getValue() == null || agreementRef.getValue().isEmpty()) {
            return ""; //AgreementRef is optional
        }

        for (Agreement agreement : this.getConfiguration().getBusinessProcesses().getAgreements()) {
            if ((((agreementRef.getType() == "") && (agreement.getType() == null)) || agreementRef.getType().equals(agreement.getType())) && agreementRef.getValue().equals(agreement.getValue())) {
                return agreement.getName();
            }
        }
        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "No matching agreementRef found", null, null, null);//FIXME: Throw ValueInconsistent if CPA not recognized [5.2.2.7]
    }

    @Override
    public Party getSenderParty(String pModeKey) {
        String partyKey = this.getSenderPartyNameFromPModeKey(pModeKey);
        for (Party party : this.getConfiguration().getBusinessProcesses().getParties()) {
            if (party.getName().equals(partyKey)) {
                return party;
            }
        }
        throw new ConfigurationException("no matching sender party found with name: " + partyKey);
    }

    @Override
    public Party getReceiverParty(String pModeKey) {
        String partyKey = this.getReceiverPartyNameFromPModeKey(pModeKey);
        for (Party party : this.getConfiguration().getBusinessProcesses().getParties()) {
            if (party.getName().equals(partyKey)) {
                return party;
            }
        }
        throw new ConfigurationException("no matching receiver party found with name: " + partyKey);
    }

    @Override
    public Service getService(String pModeKey) {
        String serviceKey = this.getServiceNameFromPModeKey(pModeKey);
        for (Service service : this.getConfiguration().getBusinessProcesses().getServices()) {
            if (service.getName().equals(serviceKey)) {
                return service;
            }
        }
        throw new ConfigurationException("no matching service found with name: " + serviceKey);
    }

    @Override
    public Action getAction(String pModeKey) {
        String actionKey = this.getActionNameFromPModeKey(pModeKey);
        for (Action action : this.getConfiguration().getBusinessProcesses().getActions()) {
            if (action.getName().equals(actionKey)) {
                return action;
            }
        }
        throw new ConfigurationException("no matching action found with name: " + actionKey);
    }

    @Override
    public Agreement getAgreement(String pModeKey) {
        String agreementKey = this.getAgreementRefNameFromPModeKey(pModeKey);
        for (Agreement agreement : this.getConfiguration().getBusinessProcesses().getAgreements()) {
            if (agreement.getName().equals(agreementKey)) {
                return agreement;
            }
        }
        throw new ConfigurationException("no matching agreement found with name: " + agreementKey);
    }

    @Override
    public LegConfiguration getLegConfiguration(String pModeKey) {
        String legKey = this.getLegConfigurationNameFromPModeKey(pModeKey);
        for (LegConfiguration legConfiguration : this.getConfiguration().getBusinessProcesses().getLegConfigurations()) {
            if (legConfiguration.getName().equals(legKey)) {
                return legConfiguration;
            }
        }
        throw new ConfigurationException("no matching legConfiguration found with name: " + legKey);
    }

    @Override
    public boolean isMpcExistant(String mpc) {
        for (Mpc mpc1 : this.getConfiguration().getMpcs()) {
            if (mpc1.getName().equals(mpc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRetentionDownloadedByMpcName(String mpcName) {
        for (Mpc mpc1 : this.getConfiguration().getMpcs()) {
            if (mpc1.getName().equals(mpcName)) {
                return mpc1.getRetentionDownloaded();
            }
        }

        CachingPModeProvider.LOG.error("No mpc with name: " + mpcName + " found. Assuming message retention of 0 for downloaded messages.");

        return 0;
    }

    @Override
    public int getRetentionUndownloadedByMpcName(String mpcName) {
        for (Mpc mpc1 : this.getConfiguration().getMpcs()) {
            if (mpc1.getName().equals(mpcName)) {
                return mpc1.getRetentionUndownloaded();
            }
        }

        CachingPModeProvider.LOG.error("No mpc with name: " + mpcName + " found. Assuming message retention of -1 for undownloaded messages.");

        return -1;
    }

    @Override
    public List<String> getMpcList() {
        List<String> result = new ArrayList();
        for (Mpc mpc : configuration.getMpcs()) {
            result.add(mpc.getName());
        }
        return result;
    }
}
