package eu.eCODEX.submission.transformer;

import backend.ecodex.org._1_1.SendRequest;
import eu.eCODEX.transport.dto.BackendMessageIn;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: muell16
 * Date: 25.11.13
 * Time: 07:19
 * To change this template use File | Settings | File Templates.
 */
@Ignore("not fully implemented")
@RunWith(MockitoJUnitRunner.class)
public class BackendInterfaceMessageTransformerTest {

    BackendMessageIn backendMessage;

    @Mock
    SendRequest request;

    @Mock
    MessagingE messageEnvelope;

    @Before
    public void setUp() {
        this.backendMessage = new BackendMessageIn(this.messageEnvelope, this.request);
    }

    @After
    public void tearDown() {
        this.request = null;
        this.messageEnvelope = null;
        this.backendMessage = null;
    }


    @Test
    public void testTransformToEbMessage() throws Exception {
        ByteArrayDataSource rawData =
                new ByteArrayDataSource(getClass().getResourceAsStream("/bodyload.xml"), "application/octet-string");
        DataHandler bodyloadData = new DataHandler(rawData);

        when(this.request.getBodyload().getBase64Binary()).thenReturn(bodyloadData);

        this.backendMessage.getSendRequest().getBodyload().getBase64Binary();


    }
}
