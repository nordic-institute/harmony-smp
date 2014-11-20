package eu.domibus.ebms3.handlers;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.persistent.TempStore;
import eu.domibus.common.persistent.TempStoreDAO;
import eu.domibus.common.util.FileUtil;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PayloadService;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.PayloadServiceUtil;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.persistent.EbmsPayload;
import eu.domibus.ebms3.persistent.Payloads;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;


/**
 * @author Hamid Ben Malek
 */
public class Packager extends AbstractHandler {
    private static final Logger LOG = Logger.getLogger(Packager.class);

    private final TempStoreDAO tsd = new TempStoreDAO();

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if ((msgCtx.getFLOW() != MessageContext.OUT_FLOW) || msgCtx.isServerSide()) {
            return InvocationResponse.CONTINUE;
        }

        final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
        if (mis == null) {
            Packager.LOG.warn("No PMode was specified for the outgoing message");
            return InvocationResponse.CONTINUE;
        }

        //if compression is enabled add PartProperties
        PayloadService payloadService = PayloadServiceUtil.getPayloadService(mis.getPmode());
        if(payloadService != null) {
            //compression is enabled for this outgoing message

            Payloads payloads = mis.getPayloads();

            Attachments uncompressedAttachments = msgCtx.getAttachmentMap();

            //Setting PartProperties for compression on all Payloads but not on BodyLoad
            for(EbmsPayload p : payloads.getPayloads()) {
                p.addPartProperties(Constants.COMPRESSION_PROPERTY_NAME, payloadService.getCompressionType());
                if(p.getPartProperties(Constants.MIMETYPE_PROPERTY_NAME) == null) {
                    p.addPartProperties(Constants.MIMETYPE_PROPERTY_NAME, p.getContentType());
                }
                try {
                    String cid = p.getCid();
                    if(cid == null || cid.isEmpty()) {
                        throw new NullPointerException("cid of Payload not set. can not proceed");
                    }

                    DataHandler attachment = uncompressedAttachments.getDataHandler(cid);

                    byte[] uncompressedContent = IOUtils.toByteArray(attachment.getInputStream());

                    msgCtx.removeAttachment(cid);
                    byte[] compressedContent = FileUtil.doCompress(uncompressedContent);
                    ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(compressedContent, p.getContentType());
                    DataHandler compressedAttachment = new DataHandler(byteArrayDataSource);
                    msgCtx.addAttachment(cid, compressedAttachment);

                } catch (IOException e) {
                   Packager.LOG.error(e);
                    throw new ConfigurationException("Error during compression of payloads", e);
                }
            }
        }

        if (msgCtx.getEnvelope() == null) {
            final Leg leg = Configuration.getLeg(mis);
            double soapVersion = 1.1;
            final String ver = leg.getSoapVersion();
            if ((ver != null) && !"".equals(ver.trim())) {
                soapVersion = Double.parseDouble(ver);
            }
            msgCtx.setEnvelope(EbMessage.createEnvelope(soapVersion));
        }

        SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            final SOAPEnvelope env = msgCtx.getEnvelope();
            header = ((SOAPFactory) env.getOMFactory()).createSOAPHeader(env);
        }

        final OMElement ebMess = XMLUtil.getGrandChildNameNS(header, Constants.MESSAGING, Constants.NS);
        if (ebMess != null) {
            return InvocationResponse.CONTINUE;
        }

        if (Packager.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }
        //log.debug(logPrefix + msgCtx.getEnvelope().getHeader());
        XMLUtil.debug(Packager.LOG, this.logPrefix, msgCtx.getEnvelope().getHeader());

        final Messaging mess = PackagingFactory.createMessagingElement(msgCtx);
        //EbUtil.createMessagingElement(msgCtx);

        mess.addToHeader(msgCtx.getEnvelope());
        Packager.LOG.debug(this.logPrefix + " ebms3 headers were added to outgoing message");
        XMLUtil.debug(Packager.LOG, this.logPrefix, msgCtx.getEnvelope().getHeader());

        return InvocationResponse.CONTINUE;
    }
}