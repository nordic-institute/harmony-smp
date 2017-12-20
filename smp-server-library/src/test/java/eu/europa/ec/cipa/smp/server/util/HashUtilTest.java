/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util;

import org.junit.Assert;
import org.junit.Test;

public class HashUtilTest {

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
