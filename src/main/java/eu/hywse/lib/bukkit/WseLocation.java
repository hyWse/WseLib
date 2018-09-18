
package eu.hywse.lib.bukkit;

import eu.hywse.lib.config.WseConfig;
import eu.hywse.lib.misc.extension.IterateBlockConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author hyWse
 * @ver: 0.1
 */
public class WseLocation extends Location {

    public WseLocation(WseConfig config, String path) {
        this(config.getConfig().getString(path));
    }

    public WseLocation(Block block) {
        this(block.getLocation());
    }

    public WseLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
    }

    public WseLocation(String string) {
        this(string, "//");
    }

    public WseLocation(String string, String splitter) {
        super(Bukkit.getServer().getWorld(string.split(splitter)[0]), Double.parseDouble(string.split(splitter)[1]),
                Double.parseDouble(string.split(splitter)[2]), Double.parseDouble(string.split(splitter)[3]),
                Float.parseFloat(string.split(splitter)[4]), Float.parseFloat(string.split(splitter)[5]));
    }

    public WseLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public WseLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public static void iterateBlocks(Location startPoint, Location endPoint, int maxIterations, IterateBlockConsumer action) {

        // Check world
        if (!startPoint.getWorld().getName().equalsIgnoreCase(endPoint.getWorld().getName())) {
            return;
        }

        Vector max = Vector.getMaximum(startPoint.toVector(), endPoint.toVector());
        Vector min = Vector.getMinimum(startPoint.toVector(), endPoint.toVector());

        int current = 0;

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {

                    // Check max
                    current++;
                    if (current >= maxIterations) {
                        break;
                    }

                    // Action
                    Block block = startPoint.getWorld().getBlockAt(x, y, z);
                    action.iterate(current, x, y, z, block);
                }
            }
        }
    }

    public static void iterateBlocks(Location startPoint, Location endPoint, IterateBlockConsumer action) {
        iterateBlocks(startPoint, endPoint, 1000, action);
    }

    @Override
    public String toString() {
        return toString("//");
    }

    public String toString(String splitter) {
        return getWorld().getName() + splitter + getX() + splitter + getY() + splitter + getZ() + splitter + getYaw() + splitter + getPitch();
    }

    public String getWorldName() {
        return getWorld().getName();
    }

    public void saveToConfig(WseConfig config, String path) {
        config.getConfig().set(path, toString());
        config.saveConfig();
    }

    public WseLocation getSafeLocation() {

        Block buttom = getBlock();
        Block top = clone().add(0, 1, 0).getBlock();

        if ((buttom != null && top != null) && (buttom.getType() != Material.AIR || top.getType() != Material.AIR) && top.getY() <= 256) {
            for (int i = buttom.getY(); i <= 256; i++) {
                Block bB = add(0, 1, 0).getBlock();
                Block bT = clone().add(0, 1, 0).getBlock();

                if (bB.getType() == Material.AIR && bT.getType() == Material.AIR) break;
            }
        }

        return this;
    }

    public static int countAirBlocksUnderPlayer(Player player) {
        int air = 0;
        Location loc = player.getLocation();
        for (int y = loc.getBlockY(); y > 0; y++) {
            Block block = loc.subtract(0, 1, 0).getBlock();
            if (block.getType() == Material.AIR) air++;
            else break;
        }
        return air;
    }

}