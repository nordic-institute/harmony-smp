package eu.domibus.ebms3.module;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.log4j.Logger;
import eu.domibus.common.util.ClassUtil;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
public abstract class ControlledPeriodicWorker implements Runnable, Serializable {
    private static final long serialVersionUID = 7351331901508281649L;

    private static final Logger log = Logger.getLogger(ControlledPeriodicWorker.class);

    protected String pmode;
    protected String mpc;
    protected long interval = 30000;
    protected int maxMsg = -1;
    protected String callbackClass;
    protected int groupSize = 2;
    protected long pause = 0;
    protected String conditionClass;

    protected int totalMsgCount = 0;
    protected ConfigurationContext configCtx;

    protected Thread currentThread = null;
    protected boolean running = true;

    public ControlledPeriodicWorker() {
    }

    public ControlledPeriodicWorker(final String pmode, final String mpc, final long interval,
                                    final String callbackClass, final int groupSize, final long pause,
                                    final int maxMsg) {
        this.pmode = pmode;
        this.mpc = mpc;
        this.interval = interval;
        this.callbackClass = callbackClass;
        this.groupSize = groupSize;
        this.pause = pause;
        this.maxMsg = maxMsg;
    }

    public ControlledPeriodicWorker(final OMElement pullElement, final ConfigurationContext config) {
        this.configCtx = config;
        init(pullElement);
    }

    public void init(final OMElement pullElement) {
        if (pullElement == null) {
            return;
        }
        this.pmode = pullElement.getAttributeValue(new QName("pmode"));
        this.mpc = pullElement.getAttributeValue(new QName("mpc"));
        this.callbackClass = pullElement.getAttributeValue(new QName("callbackClass"));
        this.conditionClass = pullElement.getAttributeValue(new QName("conditionClass"));
        final String intrv = pullElement.getAttributeValue(new QName("interval"));
        if (intrv != null && !intrv.trim().equals("")) {
            this.interval = Long.parseLong(intrv);
        }
        final String ps = pullElement.getAttributeValue(new QName("pause"));
        if (ps != null && !ps.trim().equals("")) {
            this.pause = Long.parseLong(ps);
        }
        final String max = pullElement.getAttributeValue(new QName("maxMsg"));
        if (max != null && !max.trim().equals("")) {
            this.maxMsg = Integer.parseInt(max);
        }
        final String gz = pullElement.getAttributeValue(new QName("groupSize"));
        if (gz != null && !gz.trim().equals("")) {
            this.groupSize = Integer.parseInt(gz);
        }
    }

    protected Condition getCondition() {
        if (conditionClass == null || conditionClass.trim().equals("")) {
            return null;
        }
        return (Condition) ClassUtil.createInstance(conditionClass);
    }

    protected AxisCallback getCallback() {
        if (callbackClass == null || callbackClass.trim().equals("")) {
            return null;
        }
        return (AxisCallback) ClassUtil.createInstance(callbackClass);
    }

    public void start() {
        try {
            if (currentThread == null) {
                currentThread = new Thread(this);
            }
            running = true;
            currentThread.start();
        } catch (Exception e) {
            log.error("Error occured during thread execution", e);
        }
    }

    public void stop() {
        running = false;
    }

    public void terminate() {
        stop();
        currentThread = null;
    }

    public void run() {
        final Thread thisThread = Thread.currentThread();
        try {
            Thread.sleep(interval);
            while (thisThread == currentThread && currentThread != null && running) {
                if (totalMsgCount > 0 && totalMsgCount == maxMsg) {
                    return;
                }
                if (totalMsgCount > 0 && groupSize > 0 &&
                    (totalMsgCount % groupSize) == 0 && pause > 0) {
                    Thread.sleep(pause);
                }
                final Condition condition = getCondition();
                if (condition != null) {
                    final AxisCallback cb = condition.allowed(configCtx);
                    if (cb == null) {
                        continue;
                    }
                    task();
                    totalMsgCount++;
                } else {
                    task();
                    totalMsgCount++;
                }
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            log.error("Error occured in ControllerPeriodicWorker", e);
            run();
        }
    }

    protected abstract void task();
}