package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZoneOffset;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBCertificateToCertificateROConverter implements Converter<DBCertificate, CertificateRO> {

    @Override
    public CertificateRO convert(DBCertificate source) {
        CertificateRO target = new CertificateRO();
        if (source.getValidTo() != null) {
            target.setValidTo(Date.from(source.getValidTo().toInstant(ZoneOffset.UTC)));
        }
        if (source.getValidFrom() != null) {
            target.setValidFrom(Date.from(source.getValidFrom().toInstant(ZoneOffset.UTC)));
        }
        target.setCertificateId(source.getCertificateId());
        target.setSerialNumber(source.getSerialNumber());
        target.setIssuer(source.getIssuer());
        target.setSubject(source.getSubject());
        return target;
    }
}
