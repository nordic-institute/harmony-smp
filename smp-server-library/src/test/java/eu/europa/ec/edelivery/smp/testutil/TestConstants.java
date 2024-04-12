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
package eu.europa.ec.edelivery.smp.testutil;




public class TestConstants {

    public static final String TEST_GROUP_A = "group-a";
    public static final String TEST_GROUP_B = "group-b";

    public static final String TEST_EXTENSION_IDENTIFIER = "oasis-smp-extension";
    public static final String TEST_RESOURCE_DEF_SMP10_URL = "oasis-smp-1";
    public static final String TEST_RESOURCE_DEF_SMP10_ID = "edelivery-oasis-smp-1.0-servicegroup";
    public static final String TEST_SUBRESOURCE_DEF_SMP10_URL = "services";
    public static final String TEST_SUBRESOURCE_DEF_SMP10_ID = "edelivery-oasis-smp-1.0-servicemetadata";
    public static final String TEST_RESOURCE_DEF_CPP = "oasis-cpp";


    public static final String TEST_DOMAIN_CODE_1 = "utestPeppol01";
    public static final String TEST_DOMAIN_CODE_2 = "utestEHhealth02";
    public static final String TEST_DOMAIN_CODE_3 = "utestRegistered03";

    public static final String TEST_SML_SUBDOMAIN_CODE_1 = ""; // peppol subdomain is empty string
    public static final String TEST_SML_SUBDOMAIN_CODE_2 = "ehealth";



    public static final String TEST_SG_ID_1 = "0007:001:utest";
    public static final String TEST_SG_ID_2 = "urn:eu:ncpb:utest";
    public static final String TEST_SG_ID_3 = "0007:002:utest";
    public static final String TEST_SG_ID_4 = "0007:004:utest";
    public static final String TEST_SG_ID_NO_SCHEME = "No-Scheme-Party-Id";
    public static final String TEST_SG_ID_PL = "urn:poland:ncpb:utest";
    public static final String TEST_SG_ID_PL2 = "urn:Poland:ncpb";

    public static final String TEST_SG_SCHEMA_1 = "iso6523-actorid-upis";
    public static final String TEST_SG_SCHEMA_2 = "ehealth-actorid-qns";
    public static final String TEST_SG_SCHEMA_PL2 = "eHealth-participantId-qns";

    public static final String TEST_DOC_SCHEMA_1 = "busdox-docid-qns";
    public static final String TEST_DOC_SCHEMA_2 = "ehealth-resid-qns";
    public static final String TEST_DOC_SCHEMA_PL2 = "eHealth-resId-qns";

    public static final String TEST_DOC_ID_1 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-12::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    public static final String TEST_DOC_ID_2 = "docid.007";
    public static final String TEST_DOC_ID_PL2 = "DocId.007";


    public static final String TOKEN_PREFIX = "token-";
    public static final String USERNAME_1 = "test-user_001";
    public static final String USERNAME_1_PASSWORD = "test-user_001";
    public static final String USERNAME_2 = "test-user_002";
    public static final String USERNAME_3 = "test-user_003";
    public static final String USERNAME_3_AT = "test-user_003-access-token";
    public static final String USERNAME_3_AT_PASSWORD = "test-user_003";
    public static final String USERNAME_4 = "test-user_004";
    public static final String USERNAME_5 = "test-user_005";
    public static final String USERNAME_TOKEN_1 = TOKEN_PREFIX + USERNAME_1;
    public static final String USERNAME_TOKEN_2 = TOKEN_PREFIX + USERNAME_2;
    public static final String USERNAME_TOKEN_3 = TOKEN_PREFIX + USERNAME_3;

    public static final String USER_CERT_1 = "CN=utest common name 01,O=org,C=BE:0000000000000066";
    public static final String USER_CERT_2 = "CN=utest common name 02,O=org,C=BE:0000000000000077";
    public static final String USER_CERT_3 = "CN=test example,O=European Commission,C=BE:0dd0d2f98cc25205bc6c854d1cd88411";


    public static final String ADMIN_USERNAME = "test_admin";
    public static final String CERT_USER = "CN=common name,O=org,C=BE:0000000000000066";
    public static final String CERT_USER_ENCODED = "CN%3Dcommon%20name%2CO%3Dorg%2CC%3DBE%3A0000000000000066";

    // parameter: custom string as content
    public static final String SIMPLE_EXTENSION_XML ="<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension: %s</ex:dummynode></Extension>";


}
