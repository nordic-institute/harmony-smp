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
package eu.europa.ec.edelivery.smp.data.ui.exceptions;


import java.util.Objects;


/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class ErrorResponseRO {
    protected String businessCode;
    protected String errorCode;
    protected String errorDescription;
    protected String errorUniqueId;

    /**
     * Default no-arg constructor
     */
    public ErrorResponseRO() {

    }

    /**
     * Fully-initialising value constructor
     */
    public ErrorResponseRO(final String businessCode, final String errorDescription, final String errorUniqueId) {
        this.businessCode = businessCode;
        this.errorDescription = errorDescription;
        this.errorUniqueId = errorUniqueId;
    }


    public String getBusinessCode() {
        return businessCode;
    }


    public void setBusinessCode(String value) {
        this.businessCode = value;
    }


    public String getErrorDescription() {
        return errorDescription;
    }


    public void setErrorDescription(String value) {
        this.errorDescription = value;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorUniqueId() {
        return errorUniqueId;
    }


    public void setErrorUniqueId(String value) {
        this.errorUniqueId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponseRO that = (ErrorResponseRO) o;
        return businessCode.equals(that.businessCode) &&
                Objects.equals(errorDescription, that.errorDescription) &&
                errorUniqueId.equals(that.errorUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessCode, errorDescription, errorUniqueId);
    }

    @Override
    public String toString() {
        String sb = "ErrorResponseRO{" + "'businessCode'='" + businessCode + '\'' +
                ", 'errorDescription'='" + errorDescription + '\'' +
                ", 'errorUniqueId'='" + errorUniqueId + '\'' +
                '}';
        return sb;
    }
}
