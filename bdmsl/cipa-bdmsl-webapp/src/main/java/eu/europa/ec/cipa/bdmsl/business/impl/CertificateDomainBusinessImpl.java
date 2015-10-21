package eu.europa.ec.cipa.bdmsl.business.impl;

import eu.europa.ec.cipa.bdmsl.business.ICertificateDomainBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDomainDAO;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class CertificateDomainBusinessImpl extends AbstractBusinessImpl implements ICertificateDomainBusiness {

    @Autowired
    private ICertificateDomainDAO certificateDomainDAO;

    @Override
    public CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException {
        return certificateDomainDAO.findDomain(rootCertificateAlias);
    }

    @Override
    public List<CertificateDomainBO> findAll() throws TechnicalException {
        return certificateDomainDAO.findAll();
    }
}
