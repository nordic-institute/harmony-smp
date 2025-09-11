/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.utils.EntityLoggingUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_DOMAIN_CODE;
import static org.apache.commons.lang3.StringUtils.*;


/**
 * The class resolves the domain header or  given path segment  sequence
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class DomainResolverService {


    /**
     * Domain pattern as defined in documentation since SMP 3.0.0
     */
    public static final Pattern DOMAIN_ID_PATTERN = Pattern.compile("^[^-._+0-9][-._+a-zA-Z0-9]{1,63}$");
    final DomainDao domainDao;
    final GroupDao groupDao;
    final ConfigurationService configurationService;
    final SMPExceptionLanguageService smpExceptionLanguageService;

    public DomainResolverService(DomainDao domainDao, ConfigurationService configurationService, GroupDao groupDao, SMPExceptionLanguageService smpExceptionLanguageService) {
        this.domainDao = domainDao;
        this.groupDao = groupDao;
        this.configurationService = configurationService;
        this.smpExceptionLanguageService = smpExceptionLanguageService;
    }

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResolverService.class);

    /**
     * DomiSMP resolves the domain in the following order.
     * <ol>
     *   <li>If "domain" header exists attempt to determine domain via HTTP Header
     *     <ul>
     *       <li>If present and valid, use it.</li>
     *       <li>If the domain code is invalid/domain with code not exists, throw an error.</li>
     *     </ul>
     *   </li>
     *   <li>Check if only one domain is registered
     *     <ul>
     *       <li>If true, set it as the default domain (legacy behavior).</li>
     *     </ul>
     *   </li>
     *   <li>Attempt to determine domain from URL path parameters
     *     <ul>
     *       <li>Check if there are at least two path parameters.</li>
     *       <li>If so, use the first path parameter as the domain.</li>
     *     </ul>
     *   </li>
     *   <li>Attempt to use the default domain from DomiSMP configuration
     *     <ul>
     *       <li>If a default domain is configured, use it.</li>
     *     </ul>
     *   </li>
     *   <li>Fallback to the first registered domain in DomiSMP
     *     <ul>
     *       <li>If no default is configured, use the first available domain.</li>
     *     </ul>
     *   </li>
     *   <li>If all steps fail
     *     <ul>
     *       <li>Throw a “Domain not found” error.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p>
     * NOTE: To allow the domain path parameter and the HTTP header to be used together, the first path parameter is skipped if it matches a resolved domain with an HTTP parameter or "single domain condition" and if there are more than two path parameters.
     * <p/>
     *
     * @param headerParameter the domain code from  http header
     * @param pathParameter the first path parameter which can potentially be the domain code
     * @return DBDomain from the database if found, otherwise throws an exception.
     * @throws  SMPRuntimeException if no domain is found or the domain code is invalid.
     */
    public DBDomain resolveDomain(String headerParameter, String pathParameter) {
        LOG.info("Resolve domain for HTTP header [{}] and path parameter [{}]", headerParameter, pathParameter);

        // get single domain
        Optional<DBDomain> optDomain;
        // get
        if (isNotBlank(headerParameter)) {
            optDomain = validatedAndReturnDomainByCode(headerParameter);
            if (optDomain.isPresent()) {
                LOG.debug("Located domain by the http header [{}]", headerParameter);
                return optDomain.get();
            } else {
                throw new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS,
                        smpExceptionLanguageService.getMessageTranslation("error.domain.not.exists", Map.of("domainCode", headerParameter)));
            }
        }

        optDomain = domainDao.getTheOnlyDomain();
        if (optDomain.isPresent()) {
            LOG.debug("Only one domain is registered to DomiSmp [{}]", optDomain.get().getDomainCode());
            return optDomain.get();
        }

        optDomain = domainDao.getDomainByCode(pathParameter);
        if (optDomain.isPresent()) {
            LOG.debug("Located domain by the path parameter header [{}]", pathParameter);
            return optDomain.get();
        }

        String domainCode = configurationService.getDefaultDomainCode();
        optDomain = domainDao.getDomainByCode(domainCode);
        if (optDomain.isPresent()) {
            LOG.debug("Located domain by DomiSMP configuration [{}] value [{}]", SMPPropertyEnum.DEFAULT_DOMAIN.getProperty(), domainCode);
            return optDomain.get();
        }

        optDomain = domainDao.getFirstDomain();
        if (optDomain.isPresent()) {
            DBDomain domain = optDomain.get();
            LOG.info("Can not locate the domain, use the first registered domain [{}]", domain.getDomainCode());
            return domain;
        }
        throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "error.configuration.no.domains");
    }

    public Optional<DBDomain> validatedAndReturnDomainByCode(final String domain) {

        // the test if domain is ok.
        if (!DOMAIN_ID_PATTERN.matcher(domain).matches()) {
            throw new SMPRuntimeException(INVALID_DOMAIN_CODE,
                    "error.domain.invalid.domain.code")
                    .addParam("domainCode", domain)
                    .addParam("pattern", DOMAIN_ID_PATTERN);
        }
        // get domain by code
        return domainDao.getDomainByCode(domain);
    }


    /**
     * Method resolves the group for the given domain, admin user and group name.
     * If the group name is null/not given, the first group is returned. If the group name is provided
     * but the user is not admin for the group, the exception is thrown.
     *
     * @param user        admin user creating the resource
     * @param domain      domain where the resource is created
     * @param domainGroup group name
     * @return DBGroup for the given domain, user and group name.
     * @throws SMPRuntimeException if the user is not admin authorized to create the resource for the given group/domain
     */
    public List<DBGroup> resolveGroup(DBUser user, DBDomain domain, String domainGroup) {
        String userInfo = EntityLoggingUtils.entityToString(user, EntityLoggingUtils.NULL_USER);
        LOG.debug("Resolve group for domain [{}] and user [{}] and group [{}]", domain.getDomainCode(),
                userInfo,
                domainGroup);
        if (user == null) {
            LOG.debug("User is null, return null");
            return Collections.emptyList();
        }

        String username = user.getUsername();
        String domainCode = domain.getDomainCode();
        // return all groups for domain. groups will be filtered on
        // authorization for action step
        List<DBGroup> authorizedGroup = groupDao.getAllGroupsForDomain(domain);


        if (isBlank(domainGroup)) {
            // if no group is provided, return the first group
            LOG.debug("No group is provided, can not determine the group, return al domain authorized groups");
            return authorizedGroup;
        }

        if (authorizedGroup.stream().noneMatch(entity -> Strings.CI.equals(entity.getGroupName(), domainGroup))) {
            throw new SMPRuntimeException(ErrorCode.GROUP_NOT_EXISTS, "error.domain.group.not.exists", Map.of("groupName", domainGroup));
        }

        DBGroup group = authorizedGroup.stream()
                .filter(entity -> Strings.CI.equals(entity.getGroupName(), domainGroup))
                .findFirst()
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.UNAUTHORIZED,
                        "User [" + username + "] is not authorized for group ["
                                + domainGroup + "] in domain [" + domainCode + "]"));
        return Collections.singletonList(group);

    }
}
