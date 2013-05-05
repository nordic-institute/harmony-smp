/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.sml.server.management.sm;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;

import org.busdox.servicemetadata.locator._1.FaultType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataServiceSoap;
import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.sml.server.IRequestAuthenticationHandler;
import eu.europa.ec.cipa.sml.server.ISMPDataHandler;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.sml.server.exceptions.UnknownUserException;
import eu.europa.ec.cipa.sml.server.management.DataHandlerFactory;
import eu.europa.ec.cipa.sml.server.management.DataValidator;
import eu.europa.ec.cipa.sml.server.web.WebRequestClientIdentifier;

/**
 * An implementation of the Manage Service Metadata web service.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
// @SchemaValidation
// We can't use this, since the XMLDSIG isn't set until in the handlers.
@WebService (serviceName = "ManageServiceMetadataService",
             portName = "ManageServiceMetadataServicePort",
             endpointInterface = "org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataServiceSoap",
             targetNamespace = "http://busdox.org/serviceMetadata/ManageServiceMetadataService/1.0/",
             wsdlLocation = "WEB-INF/wsdl/ManageServiceMetadataService-1.0.wsdl")
@BindingType (value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@HandlerChain (file = "handlers.xml")
public class ManageServiceMetadataImpl implements ManageServiceMetadataServiceSoap {
  private static final Logger log = LoggerFactory.getLogger (ManageServiceMetadataImpl.class);

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private final ISMPDataHandler m_aDataHandler;
  private final IRequestAuthenticationHandler m_aReqAuthHdl;

  @Resource
  public WebServiceContext wsContext;

  public ManageServiceMetadataImpl () {
    m_aDataHandler = DataHandlerFactory.getSMPDataHandler ();
    m_aReqAuthHdl = DataHandlerFactory.getGenericDataHandler ();
  }

  private void _handleException (@Nonnull final Throwable e) throws NotFoundFault,
                                                            UnauthorizedFault,
                                                            BadRequestFault,
                                                            InternalErrorFault {
    if (e instanceof NotFoundException) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
    if (e instanceof UnauthorizedException) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    if (e instanceof UnknownUserException) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    if (e instanceof BadRequestException) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
    // All others as internal errors
    final FaultType faultInfo = m_aObjFactory.createFaultType ();
    faultInfo.setFaultMessage (e.getMessage ());
    throw new InternalErrorFault (e.getMessage (), faultInfo, e);
  }

  public void create (final ServiceMetadataPublisherServiceType aSMPData) throws BadRequestFault,
                                                                         InternalErrorFault,
                                                                         UnauthorizedFault {
    if (log.isDebugEnabled ())
      log.debug ("create(ServiceMetadataPublisherServiceType arg0)");

    try {
      // Validate input data
      DataValidator.validate (aSMPData);

      // no client unique ID validation here - the only place where the ID would
      // be created
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);

      // Perform action
      m_aDataHandler.createSMPData (aSMPData, sClientUniqueID);

      log.info ("Created SMP " +
                aSMPData.getServiceMetadataPublisherID () +
                " with URLs " +
                aSMPData.getPublisherEndpoint ().getPhysicalAddress () +
                "/" +
                aSMPData.getPublisherEndpoint ().getLogicalAddress ());
    }
    catch (final Throwable t) {
      try {
        _handleException (t);
      }
      catch (final NotFoundFault ex) {
        // never happens
      }
    }
  }

  public ServiceMetadataPublisherServiceType read (final ServiceMetadataPublisherServiceType messagePart) throws BadRequestFault,
                                                                                                         InternalErrorFault,
                                                                                                         NotFoundFault,
                                                                                                         UnauthorizedFault {
    if (log.isDebugEnabled ())
      log.debug ("read()");

    try {
      // Validate input data
      DataValidator.validateSMPID (messagePart.getServiceMetadataPublisherID ());

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      final ServiceMetadataPublisherServiceType ret = m_aDataHandler.getSMPData (messagePart.getServiceMetadataPublisherID (),
                                                                                 sClientUniqueID);
      log.info ("Read SMP data of " + messagePart.getServiceMetadataPublisherID ());
      return ret;
    }
    catch (final Throwable t) {
      _handleException (t);
      // Never reached
      assert false;
      return null;
    }
  }

  public void update (final ServiceMetadataPublisherServiceType aSMPData) throws InternalErrorFault,
                                                                         NotFoundFault,
                                                                         UnauthorizedFault,
                                                                         BadRequestFault {
    if (log.isDebugEnabled ())
      log.debug ("update(ServiceMetadataPublisherServiceType arg0)");

    try {
      // Validate input data
      DataValidator.validate (aSMPData);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.updateSMPData (aSMPData, sClientUniqueID);

      log.info ("Updated SMP " +
                aSMPData.getServiceMetadataPublisherID () +
                " with URLs " +
                aSMPData.getPublisherEndpoint ().getPhysicalAddress () +
                "/" +
                aSMPData.getPublisherEndpoint ().getLogicalAddress ());
    }
    catch (final Throwable t) {
      _handleException (t);
    }
  }

  public void delete (final String sSMPID) throws BadRequestFault, InternalErrorFault, NotFoundFault, UnauthorizedFault {
    if (log.isDebugEnabled ())
      log.debug ("delete(ServiceMetadataPublisherServiceType arg0)");

    try {
      // Validate input data
      DataValidator.validateSMPID (sSMPID);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.deleteSMPData (sSMPID, sClientUniqueID);

      log.info ("Deleted SMP " + sSMPID);
    }
    catch (final Throwable t) {
      _handleException (t);
    }
  }
}
