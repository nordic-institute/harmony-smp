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
package eu.europa.ec.cipa.sml.client.console;

import java.net.URL;

import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault;

import eu.europa.ec.cipa.sml.client.ManageServiceMetadataServiceCaller;

public final class ManageSMPClient {
  private final ManageServiceMetadataServiceCaller m_aCaller;

  public ManageSMPClient (final URL aEndpointAddress) {
    m_aCaller = new ManageServiceMetadataServiceCaller (aEndpointAddress);
  }

  public void create (final String sSMPID, final String [] args, final int index) throws BadRequestFault,
                                                                                 InternalErrorFault,
                                                                                 UnauthorizedFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 2) {
      System.err.println ("Invalid number of args to create a new registry.");
      System.out.println ("Use the following two parameters: registryAddressLogical registryAddressPhysical");
      return;
    }
    create (sSMPID, args[index], args[index + 1]);
  }

  public void update (final String sSMPID, final String [] args, final int index) throws InternalErrorFault,
                                                                                 NotFoundFault,
                                                                                 UnauthorizedFault,
                                                                                 BadRequestFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 2) {
      System.err.println ("Invalid number of args to update a new registry.");
      System.out.println ("Use the following two parameters: registryAddressLogical registryAddressPhysical");
      return;
    }
    update (sSMPID, args[index], args[index + 1]);
  }

  public ServiceMetadataPublisherServiceType read (final String sSMPID, final String [] args, final int index) throws InternalErrorFault,
                                                                                                              UnauthorizedFault,
                                                                                                              BadRequestFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 0) {
      System.err.println ("Invalid number of args to list.");
      System.out.println ("There are no parameters");
      return null;
    }
    return read (sSMPID);
  }

  public void delete (final String sSMPID, final String [] args, final int index) throws InternalErrorFault,
                                                                                 NotFoundFault,
                                                                                 UnauthorizedFault,
                                                                                 BadRequestFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 0) {
      System.err.println ("Invalid number of args to delete.");
      System.out.println ("There are no parameters");
      return;
    }
    delete (sSMPID);
  }

  void create (final String sSMPID, final String addressPhysical, final String addressLogical) throws BadRequestFault,
                                                                                              InternalErrorFault,
                                                                                              UnauthorizedFault {
    m_aCaller.create (sSMPID, addressPhysical, addressLogical);
  }

  void update (final String sSMPID, final String registryAddressPhysical, final String registryAddressLogical) throws InternalErrorFault,
                                                                                                              NotFoundFault,
                                                                                                              UnauthorizedFault,
                                                                                                              BadRequestFault {
    m_aCaller.update (sSMPID, registryAddressPhysical, registryAddressLogical);
  }

  ServiceMetadataPublisherServiceType read (final String sSMPID) throws InternalErrorFault,
                                                                UnauthorizedFault,
                                                                BadRequestFault {
    try {
      final ServiceMetadataPublisherServiceType registryService = m_aCaller.read (sSMPID);
      print (registryService);
      return registryService;
    }
    catch (final NotFoundFault e) {
      printNotFound (sSMPID);
    }
    return null;
  }

  void delete (final String sSMPID) throws InternalErrorFault, NotFoundFault, UnauthorizedFault, BadRequestFault {
    m_aCaller.delete (sSMPID);
  }

  private static void print (final ServiceMetadataPublisherServiceType registryService) {
    if (registryService == null) {
      System.out.println ("Found no registry service:");
      return;
    }

    System.out.println ("Found registry service:");
    System.out.println ("  Publisher ID = " + registryService.getServiceMetadataPublisherID ());
    System.out.println ("  Endpoint address (Logical) = " +
                        registryService.getPublisherEndpoint ().getLogicalAddress ());
    System.out.println ("  Endpoint address (Physical) = " +
                        registryService.getPublisherEndpoint ().getPhysicalAddress ());
  }

  private static void printNotFound (final String sID) {
    System.out.println ("Found no registry service for ID: " + sID);
  }
}
