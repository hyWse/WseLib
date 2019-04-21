package eu.hywse.lib.misc.delay.uuid;

import java.util.UUID;

@Deprecated
/**
 * See {@link eu.hywse.lib.misc.delay.WseCooldown}
 */
public class WseDelay extends eu.hywse.lib.misc.delay.WseDelay {

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
