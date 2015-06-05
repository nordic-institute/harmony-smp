package org.xbill.DNS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by feriaad on 05/06/2015.
 */
public class CustomSIG0 {

    static final Logger s_aLogger = LoggerFactory.getLogger(CustomSIG0.class);

    private static final int MAX_RETRY = 5;

    public static void signMessage(Message message, KEYRecord key, PrivateKey privkey,
                                   SIGRecord previous, int validityMinutesBack) throws Exception {
        signMessage(message, key, privkey, previous, validityMinutesBack, 0);
    }

    private static void signMessage(Message message, KEYRecord key, PrivateKey privkey,
                                    SIGRecord previous, int validityMinutesBack, int retry) throws Exception {
        Exception exception = null;
        if (retry < MAX_RETRY) {
            try {
                int validity = Options.intValue("sig0validity");

                if (validity < 0)
                    validity = 300; // 5 minutes by default

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, -validityMinutesBack);
                Date minutesBack = cal.getTime();

                long minutesBackBackLong = minutesBack.getTime();

                Date timeSigned = new Date(minutesBackBackLong);
                Date timeExpires = new Date(minutesBackBackLong + validity * 1000);

                SIGRecord sig = null;
                sig = DNSSEC.signMessage(message, previous, key, privkey,
                        timeSigned, timeExpires);
                message.addRecord(sig, Section.ADDITIONAL);
            } catch (final Exception exc) {
                s_aLogger.warn("There was an error when trying to sign the message, trying again. " + retry + " times. Exception was: " + exc.getMessage());
                signMessage(message, key, privkey, previous, validityMinutesBack, retry + 1);
                exception = exc;
            }
        } else {
            throw exception;
        }
    }
}
