/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.exceptions;

/**
 * Thrown when entity cannot be converted.
 * Means that user's input hasn't been validated properly or data in database are malformed.
 * In both cases most probably environment or source code issue occured (bug).
 * <p>
 * Created by gutowpa on 15/11/2017.
 */
public class ConversionException extends IllegalStateException {

    public ConversionException(Exception e){
        super(e);
    }
}
