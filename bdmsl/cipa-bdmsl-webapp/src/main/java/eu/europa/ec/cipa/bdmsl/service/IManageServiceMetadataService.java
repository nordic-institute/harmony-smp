package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageServiceMetadataService {
    ServiceMetadataPublisherBO read(String id)  throws
            BusinessException, TechnicalException;

    void create(ServiceMetadataPublisherBO smpBo) throws
            BusinessException, TechnicalException;

    void delete(String smpId) throws
            BusinessException, TechnicalException;

    void update(ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException;
}
