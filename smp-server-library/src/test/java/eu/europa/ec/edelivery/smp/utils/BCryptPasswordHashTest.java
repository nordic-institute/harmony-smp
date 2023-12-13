/*-
 * #START_LICENSE#
 * smp-webapp
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

package eu.europa.ec.edelivery.smp.utils;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 22/02/2017.
 */
public class BCryptPasswordHashTest {

    private static final String PASSWORD = "this_is_sample_password";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream initialPrintStream=null;


    @After
    public void cleanUpStreams() {
        if (initialPrintStream!=null){
            System.setOut(initialPrintStream);
        }
    }

    @Test
    public void generatedHashIsValidTest() {
        //when
        String hash = BCryptPasswordHash.hashPassword(PASSWORD);
        //then
        assertTrue(BCrypt.checkpw(PASSWORD, hash));
    }

    @Test
    public void generatedHashIsAlwaysSaltedTest() {
        //when
        String hash1 = BCryptPasswordHash.hashPassword(PASSWORD);
        String hash2 = BCryptPasswordHash.hashPassword(PASSWORD);

        //then
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void mainMethodSupportsMultiplePasswordsAndPrintsThemToStandardOutputTest() {
        //given
        initialPrintStream = System.out;
        System.setOut(new PrintStream(outContent));
        String[] passwords = new String[]{PASSWORD + 1, PASSWORD + 2, PASSWORD + 3};

        //when
        BCryptPasswordHash.main(passwords);

        //then
        String[] hashes = outContent.toString().replaceAll("\r", "").split("\n");
        assertEquals(passwords.length, hashes.length);
        for (int i = 0; i < passwords.length; i++) {
            assertTrue(BCrypt.checkpw(passwords[i], hashes[i]));
        }
    }
}
