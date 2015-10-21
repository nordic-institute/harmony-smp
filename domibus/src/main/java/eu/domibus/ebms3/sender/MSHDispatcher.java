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

package eu.domibus.ebms3.sender;
/**
 * @Author Christian Koch
 * @Since 3.0
 */

//import eu.domibus.ebms3.pmode.model.PMode;

import eu.domibus.common.MSHRole;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.ebms3.common.PolicyFactory;
import eu.domibus.ebms3.common.dao.PModeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.policy.PolicyConstants;
import org.apache.neethi.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;


@Transactional
@Service
public class MSHDispatcher {


    public static final String PMODE_KEY_CONTEXT_PROPERTY = "PMODE_KEY_CONTEXT_PROPERTY";
    public static final String ASYMMETRIC_SIG_ALGO_PROPERTY = "ASYMMETRIC_SIG_ALGO_PROPERTY";
    private static final Log LOG = LogFactory.getLog(MSHDispatcher.class);

    @Autowired
    private PolicyFactory policyFactory;

    @Autowired
    private TLSReader tlsReader;

    @Autowired
    private PModeProvider pModeProvider;

    public SOAPMessage dispatch(final SOAPMessage soapMessage, final String pModeKey) throws EbMS3Exception {

        final QName serviceName = new QName("http://domibus.eu", "msh-dispatch-service");
        final QName portName = new QName("http://domibus.eu", "msh-dispatch");
        final javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
        final String endpoint = pModeProvider.getReceiverParty(pModeKey).getEndpoint();
        service.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpoint);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, javax.xml.ws.Service.Mode.MESSAGE);

        Policy policy = policyFactory.parsePolicy(pModeProvider.getLegConfiguration(pModeKey).getSecurity().getPolicy());
        LegConfiguration legConfiguration = pModeProvider.getLegConfiguration(pModeKey);
        dispatch.getRequestContext().put(PolicyConstants.POLICY_OVERRIDE, policy);
        dispatch.getRequestContext().put(ASYMMETRIC_SIG_ALGO_PROPERTY, legConfiguration.getSecurity().getSignatureMethod().getAlgorithm());
        dispatch.getRequestContext().put(PMODE_KEY_CONTEXT_PROPERTY, pModeKey);

        TLSClientParameters params = tlsReader.getTlsClientParameters();
        if (params != null && endpoint.startsWith("https://")) {
            Client client = ((DispatchImpl<SOAPMessage>) dispatch).getClient();
            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            httpConduit.setTlsClientParameters(params);
        }
        SOAPMessage result;
        try {
            result = dispatch.invoke(soapMessage);
        } catch (WebServiceException e) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0005, null, "error dispatching message to " + endpoint, e, MSHRole.SENDING);
        }
        return result;
    }

}

