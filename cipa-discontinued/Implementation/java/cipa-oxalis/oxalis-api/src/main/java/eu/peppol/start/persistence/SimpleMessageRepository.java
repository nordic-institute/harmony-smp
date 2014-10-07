package eu.peppol.start.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import eu.peppol.start.identifier.IdentifierName;
import eu.peppol.start.identifier.PeppolMessageHeader;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 28.11.11
 *         Time: 21:09
 */
public class SimpleMessageRepository implements MessageRepository {

  private static final Logger log = LoggerFactory.getLogger (SimpleMessageRepository.class);

  public void saveInboundMessage (final String inboundMessageStore,
                                  final PeppolMessageHeader peppolMessageHeader,
                                  final Document document) {
    log.info ("Default message handler " + peppolMessageHeader);

    final File messageDirectory = prepareMessageDirectory (inboundMessageStore, peppolMessageHeader);

    try {
      final String messageFileName = peppolMessageHeader.getMessageId ().stringValue ().replace (":", "_") + ".xml";
      final File messageFullPath = new File (messageDirectory, messageFileName);
      saveDocument (document, messageFullPath);

      final String headerFileName = peppolMessageHeader.getMessageId ().stringValue ().replace (":", "_") + ".txt";
      final File messageHeaderFilePath = new File (messageDirectory, headerFileName);
      saveHeader (peppolMessageHeader, messageHeaderFilePath, messageFullPath);

    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to persist message " + peppolMessageHeader.getMessageId (), e);
    }

  }

  File prepareMessageDirectory (final String inboundMessageStore, final PeppolMessageHeader peppolMessageHeader) {
    // Computes the full path of the directory in which message and routing data
    // should be stored.
    final File messageDirectory = computeDirectoryNameForInboundMessage (inboundMessageStore, peppolMessageHeader);
    if (!messageDirectory.exists ()) {
      if (!messageDirectory.mkdirs ()) {
        throw new IllegalStateException ("Unable to create directory " + messageDirectory.toString ());
      }
    }

    if (!messageDirectory.isDirectory () || !messageDirectory.canWrite ()) {
      throw new IllegalStateException ("Directory " + messageDirectory + " does not exist, or there is no access");
    }
    return messageDirectory;
  }

  void saveHeader (final PeppolMessageHeader peppolMessageHeader,
                   final File messageHeaderFilerPath,
                   final File messageFullPath) {
    try {
      final FileOutputStream fos = new FileOutputStream (messageHeaderFilerPath);
      final PrintWriter pw = new PrintWriter (new OutputStreamWriter (fos, "UTF-8"));
      final Date date = new Date ();

      // Formats the current time and date according to the ISO8601 standard.
      pw.append ("TimeStamp=").format ("%tFT%tT%tz\n", date, date, date);

      pw.append ("MessageFileName=").append (messageFullPath.toString ()).append ('\n');
      pw.append (IdentifierName.MESSAGE_ID.stringValue ())
        .append ("=")
        .append (peppolMessageHeader.getMessageId ().stringValue ())
        .append ('\n');
      pw.append (IdentifierName.CHANNEL_ID.stringValue ())
        .append ("=")
        .append (peppolMessageHeader.getChannelId ().stringValue ())
        .append ('\n');
      pw.append (IdentifierName.RECIPIENT_ID.stringValue ())
        .append ('=')
        .append (peppolMessageHeader.getRecipientId ().stringValue ())
        .append ('\n');
      pw.append (IdentifierName.SENDER_ID.stringValue ())
        .append ('=')
        .append (peppolMessageHeader.getSenderId ().stringValue ())
        .append ('\n');
      pw.append (IdentifierName.DOCUMENT_ID.stringValue ())
        .append ('=')
        .append (peppolMessageHeader.getDocumentTypeIdentifier ().toString ())
        .append ('\n');
      pw.append (IdentifierName.PROCESS_ID.stringValue ())
        .append ('=')
        .append (peppolMessageHeader.getPeppolProcessTypeId ().toString ())
        .append ('\n');
      pw.close ();
      log.debug ("File " + messageHeaderFilerPath + " written");

    }
    catch (final FileNotFoundException e) {
      throw new IllegalStateException ("Unable to create file " + messageHeaderFilerPath + "; " + e, e);
    }
    catch (final UnsupportedEncodingException e) {
      throw new IllegalStateException ("Unable to create writer for " + messageHeaderFilerPath + "; " + e, e);
    }
  }

  /**
   * Transforms an XML document into a String
   * 
   * @param document
   *        the XML document to be transformed
   */
  void saveDocument (final Document document, final File outputFile) {

    try {
      final FileOutputStream fos = new FileOutputStream (outputFile);
      final Writer writer = new BufferedWriter (new OutputStreamWriter (fos, "UTF-8"));

      final StreamResult result = new StreamResult (writer);

      final TransformerFactory tf = TransformerFactory.newInstance ();
      Transformer transformer;
      transformer = tf.newTransformer ();
      transformer.transform (new DOMSource (document), result);
      fos.close ();
      log.debug ("File " + outputFile + " written");
    }
    catch (final Exception e) {
      throw new SimpleMessageRepositoryException (outputFile, e);
    }

  }

  @Override
  public String toString () {
    return SimpleMessageRepository.class.getSimpleName ();
  }

  /**
   * Computes the directory name for inbound messages.
   * 
   * <pre>
   *     /basedir/{recipientId}/{channelId}/{senderId}
   * </pre>
   * 
   * @param inboundMessageStore
   * @param peppolMessageHeader
   * @return
   */
  File computeDirectoryNameForInboundMessage (final String inboundMessageStore,
                                              final PeppolMessageHeader peppolMessageHeader) {
    if (peppolMessageHeader == null) {
      throw new IllegalArgumentException ("peppolMessageHeader required");
    }

    final String path = String.format ("%s/%s/%s",
                                       peppolMessageHeader.getRecipientId ().stringValue ().replace (":", "_"),
                                       peppolMessageHeader.getChannelId ().stringValue (),
                                       peppolMessageHeader.getSenderId ().stringValue ().replace (":", "_"));
    return new File (inboundMessageStore, path);
  }

  /**
   * Computes the directory
   * 
   * @param outboundMessageStore
   * @param peppolMessageHeader
   * @return File
   */
  File computeDirectoryNameForOutboundMessages (final String outboundMessageStore,
                                                final PeppolMessageHeader peppolMessageHeader) {
    if (peppolMessageHeader == null) {
      throw new IllegalArgumentException ("peppolMessageHeader required");
    }

    final String path = String.format ("%s/%s/%s",
                                       peppolMessageHeader.getSenderId ().stringValue ().replace (":", "_"),
                                       peppolMessageHeader.getChannelId ().stringValue (),
                                       peppolMessageHeader.getRecipientId ().stringValue ().replace (":", "_"));
    return new File (outboundMessageStore, path);
  }
}
