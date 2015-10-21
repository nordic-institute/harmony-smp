/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.exception;

/**
 * @author Christian Koch
 * @version 1.0
 * @since 3.0
 */
public enum ErrorCode {
    EBMS_0001("EBMS:0001"), EBMS_0002("EBMS:0002"), EBMS_0003("EBMS:0003"), EBMS_0004("EBMS:0004"), EBMS_0005("EBMS:0005"), EBMS_0006("EBMS:0006"), EBMS_0007("EBMS:0007"),
    EBMS_0008("EBMS:0008"), EBMS_0009("EBMS:0009"), EBMS_0010("EBMS:0010"), EBMS_0011("EBMS:0011"), EBMS_0101("EBMS:0101"), EBMS_0102("EBMS:0102"), EBMS_0103("EBMS:0103"),
    EBMS_0201("EBMS:0201"), EBMS_0202("EBMS:0202"), EBMS_0301("EBMS:0301"), EBMS_0302("EBMS:0302"), EBMS_0303("EBMS:0303"), EBMS_0020("EBMS:0020"), EBMS_0021("EBMS:0021"),
    EBMS_0022("EBMS:0022"), EBMS_0023("EBMS:0023"), EBMS_0030("EBMS:0030"), EBMS_0031("EBMS:0031"), EBMS_0040("EBMS:0040"), EBMS_0041("EBMS:0041"), EBMS_0042("EBMS:0042"),
    EBMS_0043("EBMS:0043"), EBMS_0044("EBMS:0044"), EBMS_0045("EBMS:0045"), EBMS_0046("EBMS:0046"), EBMS_0047("EBMS:0047"), EBMS_0048("EBMS:0048"), EBMS_0049("EBMS:0049"),
    EBMS_0050("EBMS:0050"), EBMS_0051("EBMS:0051"), EBMS_0052("EBMS:0052"), EBMS_0053("EBMS:0053"), EBMS_0054("EBMS:0054"), EBMS_0055("EBMS:0055"), EBMS_0060("EBMS:0060");

    private String errorCodeName;

    ErrorCode(final String errorCodeName) {
        this.errorCodeName = errorCodeName;
    }

    public static ErrorCode findBy(final String errorCodeName) {
        for (final ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getErrorCodeName().equals(errorCodeName)) {
                return errorCode;
            }
        }

        throw new IllegalArgumentException("No ErrorCode found for ErrorCodeName: " + errorCodeName);
    }

    public String getErrorCodeName() {
        return this.errorCodeName;
    }

    public void setErrorCodeName(final String errorCodeName) {
        this.errorCodeName = errorCodeName;
    }
}
