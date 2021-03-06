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

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.text.DistinguishedNamesCodingUtil;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;
import static java.net.URLDecoder.decode;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Service
public class ServiceGroupService {


    private static final String UTF_8 = "UTF-8";

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupService.class);

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    @Autowired
    private ServiceGroupDao serviceGroupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DomainService domainService;

    @Autowired
    private SmlConnector smlConnector;

    /**
     * Method returns ServiceGroup entity for participant with references. If domain is null/empty it returns ServiceMetadata
     * for all domains else it returns metadata only for particular domain.
     * If domain is given and participantId is not defined on that domain than NotFoundException if thrown.
     *
     * @param participantId
     * @return ServiceGroup for participant id
     */
    public ServiceGroup getServiceGroup(ParticipantIdentifierType participantId) {
        // normalize participant identifier
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(participantId);
        Optional<DBServiceGroup> sg = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());
        if (!sg.isPresent()){
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        return ServiceGroupConverter.toServiceGroup(sg.get());
    }

    /**
     * Method save (or update if exists) serviceGroup for domain and servicegroup owner
     *
     * @param serviceGroup
     * @param domain
     * @param serviceGroupOwner
     * @param authenticatedUser
     * @return
     */
    @Transactional
    public boolean saveServiceGroup(ServiceGroup serviceGroup, String domain, String serviceGroupOwner, String authenticatedUser) {

        // normalize participant identifier
        ParticipantIdentifierType normalizedParticipantId = caseSensitivityNormalizer.normalize(serviceGroup.getParticipantIdentifier());
        LOG.businessDebug(SMPMessageCode.BUS_SAVE_SERVICE_GROUP,domain,normalizedParticipantId.getValue(), normalizedParticipantId.getScheme()  );

        // normalize service group owner


        String ownerName = defineGroupOwner(serviceGroupOwner, authenticatedUser);
        Optional<DBUser> newOwner = userDao.findUserByIdentifier(ownerName);
        if (!newOwner.isPresent()
                && !StringUtils.isBlank(serviceGroupOwner) && serviceGroupOwner.contains(":")) {
            // try harder
            String[] val = splitSerialFromSubject(ownerName);
            String newOwnerName = DistinguishedNamesCodingUtil.normalizeDN(val[0]) + ':' + val[1];
            LOG.info("Owner not found: {} try with normalized owner: {}.", ownerName, newOwnerName);
            newOwner = userDao.findUserByIdentifier(newOwnerName);
            ownerName = newOwnerName;
        }

        if (!newOwner.isPresent()) {
            SMPRuntimeException ex = new SMPRuntimeException(USER_NOT_EXISTS);
            LOG.businessError(SMPMessageCode.BUS_SAVE_SERVICE_GROUP_FAILED,domain,normalizedParticipantId.getValue(), normalizedParticipantId.getScheme(), ex.getMessage()  );
            throw ex;
        }
        // get domain
        DBDomain dmn = domainService.getDomain(domain);
        // get servicegroup
        Optional<DBServiceGroup> dbServiceGroup = serviceGroupDao.findServiceGroup(normalizedParticipantId.getValue(),
                normalizedParticipantId.getScheme());


        byte[] extensions = ServiceGroupConverter.extractExtensionsPayload(serviceGroup);

        if (dbServiceGroup.isPresent()) {
            // service already exists.
            // check if user has rights to modified
            // test service owner
            DBServiceGroup sg = dbServiceGroup.get();
            validateOwnership(ownerName, sg);
            //check is domain exists
            Optional<DBServiceGroupDomain> sgd = sg.getServiceGroupForDomain(dmn.getDomainCode());
            if (!sgd.isPresent()){
                SMPRuntimeException ex = new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN,domain,normalizedParticipantId.getValue(), normalizedParticipantId.getScheme());
                LOG.businessError(SMPMessageCode.BUS_SAVE_SERVICE_GROUP_FAILED,domain,normalizedParticipantId.getValue(), normalizedParticipantId.getScheme(), ex.getMessage()  );
                throw ex;
            }
            //update extensions
            sg.setExtension(extensions);
            serviceGroupDao.update(sg);
            return false;
        } else {
            //Save ServiceGroup
            DBServiceGroup newSg = new DBServiceGroup();
            newSg.setParticipantIdentifier(normalizedParticipantId.getValue());
            newSg.setParticipantScheme(normalizedParticipantId.getScheme());
            newSg.setExtension(extensions);
            newSg.addDomain(dmn); // add initial domain
            // set initial domain as not registered
            newSg.getServiceGroupDomains().get(0).setSmlRegistered(false);
            newSg.getUsers().add(newOwner.get());
            // persist (make sure this is not in transaction)
            serviceGroupDao.persistFlushDetach(newSg);
            // register to SML
            boolean registered = smlConnector.registerInDns(normalizedParticipantId, dmn);
            if (registered) {
                // update status in database
                newSg.getServiceGroupDomains().get(0).setSmlRegistered(registered);
                serviceGroupDao.update(newSg);
            }
            return true;
        }
    }

    /**
     * Method returns URL decoded serviceGroupOwner if not null/empty, else return authenticated user. If
     * User dan not be decoded InvalidOwnerException is thrown.
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

    public static String[] splitSerialFromSubject(String certificateId)  {


        int idx = certificateId.lastIndexOf(":");
        if (idx <= 0) {
            throw new SMPRuntimeException(INVALID_OWNER,  certificateId);
        }
        return new String[]{certificateId.substring(0, idx), certificateId.substring(idx+1)};

    }

    /**
     * Method validates if user owner with identifier is owner of servicegroup
     * @param  ownerIdentifier
     * @param dbsg
     */
    protected void validateOwnership(String ownerIdentifier, DBServiceGroup dbsg){

        Optional<DBUser> own = userDao.findUserByIdentifier(ownerIdentifier);
        if (!own.isPresent()){
            throw new  SMPRuntimeException(USER_NOT_EXISTS);
        }
        if (!dbsg.getUsers().contains(own.get())){
            throw new  SMPRuntimeException(USER_IS_NOT_OWNER,ownerIdentifier,
                    dbsg.getParticipantIdentifier(), dbsg.getParticipantScheme() );
        }
    }

    /**
     * Method validates if user owner with identifier is owner of servicegroup
     * @param  ownerIdentifier
     * @param serviceGroupIdentifier
     */
    @Transactional
    public boolean isServiceGroupOwner(String ownerIdentifier, String serviceGroupIdentifier ){
        ParticipantIdentifierType pt = caseSensitivityNormalizer.normalizeParticipant(serviceGroupIdentifier);
        Optional<DBServiceGroup> osg = serviceGroupDao.findServiceGroup(pt.getValue(), pt.getScheme());
        Optional<DBUser> own = userDao.findUserByIdentifier(ownerIdentifier);
        return osg.isPresent() && own.isPresent() && osg.get().getUsers().contains(own.get());
    }


    @Transactional
    public void deleteServiceGroup(ParticipantIdentifierType serviceGroupId) {
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);

        Optional<DBServiceGroup> dbServiceGroup = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());

        if (!dbServiceGroup.isPresent()) {
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        DBServiceGroup dsg = dbServiceGroup.get();
        // register to SML
        // unergister all the domains
        for (DBServiceGroupDomain sgdom: dsg.getServiceGroupDomains()) {
            if (sgdom.isSmlRegistered()) {
                smlConnector.unregisterFromDns(normalizedServiceGroupId, sgdom.getDomain());
            }
        }

        serviceGroupDao.removeServiceGroup(dsg);
    }
}
