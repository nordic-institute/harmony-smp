package eu.domibus.ebms3.submit;

//import java.util.concurrent.*;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.module.PeriodicWorker;

/**
 * @author Hamid Ben Malek
 */
public class SubmitWorker extends PeriodicWorker //implements Runnable
{
    private static final Logger log = Logger.getLogger(SubmitWorker.class);

    public void init() {
        //Constants.executor.scheduleWithFixedDelay(this, 10, 15, TimeUnit.SECONDS);
    }

    protected void task() {
        FileBasedSubmitter.submitMsgFromFolders();
    }
  /*
  public void run()
  {
    //System.out.println("=========== SubmitWorker is running now...");
    //log.debug("=========== SubmitWorker is running now...");
    FileBasedSubmitter.submitMsgFromFolders();
  }
  */
}
