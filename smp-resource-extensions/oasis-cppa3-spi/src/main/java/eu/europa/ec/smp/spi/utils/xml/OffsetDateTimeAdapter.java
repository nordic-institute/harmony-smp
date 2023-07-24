
/**
 * Purpose of the class it to provide  OffsetDateTime to string and string to OffsetDateTime conversion
 *
 * @author Joze Rihtarsic
 * @since 2.0
 */

package eu.europa.ec.smp.spi.utils.xml;

import eu.europa.ec.smp.spi.utils.DatatypeConverter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.OffsetDateTime;

public class OffsetDateTimeAdapter
    extends XmlAdapter<String, OffsetDateTime>
{
    public OffsetDateTime unmarshal(String value) {
        return (DatatypeConverter.parseDateTime(value));
    }

    public String marshal(OffsetDateTime value) {
        return (DatatypeConverter.printDateTime(value));
    }
}
