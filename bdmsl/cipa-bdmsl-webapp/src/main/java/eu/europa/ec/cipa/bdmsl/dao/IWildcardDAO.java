package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.WildcardBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IWildcardDAO {
    void changeWildcardAuthorization(Long id, Long newCertificateId) throws TechnicalException;

    WildcardBO findWildcard(String scheme, CertificateBO certificateBO) throws TechnicalException;
}
