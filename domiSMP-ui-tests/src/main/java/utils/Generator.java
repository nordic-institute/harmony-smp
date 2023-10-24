package utils;

public class Generator {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALPHABETICAL_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public static String randomAlphaNumericValue(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String randomAlphabeticalValue(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHABETICAL_STRING.length());
            builder.append(ALPHABETICAL_STRING.charAt(character));
        }
        return builder.toString();
    }


}

