package eu.hywse.libv1_8.misc.delay;

import lombok.Getter;

import java.util.HashMap;

public class WseDelay {

    @Getter
    private long time;

    private HashMap<String, Long> used = new HashMap<>();

    public WseDelay(long time) {
        this.time = time;
    }

    /*
     * STRING SHIT!
     */
    public void update(Object id, String reason) {
        this.used.put(id.toString() + "-" + reason, System.currentTimeMillis());
    }

    public void update(Object id) {
        update(id.toString(), "");
    }

    public boolean check(Object id, String reason) {
        return (System.currentTimeMillis() - used.getOrDefault(id.toString() + "-" + reason, 0L) <= getTime());
    }

    public boolean check(Object id) {
        return check(id.toString(), "");
    }

}
