/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axis2.transport.http;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;


/**
 * Extends {@link org.apache.axis2.webapp.AxisAdminServlet} for compatibility with Web applications
 * and tools written for Axis2 versions prior to 1.5.
 */
public class AxisAdminServlet extends org.apache.axis2.webapp.AxisAdminServlet {
    private static final long serialVersionUID = 3038257566847798292L;

    private static final Logger log = Logger.getLogger(AxisAdminServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        log.warn("Web application uses " + AxisAdminServlet.class.getName() +
                 "; please update web.xml to use " +
                 org.apache.axis2.webapp.AxisAdminServlet.class.getName() + " instead");
    }
}
