package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class CertificateROToDBCertificateConverter implements Converter<CertificateRO, DBCertificate> {

    @Override
    public DBCertificate convert(CertificateRO source) {

        DBCertificate target = new DBCertificate();
        if (source.getValidTo() != null) {
            target.setValidTo(OffsetDateTime.ofInstant(source.getValidTo().toInstant(), ZoneId.systemDefault()));
        }
        if (source.getValidFrom() != null) {
            target.setValidFrom(OffsetDateTime.ofInstant(source.getValidFrom().toInstant(), ZoneId.systemDefault()));
        }
        target.setCertificateId(source.getCertificateId());
        target.setSerialNumber(source.getSerialNumber());
        target.setIssuer(source.getIssuer());
        target.setSubject(source.getSubject());
        target.setCrlUrl(source.getCrlUrl());
        target.setPemEncoding(source.getEncodedValue());
        return target;
    }
}
