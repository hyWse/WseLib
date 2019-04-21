package eu.hywse.lib.misc;

import java.util.HashMap;

/**
 * @author hyWse
 * @version ${VERSION}
 */

public class WseMap<K, V> extends HashMap<K, V> {

    // + ---------------------------------------------------------------------------------------------------- +
    // | ==================================================================================================== |
    // + ---------------------------------------------------------------------------------------------------- +

    /**
     * Send the keys and values from the map in a fancy format
     * @param maxKey Max-Key-Length
     * @param maxVal Max-Value-Length
     */
    public void printf(int maxKey, int maxVal) {

        int longestVal = 1;
        int longestKey = 1;

        /* Longest Key / Val */
        for (Entry<K, V> entry : super.entrySet()) {
            int valLen = entry.getValue().toString().length();
            int keyLen = entry.getKey().toString().length();

            if ((maxKey >= keyLen || maxKey == -1) && keyLen > longestKey) {
                longestKey = keyLen;
            }

            if ((maxVal >= valLen || maxVal == -1) && valLen > longestVal) {
                longestVal = valLen;
            }
        }

        /* Lines - Key */
        StringBuilder keyLine = new StringBuilder();
        for (int i = 0; i < longestKey; i++) {
            keyLine.append("-");
        }

        /* Lines - Val */
        StringBuilder valLine = new StringBuilder();
        for (int i = 0; i < longestVal; i++) {
            valLine.append("-");
        }

        sout("Printing Table.. LKey: " + longestKey + "; LVal: " + longestVal + ";");

        sout("+ " + keyLine + " + " + valLine + " +");
        sout(String.format("| %" + longestKey + "s | %-" + longestVal + "s |", "Key", "Value"));
        sout("+ " + keyLine + " + " + valLine + " +");

        for (Entry<K, V> entry : super.entrySet()) {
            /* Key */
            String key = entry.getKey().toString();
            if (key.length() > longestKey) key = key.substring(0, longestKey - 1) + "$";

            /* Value */
            String val = entry.getValue().toString();
            if (val.length() > longestVal) val = val.substring(0, longestVal - 1) + "$";

            sout(String.format("| %" + longestKey + "s | %-" + longestVal + "s |", key, val));
        }

        sout("+ " + keyLine + " + " + valLine + " +");
    }

    /**
     * end the keys and values from the map in a fancy format
     * = Unlimited length
     */
    public void printf() {
        printf(-1, -1);
    }


    private void sout(String message) {
        System.out.println("[WseMap]: " + message);
    }

    // + ---------------------------------------------------------------------------------------------------- +
    // | ==================================================================================================== |
    // + ---------------------------------------------------------------------------------------------------- +

    /**
     * Alias to HashMap#put()
     *
     * @param key       Object | Key
     * @param value     Object | Value
     */
    public void add(K key, V value) {
        put(key, value);
    }

    /**
     * Returns the index / key of a value
     *
     * @param value Value
     * @return Object | NULL if not found
     */
    public Object getKey(Object value) {
        for(Entry<K, V> entry : entrySet()) {
            if(entry.getValue().equals(value)) return entry.getKey();
        }

        return null;
    }

}
