/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeleteEntityValidation implements Serializable {

    private static final long serialVersionUID = 9008583888835630024L;


    private boolean validOperation;
    private String stringMessage;
    private final List<String> listIds= new ArrayList<>();
    private final List<String> listDeleteNotPermitedIds = new ArrayList<>();

    public boolean isValidOperation() {
        return validOperation;
    }

    public void setValidOperation(boolean validOperation) {
        this.validOperation = validOperation;
    }

    public String getStringMessage() {
        return stringMessage;
    }

    public void setStringMessage(String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public List<String> getListIds() {
        return listIds;
    }


    public List<String> getListDeleteNotPermitedIds() {
        return listDeleteNotPermitedIds;
    }


}
