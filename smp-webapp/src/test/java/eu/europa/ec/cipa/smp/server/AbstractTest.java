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
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import org.junit.ClassRule;
import org.junit.rules.TestRule;

/**
 * Created by rodrfla on 23/01/2017.
 */
//TODO: Gutowski - to be removed once migrated to Spring
@Deprecated
public class AbstractTest {

    protected static IDataManager s_aDataMgr = null;

    private static final class SMPTestRule extends ScopeTestRule {
        @Override
        public void before() {
            super.before();
            if (s_aDataMgr == null) {
                // Do it only once :)
                // SMPEntityManagerFactory.getInstance ();
                s_aDataMgr = DataManagerFactory.getInstance();
            }
        }
    }

    @ClassRule
    public static TestRule s_aTestRule = new SMPTestRule();

}
