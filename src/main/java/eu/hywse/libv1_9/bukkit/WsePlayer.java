package eu.hywse.libv1_9.bukkit;

import lombok.Getter;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WsePlayer {

    @Getter
    private Player player;

    public WsePlayer(Player player) {
        this.player = player;
    }

    public boolean addItemElseDrop(JavaPlugin plugin, ItemStack...items) {
        var v = getPlayer().getInventory().addItem(items);

        Bukkit.getScheduler().runTask(plugin, ()-> v.values().forEach(item -> {
            getPlayer().getWorld().dropItem(getPlayer().getLocation(), item);
        }));

        return v.values().size() > 0;
    }

    public int getItems(ItemStack item) {
        return getPlayer().getInventory().all(item).size();
    }
}
