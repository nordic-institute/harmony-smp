package eu.domibus.logging.persistent;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * This Class represents a database table for standard logging events
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 * @date 07-13-2012
 */
@Entity
@Table(name = "TB_LOGGER_EVENT")

public class LoggerEvent extends AbstractBaseEntity {


    @Column(name = "LOG_DATE")
    protected Date LOGDate;

    @Column(name = "LOGGER")
    protected String Logger;

    @Column(name = "PRIORITY")
    protected String Priority;

    @Column(name = "LOG_CLASS_NAME")
    protected String Log_ClassName;

    @Column(name = "LOG_METHOD_NAME")
    protected String Log_MethodName;

    @Column(name = "LOG_LINE_NUMBER")
    protected String Log_LineNumber;

    @Column(name = "MSG", length = 2000)
    protected String Msg;

    public LoggerEvent() {
        LOGDate = new Date();
    }

    public LoggerEvent(final String logger, final String priority, final String log_ClassName, final String log_MethodName, final String log_LineNumber, final String msg) {
        super();
        LOGDate = new Date();
        Logger = logger;
        Priority = priority;
        Log_ClassName = log_ClassName;
        Log_MethodName = log_MethodName;
        Log_LineNumber = log_LineNumber;
        Msg = msg;
    }

    /**
     * @param event This constructor expects a LoggingEvent instance and creates an new instance of LoggerEvent
     */
    public LoggerEvent(final LoggingEvent event) {
        super();
        final LocationInfo locinfo = event.getLocationInformation();

        this.LOGDate = new Date(event.getTimeStamp());
        this.Logger = event.getLoggerName();
        this.Priority = event.getLevel().toString();
        Log_ClassName = locinfo.getClassName();
        Log_MethodName = locinfo.getMethodName();
        Log_LineNumber = locinfo.getLineNumber();
        Msg = event.getMessage() == null ? "null" : event.getMessage().toString();
    }


    public Date getLOGDate() {
        return LOGDate;
    }

    public void setLOGDate(final Date lOGDate) {
        LOGDate = lOGDate;
    }

    public String getLogger() {
        return Logger;
    }

    public void setLogger(final String logger) {
        Logger = logger;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(final String priority) {
        Priority = priority;
    }

    public String getLog_ClassName() {
        return Log_ClassName;
    }

    public void setLog_ClassName(final String log_ClassName) {
        Log_ClassName = log_ClassName;
    }

    public String getLog_MethodName() {
        return Log_MethodName;
    }

    public void setLog_MethodName(final String log_MethodName) {
        Log_MethodName = log_MethodName;
    }

    public String getLog_LineNumber() {
        return Log_LineNumber;
    }

    public void setLog_LineNumber(final String log_LineNumber) {
        Log_LineNumber = log_LineNumber;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(final String msg) {
        Msg = msg;
    }

    //TODO: remove this, as it is only used for a test and id should not be settable from external
    public void setId(final String id) {
        this.id = id;
    }


}
