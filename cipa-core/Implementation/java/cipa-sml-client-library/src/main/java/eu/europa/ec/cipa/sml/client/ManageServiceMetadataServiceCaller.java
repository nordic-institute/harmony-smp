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
package eu.europa.ec.cipa.sml.client;

import java.net.URL;

import javax.annotation.Nonnull;
import javax.xml.ws.BindingProvider;

import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataService;
import org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataServiceSoap;
import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.peppol.sml.ISMLInfo;

/**
 * This class is used for calling the service metadata interface of the SML.
 * 
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class ManageServiceMetadataServiceCaller {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ManageServiceMetadataServiceCaller.class);

  private final URL m_aEndpointAddress;

  /**
   * Creates a service caller for the service metadata interface
   * 
   * @param aSMLInfo
   *        The SML info object
   */
  public ManageServiceMetadataServiceCaller (@Nonnull final ISMLInfo aSMLInfo) {
    this (aSMLInfo.getManageServiceMetaDataEndpointAddress ());
  }

  /**
   * Creates a service caller for the service metadata interface
   * 
   * @param aEndpointAddress
   *        The address of the SML management interface.
   */
  public ManageServiceMetadataServiceCaller (@Nonnull final URL aEndpointAddress) {
    if (aEndpointAddress == null)
      throw new NullPointerException ("endpointAddress");
    m_aEndpointAddress = aEndpointAddress;

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Using SML endpoint address '" + m_aEndpointAddress.toExternalForm () + "'");
  }

  /**
   * Create the main WebService client for the specified endpoint address.
   * 
   * @return The WebService port to be used.
   */
  @Nonnull
  private ManageServiceMetadataServiceSoap _createPort () {
    // Use default WSDL and default QName
    final ManageServiceMetadataService aService = new ManageServiceMetadataService ();
    final ManageServiceMetadataServiceSoap aPort = aService.getManageServiceMetadataServicePort ();
    ((BindingProvider) aPort).getRequestContext ().put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                                        m_aEndpointAddress.toString ());
    return aPort;
  }

  /**
   * Creates the service metadata for the specified user.
   * 
   * @param sSMPID
   *        The certificate UID of the SMP
   * @param sSMPAddressPhysical
   *        The physical address of the SMP (Example: 198.0.0.1)
   * @param sSMPAddressLogical
   *        The logical address of the SMP (Example: http://test.com/test.svc)
   * @throws BadRequestFault
   *         The request was not well formed.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   */
  public void create (final String sSMPID, final String sSMPAddressPhysical, final String sSMPAddressLogical) throws BadRequestFault,
                                                                                                             InternalErrorFault,
                                                                                                             UnauthorizedFault {
    final ServiceMetadataPublisherServiceType aServiceMetadata = new ServiceMetadataPublisherServiceType ();
    aServiceMetadata.setServiceMetadataPublisherID (sSMPID);
    final PublisherEndpointType aEndpoint = new PublisherEndpointType ();
    aEndpoint.setLogicalAddress (sSMPAddressLogical);
    aEndpoint.setPhysicalAddress (sSMPAddressPhysical);
    aServiceMetadata.setPublisherEndpoint (aEndpoint);
    create (aServiceMetadata);
  }

  /**
   * Creates the service metadata for the specified user.
   * 
   * @param aServiceMetadata
   *        The data about the SMP
   * @throws BadRequestFault
   *         The request was not well formed.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   */
  public void create (@Nonnull final ServiceMetadataPublisherServiceType aServiceMetadata) throws BadRequestFault,
                                                                                          InternalErrorFault,
                                                                                          UnauthorizedFault {
    s_aLogger.info ("Trying to create new SMP '" +
                    aServiceMetadata.getServiceMetadataPublisherID () +
                    "' with physical address '" +
                    aServiceMetadata.getPublisherEndpoint ().getPhysicalAddress () +
                    "' and logical address '" +
                    aServiceMetadata.getPublisherEndpoint ().getLogicalAddress () +
                    "'");
    _createPort ().create (aServiceMetadata);
  }

  /**
   * Updates the specified service metadata given by the publisher id.
   * 
   * @param sSMPID
   *        The publisher id
   * @param sSMPAddressPhysical
   *        The physical address of the SMP (Example: 198.0.0.1)
   * @param sSMPAddressLogical
   *        The logical address of the SMP (Example: http://test.com/test.svc)
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws NotFoundFault
   *         The service metadata with the given publisher id was not found.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   * @throws BadRequestFault
   *         The request was not well formed.
   */
  public void update (final String sSMPID, final String sSMPAddressPhysical, final String sSMPAddressLogical) throws InternalErrorFault,
                                                                                                             NotFoundFault,
                                                                                                             UnauthorizedFault,
                                                                                                             BadRequestFault {
    final ServiceMetadataPublisherServiceType aServiceMetadata = new ServiceMetadataPublisherServiceType ();
    aServiceMetadata.setServiceMetadataPublisherID (sSMPID);
    final PublisherEndpointType aEndpoint = new PublisherEndpointType ();
    aEndpoint.setLogicalAddress (sSMPAddressLogical);
    aEndpoint.setPhysicalAddress (sSMPAddressPhysical);
    aServiceMetadata.setPublisherEndpoint (aEndpoint);
    update (aServiceMetadata);
  }

  /**
   * Updates the specified service metadata.
   * 
   * @param aServiceMetadata
   *        The service metadata instance to update.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws NotFoundFault
   *         The service metadata with the given publisher id was not found.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   * @throws BadRequestFault
   *         The request was not well formed.
   */
  public void update (@Nonnull final ServiceMetadataPublisherServiceType aServiceMetadata) throws InternalErrorFault,
                                                                                          NotFoundFault,
                                                                                          UnauthorizedFault,
                                                                                          BadRequestFault {
    s_aLogger.info ("Trying to update SMP '" +
                    aServiceMetadata.getServiceMetadataPublisherID () +
                    "' with physical address '" +
                    aServiceMetadata.getPublisherEndpoint ().getPhysicalAddress () +
                    "' and logical address '" +
                    aServiceMetadata.getPublisherEndpoint ().getLogicalAddress () +
                    "'");
    _createPort ().update (aServiceMetadata);
  }

  /**
   * Deletes the service metadata given by the publisher id.
   * 
   * @param sSMPID
   *        The publisher id of the service metadata to delete.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws NotFoundFault
   *         The service metadata with the given publisher id was not found.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   * @throws BadRequestFault
   *         The request was not well formed.
   */
  public void delete (final String sSMPID) throws InternalErrorFault, NotFoundFault, UnauthorizedFault, BadRequestFault {
    s_aLogger.info ("Trying to delete SMP '" + sSMPID + "'");
    _createPort ().delete (sSMPID);
  }

  /**
   * Returns information about the publisher given by the publisher id.
   * 
   * @param sSMPID
   *        The publisher id of the service metadata to read.
   * @return The service metadata given by the id.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws NotFoundFault
   *         The service metadata with the given publisher id was not found.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   * @throws BadRequestFault
   *         The request was not well formed.
   */
  public ServiceMetadataPublisherServiceType read (final String sSMPID) throws InternalErrorFault,
                                                                       NotFoundFault,
                                                                       UnauthorizedFault,
                                                                       BadRequestFault {
    final ServiceMetadataPublisherServiceType aSMPService = new ServiceMetadataPublisherServiceType ();
    aSMPService.setServiceMetadataPublisherID (sSMPID);
    return read (aSMPService);
  }

  /**
   * Returns information about the publisher given by the publisher id.
   * 
   * @param aSMPService
   *        The publisher id is read from this service metadata object.
   * @return The service metadata given by the id.
   * @throws InternalErrorFault
   *         An internal error happened on the server.
   * @throws NotFoundFault
   *         The service metadata with the given publisher id was not found.
   * @throws UnauthorizedFault
   *         The username or password was not correct.
   * @throws BadRequestFault
   *         The request was not well formed.
   */
  public ServiceMetadataPublisherServiceType read (@Nonnull final ServiceMetadataPublisherServiceType aSMPService) throws InternalErrorFault,
                                                                                                                  NotFoundFault,
                                                                                                                  UnauthorizedFault,
                                                                                                                  BadRequestFault {
    s_aLogger.info ("Trying to read SMP '" + aSMPService.getServiceMetadataPublisherID () + "'");
    return _createPort ().read (aSMPService);
  }
}
