/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.security.ResourceGuard;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.text.DistinguishedNamesCodingUtil;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;
import static java.net.URLDecoder.decode;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author gutowpa
 * @since 3.0.0
 */
@Service
public class ServiceGroupService {

    private static final String UTF_8 = "UTF-8";

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupService.class);

    @Autowired
    private IdentifierService identifierService;

    @Autowired
    private ResourceGuard resourceGuard;

    @Autowired
    private ResourceDao serviceGroupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DomainService domainService;

    @Autowired
    private SmlConnector smlConnector;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Method returns ServiceGroup entity for participant with references. If domain is null/empty it returns ServiceMetadata
     * for all domains else it returns metadata only for particular domain.
     * If domain is given and participantId is not defined on that domain than NotFoundException if thrown.
     *
     * @param participantId participant identifier object
     * @return ServiceGroup for participant id
     */
    public ServiceGroup getServiceGroup(Identifier participantId) {
        // normalize participant identifier
        Identifier normalizedServiceGroupId = identifierService.normalizeParticipant(participantId);
        Optional<DBResource> sg = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());
        if (!sg.isPresent()) {
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        return toServiceGroup(sg.get(), configurationService.getParticipantIdentifierUrnValidationRexExp());
    }

    /**
     * Method save (or update if exists) serviceGroup for domain and servicegroup owner
     *
     * @param serviceGroup      service group entity to be stored
     * @param domain            domain of service group
     * @param serviceGroupOwner owner of the service group
     * @param authenticatedUser authenticated user who is trying to save service group
     * @return return true if object was stored
     */
    @Transactional
    public boolean saveServiceGroup(ServiceGroup serviceGroup, String domain, String serviceGroupOwner, String authenticatedUser) {

        // normalize participant identifier
        Identifier normalizedParticipantId = identifierService.normalizeParticipant(serviceGroup.getParticipantIdentifier().getScheme(), serviceGroup.getParticipantIdentifier().getValue());
        LOG.businessDebug(SMPMessageCode.BUS_SAVE_SERVICE_GROUP, domain, normalizedParticipantId.getValue(), normalizedParticipantId.getScheme());

        // normalize service group owner


        String ownerName = defineGroupOwner(serviceGroupOwner, authenticatedUser);
        Optional<DBUser> newOwner = userDao.findUserByIdentifier(ownerName);
        if (!newOwner.isPresent()
                && !StringUtils.isBlank(serviceGroupOwner) && serviceGroupOwner.contains(":")) {
            // try harder
            String[] val = splitSerialFromSubject(ownerName);
            String newOwnerName = DistinguishedNamesCodingUtil.normalizeDN(val[0]) + ':' + val[1];
            LOG.info("Owner not found: [{}] try with normalized owner: [{}].", ownerName, newOwnerName);
            newOwner = userDao.findUserByIdentifier(newOwnerName);
            ownerName = newOwnerName;
        }

        if (!newOwner.isPresent()) {
            LOG.error("The owner [{}] does not exist! Save service group is rejected!", ownerName);
            SMPRuntimeException ex = new SMPRuntimeException(INVALID_OWNER, ownerName);
            LOG.businessError(SMPMessageCode.BUS_SAVE_SERVICE_GROUP_FAILED, domain, normalizedParticipantId.getValue(), normalizedParticipantId.getScheme(), ex.getMessage());
            throw ex;
        }
        // get domain
        DBDomain dmn = domainService.getDomain(domain);
        // get servicegroup
        Optional<DBResource> dbServiceGroup = serviceGroupDao.findServiceGroup(normalizedParticipantId.getValue(),
                normalizedParticipantId.getScheme());

/*
        byte[] extensions = ServiceGroupConverter.extractExtensionsPayload(serviceGroup);

        if (dbServiceGroup.isPresent()) {
            // service already exists.
            // check if user has rights to modified
            // test service owner
            DBResource sg = dbServiceGroup.get();
            validateOwnership(ownerName, sg);
            //check is domain exists
            Optional<DBDomainResourceDef> sgd = sg.getServiceGroupForDomain(dmn.getDomainCode());
            if (!sgd.isPresent()) {
                SMPRuntimeException ex = new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, domain, normalizedParticipantId.getValue(), normalizedParticipantId.getScheme());
                LOG.businessError(SMPMessageCode.BUS_SAVE_SERVICE_GROUP_FAILED, domain, normalizedParticipantId.getValue(), normalizedParticipantId.getScheme(), ex.getMessage());
                throw ex;
            }
            //update extensions
            sg.setExtension(extensions);
            serviceGroupDao.update(sg);
            return false;
        } else {
            //Save ServiceGroup
            DBResource newSg = new DBResource();
            newSg.setIdentifierValue(normalizedParticipantId.getValue());
            newSg.setIdentifierScheme(normalizedParticipantId.getScheme());
            newSg.setExtension(extensions);
            newSg.addDomain(dmn); // add initial domain
            // set initial domain as not registered
            newSg.getResourceDomains().get(0).setSmlRegistered(false);

            // persist (make sure this is not in transaction)
            serviceGroupDao.persistFlushDetach(newSg);
            // register to SML
            boolean registered = smlConnector.registerInDns(normalizedParticipantId, dmn);
            if (registered) {
                // update status in database
                newSg.getResourceDomains().get(0).setSmlRegistered(registered);
                serviceGroupDao.update(newSg);
            }
            return true;
        }

 */
        return false;
    }

    /**
     * Method returns URL decoded serviceGroupOwner if not null/empty, else return authenticated user. If
     * User cannot be decoded SMPRuntimeException is thrown.
     *
     * @param serviceGroupOwner
     * @param authenticatedUser
     * @return database owner string.
     */
    protected String defineGroupOwner(final String serviceGroupOwner, final String authenticatedUser) {
        try {
            return isNotBlank(serviceGroupOwner) ? decode(serviceGroupOwner, UTF_8) : authenticatedUser;
        } catch (UnsupportedEncodingException | IllegalArgumentException ex) {
            LOG.error("Error occurred while decoding serviceGroupOwner '" + serviceGroupOwner + "'", ex);
            throw new SMPRuntimeException(INVALID_ENCODING, serviceGroupOwner, "Unsupported or invalid encoding: " + ex.getMessage());

        }

    }

    public static String[] splitSerialFromSubject(String certificateId) {


        int idx = certificateId.lastIndexOf(":");
        if (idx <= 0) {
            throw new SMPRuntimeException(INVALID_OWNER, certificateId);
        }
        return new String[]{certificateId.substring(0, idx), certificateId.substring(idx + 1)};

    }

    /**
     * Method validates if user owner with identifier is owner of servicegroup
     *
     * @param ownerIdentifier
     * @param dbsg
     */
    protected void validateOwnership(String ownerIdentifier, DBResource dbsg) {
        Optional<DBUser> own = userDao.findUserByIdentifier(ownerIdentifier);
        if (!own.isPresent()) {
            throw new SMPRuntimeException(USER_NOT_EXISTS);
        }

        if (!resourceGuard.isResourceAdmin(ownerIdentifier, dbsg.getIdentifierValue(), dbsg.getIdentifierScheme())){
            throw new SMPRuntimeException(USER_IS_NOT_OWNER, ownerIdentifier,
                    dbsg.getIdentifierValue(), dbsg.getIdentifierScheme());
        }
    }

    /**
     * Method validates if user owner with identifier is owner of servicegroup
     *
     * @param userId
     * @param serviceMetadataID
     */
    @Transactional
    public boolean isServiceGroupOwnerForMetadataID(long userId, long serviceMetadataID) {
        return serviceGroupDao.findServiceGroupDomainForUserIdAndMetadataId(userId, serviceMetadataID).isPresent();
    }



    @Transactional
    public void deleteServiceGroup(Identifier serviceGroupId) {
        /*
        final ParticipantIdentifierType normalizedServiceGroupId = identifierService.normalizeParticipant(serviceGroupId);

        Optional<DBResource> dbServiceGroup = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());

        if (!dbServiceGroup.isPresent()) {
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        DBResource dsg = dbServiceGroup.get();
        // register to SML
        // unergister all the domains
        for (DBDomainResourceDef sgdom : dsg.getResourceDomains()) {
            if (sgdom.isSmlRegistered()) {
                smlConnector.unregisterFromDns(normalizedServiceGroupId, sgdom.getDomain());
            }
        }

        serviceGroupDao.removeServiceGroup(dsg);

         */
    }

    /**
     * Method returns Oasis ServiceGroup entity with  extension and
     * empty ServiceMetadataReferenceCollectionType. If extension can not be converted to jaxb object than
     * ConversionException is thrown.
     *
     * @param dsg                - database service group entity
     * @param concatenatePartyId - regular expression if servicegroup in party identifier must be concatenate and returned in string value.
     * @return Oasis ServiceGroup entity or null if parameter is null
     */
    public ServiceGroup toServiceGroup(DBResource dsg, Pattern concatenatePartyId) {
/*todo
        if (dsg == null) {
            return null;
        }

        ServiceGroup serviceGroup = new ServiceGroup();
        String schema = dsg.getIdentifierScheme();
        String value = dsg.getIdentifierValue();

        if (StringUtils.isNotBlank(schema) && concatenatePartyId != null && concatenatePartyId.matcher(schema).matches()) {
            value = identifierService.formatParticipant(schema, value);
            schema = null;
        }
        Identifier identifier = new Identifier(value, schema);
        serviceGroup.setParticipantIdentifier(identifier);
        if (dsg.getExtension() != null) {
            try {
                List<ExtensionType> extensions = ExtensionConverter.unmarshalExtensions(dsg.getExtension());
                serviceGroup.getExtensions().addAll(extensions);
            } catch (JAXBException e) {
                throw new SMPRuntimeException(INVALID_EXTENSION_FOR_SG, e, dsg.getIdentifierValue(),
                        dsg.getIdentifierScheme(), ExceptionUtils.getRootCauseMessage(e));
            }
        }
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());

        return serviceGroup;

 */ return  null;
    }
}
