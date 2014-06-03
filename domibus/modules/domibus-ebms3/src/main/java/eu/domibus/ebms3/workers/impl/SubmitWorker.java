package eu.domibus.ebms3.workers.impl;


import eu.domibus.ebms3.submit.FileBasedSubmitter;

/**
 * @author Hamid Ben Malek
 */
public class SubmitWorker implements Runnable {
    public void run() {
        FileBasedSubmitter.submitMsgFromFolders();
    }
}