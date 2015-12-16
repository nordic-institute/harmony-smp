package eu.europa.ec.digit.domibus.common.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

public class Logger {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	private Log log = null;

	/* ---- Constructors ---- */

	@SuppressWarnings("rawtypes")
	public Logger(Class clazz) {
		this.log = LogFactory.getLog(clazz);
	}



	/* ---- Business Methods ---- */

    public void securityLog(LogEvent event, String...params) {
    	categoryLog(Severity.INFO, Category.SECURITY, event, null, params);
    }

    public void businessLog(LogEvent event, String...params) {
    	categoryLog(Severity.INFO, Category.BUSINESS, event, null, params);
    }

    public void businessLog(Severity severity, LogEvent event, String...params) {
    	categoryLog(severity, Category.BUSINESS, event, null, params);
    }

    public void businessLog(LogEvent event, Throwable t, String...params) {
    	categoryLog(Severity.ERROR, Category.BUSINESS, event, t, params);
    }

    public void info(String message) {
    	log.info(message);
    }

    public void debug(String message) {
    	log.debug(message);
    }

    public void warn(String message) {
    	log.warn(message);
    }

    public void warn(String message, Throwable throwable) {
    	log.warn(message, throwable);
    }

    public void error(String message, Throwable throwable) {
    	log.error(message, throwable);
    }

    public void putMDC(String key, String value) {
    	MDC.put(key, value);
    }

    private void categoryLog(Severity severity, Category category, LogEvent event, Throwable t, String... params) {

    	switch (severity) {
    		case WARN:
    			log.warn("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    		case INFO:
    			log.info("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    		case ERROR:
    			log.error("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    		case TRACE:
    			log.trace("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    		case FATAL:
    			log.fatal("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    		case DEBUG:
    			log.debug("[" + category.name() + " - " + event.getCode() + "] " + event.format(params));
    			break;
    	}
    }

    /* ---- Getters and Setters ---- */

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}
}
