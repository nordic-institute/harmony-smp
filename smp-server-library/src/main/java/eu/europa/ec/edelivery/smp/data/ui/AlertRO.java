/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class AlertRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630003L;

    // session id
    private String sid;
    private String username;
    private String mailTo;
    private OffsetDateTime processedTime;
    private AlertTypeEnum alertType;
    private OffsetDateTime reportingTime;
    private AlertStatusEnum alertStatus;
    private String alertStatusDesc;
    private AlertLevelEnum alertLevel;
    private final Map<String, String> alertDetails = new HashMap<>();

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getAlertStatusDesc() {
        return alertStatusDesc;
    }

    public void setAlertStatusDesc(String alertStatusDesc) {
        this.alertStatusDesc = alertStatusDesc;
    }

    public OffsetDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(OffsetDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public AlertTypeEnum getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertTypeEnum alertType) {
        this.alertType = alertType;
    }

    public OffsetDateTime getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(OffsetDateTime reportingTime) {
        this.reportingTime = reportingTime;
    }

    public AlertStatusEnum getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(AlertStatusEnum alertStatus) {
        this.alertStatus = alertStatus;
    }

    public AlertLevelEnum getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(AlertLevelEnum alertLevel) {
        this.alertLevel = alertLevel;
    }

    public Map<String, String> getAlertDetails() {
        return alertDetails;
    }
}
