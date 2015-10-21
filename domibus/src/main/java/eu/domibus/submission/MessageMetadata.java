/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.submission;

import eu.domibus.common.configuration.model.Action;
import eu.domibus.common.configuration.model.Service;

/**
 * Created by kochc01 on 22.06.2015.
 */
public class MessageMetadata {

    private final String messageId;
    private final Service service;
    private final Action action;
    private final MessageMetadata.Type type;

    public MessageMetadata(final String messageId, final Service service, final Action action, final MessageMetadata.Type type) {
        this.messageId = messageId;
        this.service = service;

        this.action = action;
        this.type = type;
    }

    public MessageMetadata.Type getType() {
        return this.type;
    }

    public Action getAction() {
        return this.action;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public Service getService() {
        return this.service;
    }

    public enum Type {
        INBOUND, ERROR;
    }
}
