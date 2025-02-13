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

import eu.europa.ec.edelivery.smp.data.dao.DocumentDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBReviewDocumentVersionMapping;
import eu.europa.ec.edelivery.smp.data.ui.ReviewDocumentVersionRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to provide the UI Document Review Services.
 *
 * @since 5.1
 * @author Joze RIHTARSIC
 */
@Service
public class UIReviewService {


    private DocumentDao documentDao;
    private ConversionService conversionService;
    public UIReviewService(DocumentDao documentDao, ConversionService conversionService) {
        this.documentDao = documentDao;
        this.conversionService = conversionService;
    }


    /**
     * Method returns Users list of review tasks. To access the list user must be logged in.
     *
     * @param userId
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return
     */
    public ServiceResult<ReviewDocumentVersionRO> getDocumentReviewListForUser(
            Long userId,
            int page, int pageSize,
            String sortField,
            String sortOrder, Object filter) {

        ServiceResult<ReviewDocumentVersionRO> sg = new ServiceResult<>();
        long iCnt = documentDao.getDocumentReviewListForUserCount(userId);

        sg.setPage(page < 0 ? 0 : page);
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }
        sg.setPageSize(pageSize);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            List<DBReviewDocumentVersionMapping> listTask = documentDao.getDocumentReviewListForUser(userId, page, pageSize);
            List<ReviewDocumentVersionRO> result = listTask.stream().map(resource -> conversionService.convert(resource, ReviewDocumentVersionRO.class))
                    .collect(Collectors.toList());
            sg.getServiceEntities().addAll(result);
        }
        return sg;
    }
}
