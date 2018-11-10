
package eu.hywse.libv1_6.bukkit;

import eu.hywse.libv1_6.bukkit.config.WseConfig;
import eu.hywse.libv1_6.bukkit.extension.IterateBlockConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author hyWse
 * @version 0.1
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

    /**
     * Iterates through every block in selection
     *
     * @param startPoint First Block
     * @param endPoint Second Block
     * @param maxIterations Max. Block amount
     * @param action Action for every block
     */
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

    /**
     * Iterates through every block in selection
     * Default maxIterations = 1000
     *
     * @param startPoint First Block
     * @param endPoint Second Block
     * @param action Action for every block
     */
    public static void iterateBlocks(Location startPoint, Location endPoint, IterateBlockConsumer action) {
        iterateBlocks(startPoint, endPoint, 1000, action);
    }

    /**
     * Returns location as string
     * Default splitter = "//"
     * @return String - Location as String
     */
    @Override
    public String toString() {
        return toString("//");
    }

    /**
     * Returns location as string
     * @param splitter Splitter betweenx,y,z,yaw,pitch,world
     * @return String - Location as String
     */
    public String toString(String splitter) {
        return getWorld().getName() + splitter + getX() + splitter + getY() + splitter + getZ() + splitter + getYaw() + splitter + getPitch();
    }

    /**
     * Returns the world name of location
     * @return World-Name
     */
    public String getWorldName() {
        return getWorld().getName();
    }

    /**
     * Saves the location as string in config
     * @param config Config
     * @param path Path
     */
    public void saveToConfig(WseConfig config, String path) {
        config.getConfig().set(path, toString());
        config.saveConfig();
    }

    /**
     * Returns the highest point of a location
     * @return Highest point of location
     */
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

    /**
     * Counts empty blocks under a player
     * @param player Player
     * @return Empty Block-Count as int
     */
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