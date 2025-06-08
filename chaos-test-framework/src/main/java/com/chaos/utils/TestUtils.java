package com.chaos.utils;

import java.util.Random;

public class TestUtils {

    private static final Random RANDOM = new Random();

    // Generates a random integer within the specified range
    public static int generateRandomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    // Validates if the response is as expected
    public static boolean validateResponse(Object actual, Object expected) {
        return actual != null && actual.equals(expected);
    }

    // Generates a random string of specified length
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (RANDOM.nextInt(26) + 'a'));
        }
        return sb.toString();
    }
}