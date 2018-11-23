package eu.hywse.libv1_8.misc.delay;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

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
    public void update(String id, String reason) {
        this.used.put(id + "-" + reason, System.currentTimeMillis());
    }

    public void update(String id) {
        update(id, "");
    }

    public boolean check(String id, String reason) {
        return (System.currentTimeMillis() - used.getOrDefault(id + "-" + reason, 0L) <= getTime());
    }

    public boolean check(String id) {
        return check(id, "");
    }

    /*
     * UUID SHIT!
     */
    public void update(UUID id, String reason) {
        update(id.toString(), reason);
    }
    public void update(UUID id) {
        update(id, "");
    }
    public boolean check(UUID id, String reason) {
        return check(id.toString(), reason);
    }
    public boolean check(UUID id) {
        return check(id.toString());
    }
}
