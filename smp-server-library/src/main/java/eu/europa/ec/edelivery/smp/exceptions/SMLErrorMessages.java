/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.exceptions;

public class SMLErrorMessages {

    public static final String ERR_PARTICIPANT_ALREADY_EXISTS ="[ERR-106] The participant identifier '%s' with scheme: '%s' already exist";
    public static final String ERR_PARTICIPANT_NOT_EXISTS="[ERR-110] At least one of the participants doesn't exist in the list";
    public static final String ERR_DOMAIN_ALREADY_EXISTS="[ERR-106] The SMP '%s' already exists";
    public static final String ERR_DOMAIN_NOT_EXISTS="[ERR-100] The SMP '%s' doesn't exist";


}
