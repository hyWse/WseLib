package eu.hywse.libv1_9.misc.delay.player;

import org.bukkit.entity.Player;

public class WseDelay extends eu.hywse.libv1_9.misc.delay.uuid.WseDelay {

    public WseDelay(long time) {
        super(time);
    }

    public void update(Player id, String reason) {
        update(id.getUniqueId(), reason);
    }

    public void update(Player id) {
        update(id.getUniqueId(), "");
    }

    public boolean check(Player id, String reason) {
        return check(id.getUniqueId(), reason);
    }

    public boolean check(Player id) {
        return check(id.getUniqueId());
    }

}
