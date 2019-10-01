package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class UserROToDBUserConverter implements Converter<UserRO, DBUser> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public DBUser convert(UserRO source) {
        DBUser target = new DBUser();
        target.setEmailAddress(source.getEmailAddress());
        target.setUsername(source.getUsername());
        target.setRole(source.getRole());
        target.setPassword(source.getPassword());
        target.setActive(source.isActive());
        target.setId(source.getId());
        if (source.getCertificate() != null) {
            DBCertificate certData = conversionService.convert(source.getCertificate(), DBCertificate.class);
            target.setCertificate(certData);

            if(StringUtils.isBlank(source.getUsername())) {
                // set username with certificate id.
                // username as cert id is set to database to force unique users
                // and to fix issue with mysql - where null value is also unique...
                target.setUsername(certData.getCertificateId());
            }
        }
        return target;
    }
}
