package eu.domibus.ebms3.consumers.impl;

//import org.springframework.jms.core.MessageCreator;

import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Hamid Ben Malek
 */
public class MsgCreator // implements MessageCreator
{

    private static final Logger log = Logger.getLogger(MsgCreator.class);

    protected final MessageContext msgCtx;
    protected boolean saveMessageToDisk = true;

    public MsgCreator(final MessageContext ctx) {
        this.msgCtx = ctx;
    }

    public MsgCreator(final MessageContext ctx, final boolean saveMsg) {
        this.msgCtx = ctx;
        this.saveMessageToDisk = saveMsg;
    }

    public Message createMessage(final Session session) {
        final byte[] contents;
        try {
            if (saveMessageToDisk) {
                final String fileName = getSaveLocation() + File.separator +
                                        System.currentTimeMillis() + ".mime";
                WSUtil.writeMessage(msgCtx, new File(fileName));
                contents = getBytesFromFile(new File(fileName));
            } else {
                contents = WSUtil.getMessageBytes(msgCtx);
            }
            final BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(contents);
            return msg;
        } catch (JMSException ex) {
            log.error("Error while processing JMS Message", ex);
        } catch (IOException e) {
            log.error("I/o exception occured while writing message to disk", e);
        }
        return null;
    }

    private static byte[] getBytesFromFile(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        final long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large: " + file.getName());
        }

        final byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    private String getSaveLocation() {
        final String receivedMsgsFolder = eu.domibus.ebms3.module.Constants.getReceivedFolder();
        final String path = receivedMsgsFolder + File.separator + "Messages_mq";
        new File(path).mkdirs();
        return path;
    }
}