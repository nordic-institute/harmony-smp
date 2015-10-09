package eu.domibus.ebms3.config;

import eu.domibus.ebms3.config.util.EmptyStringConverter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import java.io.Serializable;

/**
 * This class represents the PayloadService element used for configuration of AS4 compression.
 * This element can be either absent, empty or equal to application/gzip.
 * (http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/profiles/AS4-profile/v1.0/os/AS4-profile-v1.0-os.html)
 * <ul>
 *     <li>PayloadService element absent: compression disabled</li>
 *     <li>CompressionType element empty: compression disabled</li>
 *     <li>CompressionType element equal to application/gzip: compression enabled</li>
 * </ul>
 *
 * @author muell16
 */
@Root(name = "PayloadService", strict = false)
public class PayloadService implements Serializable {
    private static final long serialVersionUID = 3923744798143120404L;

    /**
     * In case this element is equal to application/gzip compression is enabled for this pmode.
     * If it is empty compression is disabled, otherwise an error should occur
     */
    @Element(name = "CompressionType", required = true)
    @Convert(EmptyStringConverter.class)
    private String compressionType;

    /**
     *
     * @return the content of compressionType
     */
    public String getCompressionType() {
        return compressionType;
    }

    /**
     *
     * @param compressionType compressionType to set
     */
    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }
}
