package org.xbill.DNS;

import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by feriaad on 05/06/2015.
 */
public class CustomSIG0 {
    public static void signMessage(Message message, KEYRecord key, PrivateKey privkey,
                SIGRecord previous, int validityMinutesBack) throws DNSSEC.DNSSECException {

        int validity = Options.intValue("sig0validity");
        if (validity < 0)
            validity = 300; // 5 minutes by default

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -validityMinutesBack);
        Date minutesBack = cal.getTime();

        long minutesBackBackLong = minutesBack.getTime();

        Date timeSigned = new Date(minutesBackBackLong);
        Date timeExpires = new Date(minutesBackBackLong + validity * 1000);

        SIGRecord sig = DNSSEC.signMessage(message, previous, key, privkey,
                timeSigned, timeExpires);

        message.addRecord(sig, Section.ADDITIONAL);
    }
}
