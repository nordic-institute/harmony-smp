/**
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
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * <p>
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.busdox.types;

import com.helger.commons.annotations.PresentForCodeCoverage;
import eu.europa.ec.cipa.busdox.util.CommonUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class is used for converting between XML time elements and Java Date
 * objects.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DateAdapter {
    /**
     * The time zone used in the adapter
     */

    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

    @SuppressWarnings("unused")
    @PresentForCodeCoverage
    private static final DateAdapter s_aInstance = new DateAdapter();

    private DateAdapter() {
    }

    @Nonnull
    public static Date parseDate(final String sDate) {
        String timezonedDate = CommonUtil.addDefaultTimezoneIfNotPresent(sDate);
        final Calendar aCal = DatatypeConverter.parseDate(timezonedDate);
        final Date ret = aCal.getTime();
        return ret;
    }

    @Nonnull
    public static String printDate(@Nonnull final Date aDate) {
        final Calendar aCal = new GregorianCalendar(TIMEZONE_UTC);
        aCal.setTime(aDate);
        final String ret = DatatypeConverter.printDate(aCal);
        return ret;
    }

    @Nonnull
    public static Date parseDateTime(final String sDateTime) {
        String timezonedDate = CommonUtil.addDefaultTimezoneIfNotPresent(sDateTime);
        final Calendar aCal = DatatypeConverter.parseDateTime(timezonedDate);
        final Date ret = aCal.getTime();
        return ret;
    }

    @Nonnull
    public static String printDateTime(@Nonnull final Date aDateTime) {
        final Calendar aCal = new GregorianCalendar(TIMEZONE_UTC);
        aCal.setTime(aDateTime);
        final String ret = DatatypeConverter.printDateTime(aCal);
        return ret;
    }
}
