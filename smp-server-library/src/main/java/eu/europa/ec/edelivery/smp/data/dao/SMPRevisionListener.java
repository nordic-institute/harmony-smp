package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBRevisionLog;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class SMPRevisionListener implements RevisionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceGroupService.class);

    @Override
    public void newRevision(Object revisionEntity) {
        DBRevisionLog rev = (DBRevisionLog) revisionEntity;
        String username = getSessionUserName();
        rev.setRevisionDate(LocalDateTime.now());
        if (StringUtils.isEmpty(username)){
            LOG.warn("Update database revision"+rev.getId()+" without session - authenticated user!");
            rev.setUserName("anonymous");
        } else {
            rev.setUserName(getSessionUserName());
        }
    }

    public String getSessionUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null?authentication.getName():null;
    }
}