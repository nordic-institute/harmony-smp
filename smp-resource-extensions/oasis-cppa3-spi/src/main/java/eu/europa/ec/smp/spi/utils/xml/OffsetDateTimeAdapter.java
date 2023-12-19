/*-
 * #START_LICENSE#
 * oasis-cppa3-spi
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
