package eu.hywse.lib.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WseConfirm {

    private Map<UUID, String> confirms;

    public WseConfirm() {
        this.confirms = new HashMap<>();
    }

    public boolean check(UUID uuid, String string) {
        if(confirms.containsKey(uuid) && confirms.get(uuid).equalsIgnoreCase(string)) {
            confirms.remove(uuid);
            return true;
        }

        return false;
    }

    public void update(UUID uuid, String string) {
        confirms.put(uuid, string);
    }

}
