/*-
 * #START_LICENSE#
 * smp-spi
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.smp.spi.api;


import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Implementation of the class provides the xml signature service for the document response.
 * is DomiSMP configuration specific!
 *
 *  @author Joze Rihtarsic
 *  @since 5.0
 */
public interface SmpXmlSignatureApi {


    /**
     * Creates an Enveloped XML signature which is embed to the specified node (parentSignatureNode) of the document.
     * The marshalled <code>XMLSignature</code> will be added as the last
     * child element of the specified parentSignatureNode.
     *
     * @param resourceMetadata the resource metadata to be signed
     * @param parentSignatureNode the parent of the signing node. The element must be part of the XML document to be signed
     * @param signedElementURIList the parent node the list of URIs to be signed. If List is empty then the whole document is signed
     * @throws  SignatureException if something goes wring with the signing
     */
    void createEnvelopedSignature(RequestData resourceMetadata, Element parentSignatureNode, List<String> signedElementURIList) throws SignatureException;
}
