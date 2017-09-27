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

package eu.europa.ec.cipa.smp.server;

import com.helger.commons.scopes.mock.ScopeTestRule;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.*;
import eu.europa.ec.smp.api.Identifiers;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.rules.TestRule;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

/**
 * Created by rodrfla on 23/01/2017.
 */
public class AbstractTest {

    protected static IDataManager s_aDataMgr = null;

    final static String[][] usernames = new String[][]{{"CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", null},
            {"CN=SMP_1000000007,O=DG-DIGIT,C=BE", null},
            {"CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE", null},
            {"CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", null},
            {"CN=EHEALTH_SMP_EC/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:f71ee8b11cb3b787", null}};

    private static final class SMPTestRule extends ScopeTestRule {
        @Override
        public void before() {
            super.before();
            if (s_aDataMgr == null) {
                // Do it only once :)
                // SMPEntityManagerFactory.getInstance ();
                //s_aDataMgr = DataManagerFactory.getInstance();
            }
        }
    }

    @ClassRule
    public static TestRule s_aTestRule = new SMPTestRule();

    @Before
    public void before() throws Throwable {
        createDBCertificated();
        createServiceGroup();
        createOwnerShip();
    }

    private static void createServiceGroup() throws Exception {
        String serviceGroupId = "ehealth-actorid-qns::urn:australia:ncpb";
        final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(serviceGroupId);

        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroupID);
        DBServiceGroup aDBServiceGroup = s_aDataMgr.getCurrentEntityManager().find(DBServiceGroup.class, aDBServiceGroupID);
        if (aDBServiceGroup == null) {
            aDBServiceGroup = new DBServiceGroup(aDBServiceGroupID);
            s_aDataMgr.getCurrentEntityManager().persist(aDBServiceGroup);
        }
    }

    private static void createOwnerShip() throws Exception {
        String serviceGroupId = "ehealth-actorid-qns::urn:australia:ncpb";
        String username = "CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:48b681ee8e0dcc08";
        final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(serviceGroupId);
        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroupID);
        final DBOwnershipID dbOwnershipID = new DBOwnershipID(username, aServiceGroupID);
        DBOwnership dbOwnership = s_aDataMgr.getCurrentEntityManager().find(DBOwnership.class, dbOwnershipID);
        if (dbOwnership == null) {
            DBUser dbUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, username);
            if (dbUser != null) {
                final DBOwnershipID aDBOwnershipID = new DBOwnershipID(dbUser.getUsername(), aServiceGroupID);
                dbOwnership = new DBOwnership(aDBOwnershipID, dbUser, new DBServiceGroup(aDBServiceGroupID));
                s_aDataMgr.getCurrentEntityManager().persist(dbOwnership);
            }
        }
    }

    private static void createDBCertificated() throws Throwable {
        for (int i = 0; i < usernames.length; i++) {
            DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, usernames[i][0]);
            if (aDBUser == null) {
                aDBUser = new DBUser();
                aDBUser.setUsername(usernames[i][0]);
                aDBUser.setPassword(usernames[i][1]);
                s_aDataMgr.getCurrentEntityManager().persist(aDBUser);
            } else {
                if (aDBUser.getPassword() != null && !aDBUser.getPassword().equals(usernames[i][1])) {
                    aDBUser.setPassword(usernames[i][1]);
                    s_aDataMgr.getCurrentEntityManager().merge(aDBUser);
                }
            }
        }
    }

    @After
    public final void after() throws Throwable {
        removeDBUser();
    }

    private static void removeDBUser() throws Throwable {
        for (int i = 0; i < usernames.length; i++) {
            DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, usernames[i][0]);
            if (aDBUser != null) {
                s_aDataMgr.getCurrentEntityManager().remove(aDBUser);
            }
        }
    }

}
