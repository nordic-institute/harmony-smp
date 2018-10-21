package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/user")
public class UserResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UIUserService uiUserService;

    @GetMapping
    @ResponseBody
    public ServiceResult<UserRO> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "user", required = false) String user) {
        return  uiUserService.getTableList(page,pageSize, orderBy, orderType, null);
    }

    @PutMapping(produces = {"application/json"})
    public void updateUserList(@RequestBody(required = true) UserRO[] updateEntities ){
        LOG.info("Update user list, count: {}", updateEntities.length);
        uiUserService.updateUserList(Arrays.asList(updateEntities));
    }

    @PostMapping(path = "certdata")
    public CertificateRO uploadFile(@RequestBody byte[] data) {
        LOG.info("Got certificate data: " + data.length);
        try {
            return uiUserService.getCertificateData(data);
        } catch (CertificateException e) {
            LOG.error("Error occurred while parsing certificate.", e);
        }
        return null;
    }
}
