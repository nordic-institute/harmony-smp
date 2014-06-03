package eu.domibus.ebms3.submit;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.persistent.*;

import java.io.File;

/**
 * @author Hamid Ben Malek
 */
public class FileBasedSubmitter {
    //  private static Log log = LogFactory.getLog(FileBasedSubmitter.class.getName());
    private static final Logger log = Logger.getLogger(FileBasedSubmitter.class.getName());
    private static final UserMsgToPushDAO umtpd = new UserMsgToPushDAO();
    private static final UserMsgToPullDAO umtpld = new UserMsgToPullDAO();
    private static final SyncResponseDAO srd = new SyncResponseDAO();

    public static synchronized void submitMsgFromFolders() {
        final String submitFolder = Constants.getSubmitFolder();
        //log.debug("Submit_Messages folder is: " + submitFolder);
        if (submitFolder == null) {
            return;
        }
        final File submitF = new File(submitFolder);
        final File[] directories = submitF.listFiles();
        if (directories == null || directories.length == 0) {
            log.debug("did not find any in message folders ...");
            return;
        }
        //log.debug("Found " + directories.length + " message folders");
        for (final File dir : directories) {
            if (dir.isDirectory()) {
                submitFromFolder(dir);
            }
        }
    }

    public static synchronized void submitFromFolder(final File folder) {
        if (folder == null || !folder.exists()) {
            return;
        }
        MsgInfoSet mis = null;
        try {
            mis = readMeta(folder);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            return;
        }
        if (mis == null) {
            //log.debug("No metadata.xml found in message folder " + folder.getName());
            return;
        }
        final String bodyPayload = mis.getBodyPayload();

        log.debug("[ SubmitWorker ] is scanning message folder " + folder.getName());
        log.debug("body payload is " + bodyPayload);
        final File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.debug("returning because no files found");
            return;
        }
        final int type = SubmitUtil.msgCategory(mis);
        if (type == SubmitUtil.TO_BE_PUSHED) {
            final UserMsgToPush msg = new UserMsgToPush(folder, mis);
            msg.setPushed(false);
            umtpd.persist(msg);
            log.debug("UserMsgToPush was submitted to database");
        } else if (type == SubmitUtil.TO_BE_PULLED) {
            final UserMsgToPull msg = new UserMsgToPull(folder, mis);
            umtpld.persist(msg);
            log.debug("A UserMsgToPull message was submitted (i.e: saved to DB)");
        } else if (type == SubmitUtil.TO_BE_SYNC_RESPONSE) {
            final SyncResponse msg = new SyncResponse(folder, mis);
            srd.persist(msg);
            log.debug("A SyncResponse message was submitted (i.e: saved to DB)");
        }
    }

    private static boolean renameMetadata(final File folder) {
        if (folder == null) {
            return false;
        }
        final File meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml");
        final File metaRenamed = new File(folder.getAbsolutePath() + File.separator +
                                          "metadata.xml.processed");
        return meta.renameTo(metaRenamed);
    }

    private static synchronized MsgInfoSet readMeta(final File folder) {
        if (folder == null || !folder.exists()) {
            return null;
        }
        File meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml");
        if (!meta.exists()) {
            return null;
        }
        renameMetadata(folder);
        meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml.processed");

        final MsgInfoSet mis = MsgInfoSet.read(meta);
        mis.setLegNumber(SubmitUtil.getLegNumber(mis));
        return mis;
    }
}