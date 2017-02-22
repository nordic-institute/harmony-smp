package eu.europa.ec.cipa.smp.server.security;

import org.junit.After;
import org.junit.Before;
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
    private PrintStream initialPrintStream;

    @Before
    public void setUpStreams() {
        initialPrintStream = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(initialPrintStream);
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
