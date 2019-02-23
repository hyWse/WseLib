package eu.hywse.libv1_11.misc.delay.uuid;

import java.util.UUID;

public class WseDelay extends eu.hywse.libv1_11.misc.delay.WseDelay {

    public WseDelay(long time) {
        super(time);
    }

    public void update(UUID id, String reason) {
        update(id.toString(), reason);
    }

    public void update(UUID id) {
        update(id.toString(), "");
    }

    public boolean check(UUID id, String reason) {
        return check(id.toString(), reason);
    }

    public boolean check(UUID id) {
        return check(id.toString());
    }

}
