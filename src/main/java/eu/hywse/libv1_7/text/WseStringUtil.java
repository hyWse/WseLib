package eu.hywse.libv1_7.text;

import lombok.AllArgsConstructor;
import lombok.Data;

public class WseStringUtil {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isBoolean(String s) {
        if (s == null) {
            return false;
        }

        s = s.toLowerCase().trim();
        switch (s) {
            case "y":
            case "j":
            case "yes":
            case "yep":
            case "1":
            case "true":

            case "n":
            case "no":
            case "nop":
            case "0":
            case "false":

                return true;
        }

        return false;
    }

    public static boolean getBoolean(String s, boolean def) {
        if (!isBoolean(s)) return def;

        s = s.toLowerCase().trim();
        switch (s) {
            case "y":
            case "j":
            case "yes":
            case "yep":
            case "1":
            case "true":
                return true;

            case "n":
            case "no":
            case "nop":
            case "0":
            case "false":
                return false;

            default:
                return false;
        }
    }

    public static int getInteger(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static double getDouble(String s, double def) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     *
     * @param s1 Input 1
     * @param s2 Input 2
     *
     * @return Similarity of strings
     */
    public static double getSimilarity(String s1, String s2) {
        LenStr lenStr = LenStr.get(s1, s2);
        String longer = lenStr.getLonger(), shorter = lenStr.getShorter();

        int longerLength = longer.length();
        if (longerLength == 0) {
            // both strings are zero length
            return 1.0;
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static int getLengthDifference(String s1, String s2) {
        LenStr lenStr = LenStr.get(s1, s2);
        return lenStr.getLonger().length() - lenStr.getShorter().length();
    }

    public static String repeat(String input, int times) {
        String res = "";
        for (int i = 0; i < times; i++) res += input;
        return res;
    }

    @Data
    @AllArgsConstructor
    public static class LenStr {
        String shorter;
        String longer;

        public static LenStr get(String s1, String s2) {
            String longer = s1, shorter = s2;
            if (s1.length() < s2.length()) { // longer should always have greater length
                longer = s2;
                shorter = s1;
            }
            return new LenStr(shorter, longer);
        }
    }

}
