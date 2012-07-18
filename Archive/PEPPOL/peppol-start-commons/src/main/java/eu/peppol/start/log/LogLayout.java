/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.log;

import java.text.DateFormat;
import java.util.Date;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;

/**
 * Custom log4j Layout.
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class LogLayout extends XMLLayout{

    private  final int DEFAULT_SIZE = 256;
    private final int UPPER_LIMIT = 2048;

    private StringBuffer buf = new StringBuffer(DEFAULT_SIZE);

    /**Set up the format for XML file.
     *@param event
     *          Represents log event.
     *@return  String containing XML log data
     */
    @Override
    public final String format(final LoggingEvent event) {

        if (buf.capacity() > UPPER_LIMIT) {
            buf = new StringBuffer(DEFAULT_SIZE);
        } else {
            buf.setLength(0);
        }

     // We yield to the \r\n heresy.
     buf.append("<event>\n");
     buf.append("\t<priority>");
     buf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
     buf.append("</priority>\n");
     buf.append("\t<message>");
     // Append the rendered message. Also make sure to escape any
     // existing CDATA sections.
     Transform.appendEscapingCDATA(buf, event.getRenderedMessage());
     buf.append("</message>\n");
     buf.append("\t<class>");
     buf.append(Transform.escapeTags(event.getLoggerName()));
     buf.append("</class>\n");
     buf.append("\t<method>");
     buf.append(event.getLocationInformation().getMethodName());
     buf.append(": line ");
     buf.append(event.getLocationInformation().getLineNumber());
     buf.append("</method>\n");
     buf.append("\t<timestamp>");
     buf.append(event.timeStamp);
     buf.append("</timestamp>\n");

     String date =
             DateFormat.getDateTimeInstance(DateFormat.SHORT,
             DateFormat.SHORT).format(new Date(event.timeStamp));

     buf.append("\t<datetime>");
     buf.append(date);
     buf.append("</datetime>\n");
     buf.append("\t<thread>");
     buf.append(Transform.escapeTags(event.getThreadName()));
     buf.append("</thread>\n");
     String[] s = event.getThrowableStrRep();
     if (s != null) {
       buf.append("\t<throwable>");
       for (int i = 0; i < s.length; i++) {
           Transform.appendEscapingCDATA(buf, s[i]);
           buf.append("\r\n");
       }
       buf.append("\t</throwable>\r\n");
     }

     buf.append("</event>\r\n");

     return buf.toString();
    }
}