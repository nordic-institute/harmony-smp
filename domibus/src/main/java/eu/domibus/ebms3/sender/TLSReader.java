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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.jsse.TLSClientParametersConfig;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * TODO: add class description
 */
@Service
public class TLSReader {
    public static final String CLIENTAUTHENTICATION_XML = "clientauthentication.xml";

    private static final Log LOG = LogFactory.getLog(TLSReader.class);

    //    @Cacheable("tlsCache")
    public TLSClientParameters getTlsClientParameters() {
        byte[] encoded = new byte[0];
        String config = "";
        try {
            encoded = Files.readAllBytes(Paths.get(System.getProperty("domibus.config.location"), CLIENTAUTHENTICATION_XML));
            config = new String(encoded, "UTF-8");
            config = config.replaceAll("\\Q${domibus.config.location}\\E", System.getProperty("domibus.config.location").replace('\\', '/'));

            return (TLSClientParameters) TLSClientParametersConfig.createTLSClientParameters(config);

        } catch (FileNotFoundException e) {
            LOG.warn("No tls configuration file " + System.getProperty("domibus.config.location") + CLIENTAUTHENTICATION_XML + " found. Mutual authentication will not be supported.");
            return null;
        } catch (IOException | RuntimeException e) {
            LOG.error("Mutual authentication will not be supported.", e);
            return null;
        }
    }
}
