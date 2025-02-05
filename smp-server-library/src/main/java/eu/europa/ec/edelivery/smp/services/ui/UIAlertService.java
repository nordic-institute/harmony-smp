/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.AlertRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

@Service
public class UIAlertService extends UIServiceBase<DBAlert, AlertRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIAlertService.class);
    AlertDao alertDao;

    public UIAlertService(AlertDao alertDao) {
        this.alertDao = alertDao;
    }

    @Override
    protected BaseDao<DBAlert> getDatabaseDao() {
        return alertDao;
    }

    /**
     * Method returns Domain resource object list for page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return
     */
    @Transactional
    public ServiceResult<AlertRO> getTableList(int page, int pageSize,
                                               String sortField,
                                               String sortOrder, Object filter) {

        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }

    @Override
    public AlertRO convertToRo(DBAlert d) {
        AlertRO alertRO = new AlertRO();
        try {
            BeanUtils.copyProperties(alertRO, d);
            if (d.getProperties()!=null) {
                d.getProperties().forEach((s, dbAlertProperty) ->
                        alertRO.getAlertDetails().put(s, dbAlertProperty.getValue()));
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            String msg = "Error occurred while converting  DBAlert to AlertRO";
            LOG.error(msg, e);
            throw new SMPRuntimeException(INTERNAL_ERROR, "DB to RO entity conversion.", msg);
        }
        return alertRO;
    }

}
