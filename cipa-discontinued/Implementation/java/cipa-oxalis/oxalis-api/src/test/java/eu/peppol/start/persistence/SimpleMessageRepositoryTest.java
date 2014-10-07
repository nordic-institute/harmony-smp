package eu.peppol.start.persistence;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import eu.peppol.start.identifier.ChannelId;
import eu.peppol.start.identifier.MessageId;
import eu.peppol.start.identifier.ParticipantId;
import eu.peppol.start.identifier.PeppolMessageHeader;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 04.12.11 Time: 21:10
 */
public class SimpleMessageRepositoryTest {

  private PeppolMessageHeader peppolHeader;

  @BeforeTest (alwaysRun = true)
  public void createPeppolHeader () {
    peppolHeader = new PeppolMessageHeader ();
    peppolHeader.setRecipientId (new ParticipantId ("9908:976098897"));
    peppolHeader.setSenderId (new ParticipantId ("9908:123456789"));
  }

  @Test
  public void computeDirectoryNameForMessage () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    peppolHeader.setChannelId (new ChannelId ("CH2"));
    peppolHeader.setRecipientId (new ParticipantId ("9908:976098897"));
    peppolHeader.setSenderId (new ParticipantId ("9908:123456789"));

    final String tmpdir = "/tmpx";

    final File dirName = simpleMessageRepository.computeDirectoryNameForInboundMessage (tmpdir, peppolHeader);

    assertEquals (dirName, new File (tmpdir + "/9908_976098897/CH2/9908_123456789"), "Invalid directory name computed");
  }

  @Test
  public void computeDirectoryNameForMessageWithNoChannel () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    final String tmpdir = "/tmpx";

    createPeppolHeader (); // Weird stuff happening in TestNG, would have
                           // expected us to have a clean header using
                           // @BeforeTest
    final File dirName = simpleMessageRepository.computeDirectoryNameForInboundMessage (tmpdir, peppolHeader);
    assertEquals (dirName, new File (tmpdir + "/9908_976098897/9908_123456789"), "Invalid directory name computed");
  }

  @Test
  public void testPrepareMessageStore () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    final File root = File.listRoots ()[0];
    final File tmp = new File (root, "/tmp/X");
    try {
      tmp.mkdirs ();
      System.err.println (tmp.toString ());
      System.err.flush ();
      final PeppolMessageHeader peppolMessageHeader = new PeppolMessageHeader ();
      peppolMessageHeader.setMessageId (new MessageId ("uuid:c5aa916d-9a1e-4ae8-ba25-0709ec913acb"));
      peppolMessageHeader.setRecipientId (new ParticipantId ("9908:976098897"));
      peppolMessageHeader.setSenderId (new ParticipantId ("9908:123456789"));

      simpleMessageRepository.prepareMessageDirectory (tmp.toString (), peppolMessageHeader);
    }
    finally {
      tmp.delete ();
    }
  }
}
