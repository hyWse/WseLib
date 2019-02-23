package eu.hywse.libv1_11.misc;

import java.util.HashMap;

/**
 * @author hyWse
 * @version ${VERSION}
 */

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WseMap extends HashMap<Object, Object> {

    private final String VERSION = "1.1";

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
        for (Entry<Object, Object> entry : super.entrySet()) {
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

        for (Entry<Object, Object> entry : super.entrySet()) {
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
        System.out.println("[WseMap " + VERSION + "]: " + message);
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
    public void add(Object key, Object value) {
        put(key, value);
    }

    /**
     * Adds a value with automatically generated key
     *
     * @param value Value
     */
    public void add(Object value) {
        int key = size();
        while(containsKey(key)) key ++;
        add(key, value);
    }

    /**
     * Returns the index / key of a value
     *
     * @param value Value
     * @return Object | NULL if not found
     */
    public Object getKey(Object value) {
        if(!containsValue(value)) return null;

        for(Entry<Object, Object> entry : entrySet()) {
            if(entry.getValue().equals(value)) return entry.getKey();
        }

        return null;
    }

    // + ---------------------------------------------------------------------------------------------------- +
    // | ==================================================================================================== |
    // + ---------------------------------------------------------------------------------------------------- +

    /*
     * Getters
     */

    public String getString(Object key, String def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return null;
        if (!(res instanceof String)) return res.toString();

        return (String) res;
    }
    public byte getByte(Object key, byte def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Byte)) return -1;

        return (byte) res;
    }
    public short getShort(Object key, short def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Short)) return -1;

        return (short) res;
    }
    public int getInt(Object key, int def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Integer)) return -1;

        return (int) res;
    }
    public long getLong(Object key, long def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Long)) return -1;

        return (long) res;
    }
    public float getFloat(Object key, float def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Float)) return -1;

        return (float) res;
    }
    public double getDouble(Object key, int def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return -1;
        if (!(res instanceof Double)) return -1;

        return (double) res;
    }
    public boolean getBoolean(Object key, boolean def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return false;
        if (!(res instanceof Boolean)) return false;

        return (boolean) res;
    }
    public char getChar(Object key, char def) {
        Object res = super.getOrDefault(key, def);
        if (res == null) return '\u0000';
        if (!(res instanceof Character)) return '\u0000';

        return (char) res;
    }
    public WseMap getMap(int key) {
        Object res = getOrDefault(key, new WseMap());

        if(!(res instanceof WseMap)) {
            res = new WseMap();
        }

        return (WseMap) res;
    }

}
