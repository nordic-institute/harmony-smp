package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.services.ui.filters.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = "/ui/rest/keystore")
public class KeystoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(KeystoreResource.class);

    @Autowired
    private UIKeystoreService uiKeystoreService;

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public ServiceResult<CertificateRO> getKeyCertificateList() {
        List<CertificateRO> lst = uiKeystoreService.getKeystoreEntriesList();
        // clear encoded value to reduce http traffic
        lst.stream().forEach(certificateRO -> {certificateRO.setEncodedValue(null);});

        ServiceResult<CertificateRO> sg = new ServiceResult<>();
        sg.getServiceEntities().addAll(lst);
        sg.setCount((long)lst.size());
        return sg;
    }
}
