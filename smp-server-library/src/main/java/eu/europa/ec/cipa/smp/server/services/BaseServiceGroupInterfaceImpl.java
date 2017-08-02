/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.services;


import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.DBMSDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadataID;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import eu.europa.ec.smp.api.Identifiers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the read-only methods for the REST ServiceGroup
 * interface. It is used in the read-only interface and in the writable
 * interface.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Service
public final class BaseServiceGroupInterfaceImpl {
  private static final Logger s_aLogger = LoggerFactory.getLogger (BaseServiceGroupInterfaceImpl.class);


  @Autowired
  private IDataManager dataManager;

/*
  private static ConfigFile configFile;
  static {
    /*
     * TODO : This is a quick and dirty hack to allow the use of a configuration
     * file with an other name if it's in the classpath (like
     * smp.config.properties or sml.config.properties). If the configuration
     * file defined in applicationContext.xml couldn't be found, then the
     * config.properties inside the war is used as a fallback. Needs to be
     * properly refactored
     *
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext (new String [] { "classpath:applicationContext.xml" });
    configFile = (ConfigFile) context.getBean ("configFile");
  }
  private BaseServiceGroupInterfaceImpl () {}
*/

  /**
   * @param sServiceGroupID
   *        Requested service group ID
   * @throws Throwable
   *         in case of an error
   */
  @Nullable
  public ServiceGroup getServiceGroup(@Nullable final String sServiceGroupID) {

    s_aLogger.info (String.format("GET /%s",sServiceGroupID));

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);

    final ObjectFactory aObjFactory = new ObjectFactory ();

    // Retrieve the service group
    //final IDataManager aDataManager = DataManagerFactory.getInstance ();
    final ServiceGroup aServiceGroup = dataManager.getServiceGroup (aServiceGroupID);
    if (aServiceGroup == null) {
      // No such service group
      throw new NotFoundException(String.format("ServiceGroup '%s::%s' was not found", aServiceGroupID.getScheme(), aServiceGroupID.getValue()));
    }

    aServiceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType(new ArrayList<ServiceMetadataReferenceType>()));

    // Then add the service metadata references
    /*
    final ServiceMetadataReferenceCollectionType aCollectionType = aObjFactory.createServiceMetadataReferenceCollectionType ();
    final List <ServiceMetadataReferenceType> aMetadataReferences = aCollectionType.getServiceMetadataReferences();

    final List <DBServiceMetadataID> aDocTypeIds = dataManager.getDocumentTypes (aServiceGroupID);
    for (final DBServiceMetadataID aDocTypeId : aDocTypeIds) {
      final ServiceMetadataReferenceType aMetadataReference = aObjFactory.createServiceMetadataReferenceType ();

      UriBuilder uriBuilder = aUriInfo.getBaseUriBuilder();
      if (configFile.getString ("contextPath.output", "false").equals ("false")) {
        uriBuilder.replacePath ("");
      }
      XForwardedHttpHeadersHandler.applyReverseProxyParams(uriBuilder, httpHeaders);
      String metadataHref = uriBuilder
              .path (aServiceMetadataInterface)
              .buildFromEncoded (IdentifierUtils.getIdentifierURIPercentEncoded (aDocTypeId.asBusinessIdentifier()),
                                 IdentifierUtils.getIdentifierURIPercentEncoded (aDocTypeId.asDocumentTypeIdentifier()))
              .toString();

      aMetadataReference.setHref (metadataHref);
      aMetadataReferences.add (aMetadataReference);
    }
    aServiceGroup.setServiceMetadataReferenceCollection (aCollectionType);
    */

    s_aLogger.info (String.format("Finished getServiceGroup(%s)", sServiceGroupID));

    /*
     * Finally return it
     */
    return aServiceGroup;
  }

}
