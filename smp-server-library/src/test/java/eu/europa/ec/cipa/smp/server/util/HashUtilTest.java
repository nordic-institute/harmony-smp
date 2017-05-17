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

/*
 * (C) Copyright 2016 - European Commission | Dynamic Discovery Client
 *
 * https://ec.europa.eu/cefdigital/code/projects/EDELIVERY/repos/dynamic-discovery-client/browse
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     dynamic-discovery\License_LGPL-2.1.txt or https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Flávio W. R. Santos - CEF-EDELIVERY-SUPPORT@ec.europa.eu
 * @author Adrien Ferial - CEF-EDELIVERY-SUPPORT@ec.europa.eu
 *
 */
package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

public class HashUtilTest extends AbstractTest {

    @Test
    public void testNAPTRHash() throws Exception {
        String participantId = HashUtil.getSHA256HashBase32("urn:poland:ncpb");
        Assert.assertEquals("DALXFO3CDYE5ZSLF5WAVCYQ3XGERI6ONUBJU5WAH3T77THFWCGEQ", participantId);
    }

    @Test
    public void testCNAMEHash() throws Exception {
        String participantId = HashUtil.getMD5Hash("urn:poland:ncpb");
        Assert.assertEquals("b-adb4c6d3821d142c684b13ed269fad65", "b-" + participantId);
    }
}
