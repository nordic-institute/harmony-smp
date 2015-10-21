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

package eu.domibus.ebms3.security.custom;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.policy.AssertionBuilderRegistry;
import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertion;
import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertionBuilder;
import org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.builders.xml.XMLPrimitiveAssertionBuilder;
import org.apache.wss4j.policy.SPConstants;
import org.apache.wss4j.policy.model.AbstractSecurityAssertion;
import org.apache.wss4j.policy.model.AlgorithmSuite;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a custom {@link org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader} in order to enable the domibus gateway to support:
 * <ol>
 * <li>the gcm variant of the aes algorithm</li>
 * <li>a key transport algorithm wihtout SHA1 dependencies</li> *
 * </ol>
 * NOTE: GCM is supported by Apache CXF via {@link org.apache.cxf.ws.security.policy.custom.DefaultAlgorithmSuiteLoader} but at time of writing (15.10.2014)
 * the corresponding AlgorithmSuites do not support digest Algorithms other than SHA1.
 */
public class DomibusAlgorithmSuiteLoader implements AlgorithmSuiteLoader {

    public static final String E_DELIVERY_ALGORITHM_NAMESPACE = "http://e-delivery.eu/custom/security-policy";

    public static final String MGF1_KEY_TRANSPORT_ALGORITHM = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
    public static final String AES128_GCM_ALGORITHM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";


    public DomibusAlgorithmSuiteLoader(Bus bus) {
        bus.setExtension(this, AlgorithmSuiteLoader.class);
    }


    public AlgorithmSuite getAlgorithmSuite(Bus bus, SPConstants.SPVersion version, Policy nestedPolicy) {
        AssertionBuilderRegistry reg = bus.getExtension(AssertionBuilderRegistry.class);
        if (reg != null) {
            final Map<QName, Assertion> assertions = new HashMap<QName, Assertion>();
            QName qName = new QName(DomibusAlgorithmSuiteLoader.E_DELIVERY_ALGORITHM_NAMESPACE, "Basic128GCMSha256");
            assertions.put(qName, new PrimitiveAssertion(qName));
            qName = new QName(DomibusAlgorithmSuiteLoader.E_DELIVERY_ALGORITHM_NAMESPACE, "Basic128GCMSha256Mgf1Sha256");
            assertions.put(qName, new PrimitiveAssertion(qName));

            reg.registerBuilder(new PrimitiveAssertionBuilder(assertions.keySet()) {
                public Assertion build(Element element, AssertionBuilderFactory fact) {
                    if (XMLPrimitiveAssertionBuilder.isOptional(element)
                            || XMLPrimitiveAssertionBuilder.isIgnorable(element)) {
                        return super.build(element, fact);
                    }
                    QName q = new QName(element.getNamespaceURI(), element.getLocalName());
                    return assertions.get(q);
                }
            });
        }
        return new DomibusAlgorithmSuiteLoader.DomibusAlgorithmSuite(version, nestedPolicy);
    }

    public static class DomibusAlgorithmSuite extends AlgorithmSuite {

        static {
            AlgorithmSuite.algorithmSuiteTypes.put(
                    "Basic128GCMSha256",
                    new AlgorithmSuite.AlgorithmSuiteType(
                            "Basic128GCMSha256",
                            SPConstants.SHA256,
                            DomibusAlgorithmSuiteLoader.AES128_GCM_ALGORITHM,
                            SPConstants.KW_AES128,
                            SPConstants.KW_RSA_OAEP,
                            SPConstants.P_SHA1_L128,
                            SPConstants.P_SHA1_L128,
                            128, 128, 128, 256, 1024, 4096
                    )
            );

            AlgorithmSuite.algorithmSuiteTypes.put(
                    "Basic128GCMSha256Mgf1Sha256",
                    new AlgorithmSuite.AlgorithmSuiteType(
                            "Basic128GCMSha256Mgf1Sha256",
                            SPConstants.SHA256,
                            DomibusAlgorithmSuiteLoader.AES128_GCM_ALGORITHM,
                            SPConstants.KW_AES128,
                            DomibusAlgorithmSuiteLoader.MGF1_KEY_TRANSPORT_ALGORITHM,
                            SPConstants.P_SHA1_L128,
                            SPConstants.P_SHA1_L128,
                            128, 128, 128, 256, 1024, 4096
                    )
            );
        }

        DomibusAlgorithmSuite(SPConstants.SPVersion version, Policy nestedPolicy) {
            super(version, nestedPolicy);
        }

        @Override
        protected AbstractSecurityAssertion cloneAssertion(Policy nestedPolicy) {
            return new DomibusAlgorithmSuiteLoader.DomibusAlgorithmSuite(this.getVersion(), nestedPolicy);
        }

        @Override
        protected void parseCustomAssertion(Assertion assertion) {
            String assertionName = assertion.getName().getLocalPart();
            String assertionNamespace = assertion.getName().getNamespaceURI();
            if (!DomibusAlgorithmSuiteLoader.E_DELIVERY_ALGORITHM_NAMESPACE.equals(assertionNamespace)) {
                return;
            }

            if ("Basic128GCMSha256".equals(assertionName)) {
                this.setAlgorithmSuiteType(AlgorithmSuite.algorithmSuiteTypes.get("Basic128GCMSha256"));
                this.getAlgorithmSuiteType().setNamespace(assertionNamespace);
            } else if ("Basic128GCMSha256Mgf1Sha256".equals(assertionName)) {
                this.setAlgorithmSuiteType(AlgorithmSuite.algorithmSuiteTypes.get("Basic128GCMSha256Mgf1Sha256"));
                this.getAlgorithmSuiteType().setNamespace(assertionNamespace);
            }
        }
    }
}
