/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.data.dbms.model;

/**
 * Created by gutowpa on 01/02/2017.
 */
public class CommonColumnsLengths {
    public static final String DNS_HASHED_IDENTIFIER_PREFIX = "B-";
    public static final int MAX_IDENTIFIER_SCHEME_LENGTH = 100;
    public static final int MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH = 50;
    public static final int MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH = 500;
    public static final int MAX_PROCESS_IDENTIFIER_VALUE_LENGTH = 200;
    public static final String DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME = "iso6523-actorid-upis";
    public static final String DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME = "busdox-docid-qns";
    public static final String DEFAULT_PROCESS_IDENTIFIER_SCHEME = "cenbii-procid-ubl";
    public static final String URL_SCHEME_VALUE_SEPARATOR = "::";
}
