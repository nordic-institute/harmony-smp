package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBRevisionLog;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;

/**
 * The purpose of the SMPRevisionListener is to update the revision log with currently logged-in username
 *
 * @author Joze Rihtarsic
 * @since 4.0
 */
public class SMPRevisionListener implements RevisionListener {
    private static final String ANONYMOUS_USER = "anonymous";

    private static final Logger LOG = LoggerFactory.getLogger(SMPRevisionListener.class);

    @Override
    public void newRevision(Object revisionEntity) {
        DBRevisionLog rev = (DBRevisionLog) revisionEntity;
        String username = getSessionUserName();
        rev.setRevisionDate(OffsetDateTime.now());
        if (StringUtils.isEmpty(username)) {
            LOG.warn("Update database revision [{}] without session - authenticated user!", rev);
            rev.setUserName(ANONYMOUS_USER);
        } else {
            rev.setUserName(getSessionUserName());
        }
    }

    public String getSessionUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
