package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;


/**
 * Service group domain
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Service
public class DomainService {

    public static final Pattern DOMAIN_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,50}");

    @Autowired
    private DomainDao domainDao;


    /**
     * Method checks if domain is in right format. Domain must contains only alphanomeric chars and it shoud
     * not be longer than 50 chars.
     *
     * @param domain
     * @return
     */
    @NotNull
    public DBDomain getDomain(final String domain) {
        if (StringUtils.isBlank(domain)) {
            Optional<DBDomain> res = domainDao.getTheOnlyDomain();
            if (!res.isPresent()) {
                throw new SMPRuntimeException(MISSING_DOMAIN);
            }
            return res.get();
        }
        // else test if domain is ok.
        if (!DOMAIN_ID_PATTERN.matcher(domain).matches()) {
            throw new SMPRuntimeException(INVALID_DOMAIN_CODE, domain, DOMAIN_ID_PATTERN);
        }
        // get domain by code
        Optional<DBDomain> domEntity = domainDao.getDomainByCode(domain);
        if (!domEntity.isPresent()) {
            throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, domain);
        }
        return domEntity.get();
    }
}
