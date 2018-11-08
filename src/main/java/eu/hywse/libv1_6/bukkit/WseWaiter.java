package eu.hywse.libv1_6.bukkit;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WseWaiter {

    @Getter
    private long time;

    private HashMap<String, Long> used = new HashMap<>();

    public WseWaiter(long time) {
        this.time = time;
    }

    public void update(Player player, String reason) {
        this.used.put(player.getUniqueId().toString() + "-" + reason, System.currentTimeMillis());
    }

    public void update(Player player) {
        update(player, "");
    }

    public boolean check(Player player, String reason) {
        return (System.currentTimeMillis() - used.getOrDefault(player.getUniqueId().toString() + "-" + reason, 0L) <= getTime());
    }

    public boolean check(Player player) {
        return check(player, "");
    }

}
