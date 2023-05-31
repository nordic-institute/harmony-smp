package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBCertificateToCertificateROConverter implements Converter<DBCertificate, CertificateRO> {

    @Override
    public CertificateRO convert(DBCertificate source) {

        CertificateRO target = new CertificateRO();
        if (source.getValidTo() != null) {
            target.setValidTo(source.getValidTo());
        }
        if (source.getValidFrom() != null) {
            target.setValidFrom(source.getValidFrom());
        }
        target.setCertificateId(source.getCertificateId());
        target.setSerialNumber(source.getSerialNumber());
        target.setIssuer(source.getIssuer());
        target.setSubject(source.getSubject());
        target.setCrlUrl(source.getCrlUrl());
        target.setEncodedValue(source.getPemEncoding());

        return target;
    }
}
