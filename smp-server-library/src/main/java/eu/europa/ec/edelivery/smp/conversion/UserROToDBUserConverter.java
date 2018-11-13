package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class UserROToDBUserConverter implements Converter<UserRO, DBUser> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public DBUser convert(UserRO source) {
        DBUser dro = new DBUser();
        dro.setEmailAddress(source.getEmailAddress());
        dro.setUsername(source.getUsername());
        dro.setRole(source.getRole());
        dro.setPassword(source.getPassword());
        dro.setActive(source.isActive());
        dro.setId(source.getId());
        dro.setPasswordChanged(source.getPasswordChanged());
        if (source.getCertificate() != null) {
            DBCertificate certData = conversionService.convert(source.getCertificate(), DBCertificate.class);
            dro.setCertificate(certData);
        }
        return dro;
    }
}
