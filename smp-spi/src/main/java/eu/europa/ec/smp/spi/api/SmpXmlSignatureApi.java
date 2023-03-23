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
