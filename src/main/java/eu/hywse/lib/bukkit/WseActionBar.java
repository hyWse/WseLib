package eu.hywse.lib.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WseActionBar {

  private static String nmsver;
  private static boolean useOldMethods = false;

  static {
    nmsver = Bukkit.getServer().getClass().getPackage().getName();
    nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

    if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver
        .startsWith("v1_7_")) { // Not sure if 1_7 works for the protocol hack?
      useOldMethods = true;
    }
  }

  public static void sendActionBar(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }

    if (nmsver.startsWith("v1_12_")) {
      sendActionBarPost112(player, message);
    } else {
      sendActionBarPre112(player, message);
    }
  }

  private static void sendActionBarPost112(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }
    try {
      Class<?> craftPlayerClass = Class
          .forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
      Object craftPlayer = craftPlayerClass.cast(player);
      Object ppoc;
      Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
      Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
      Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
      Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
      Class<?> chatMessageTypeClass = Class
          .forName("net.minecraft.server." + nmsver + ".ChatMessageType");
      Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
      Object chatMessageType = null;
      for (Object obj : chatMessageTypes) {
        if (obj.toString().equals("GAME_INFO")) {
          chatMessageType = obj;
        }
      }
      Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
      ppoc = c4.getConstructor(new Class<?>[]{c3, chatMessageTypeClass})
          .newInstance(o, chatMessageType);
      Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
      Object h = m1.invoke(craftPlayer);
      Field f1 = h.getClass().getDeclaredField("playerConnection");
      Object pc = f1.get(h);
      Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
      m5.invoke(pc, ppoc);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void sendActionBarPre112(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }
    try {
      Class<?> craftPlayerClass = Class
          .forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
      Object craftPlayer = craftPlayerClass.cast(player);
      Object ppoc;
      Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
      Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
      if (useOldMethods) {
        Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
        Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
        Method m3 = c2.getDeclaredMethod("a", String.class);
        Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
        ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
      } else {
        Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
        Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
        Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
        ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
      }
      Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
      Object h = m1.invoke(craftPlayer);
      Field f1 = h.getClass().getDeclaredField("playerConnection");
      Object pc = f1.get(h);
      Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
      m5.invoke(pc, ppoc);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void sendActionBar(JavaPlugin plugin, Player player, final String message,
      int duration) {
    sendActionBar(player, message);

    if (duration >= 0) {
      // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
      new BukkitRunnable() {
        @Override
        public void run() {
          sendActionBar(player, "");
        }
      }.runTaskLater(plugin, duration + 1);
    }

    // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
    while (duration > 40) {
      duration -= 40;
      new BukkitRunnable() {
        @Override
        public void run() {
          sendActionBar(player, message);
        }
      }.runTaskLater(plugin, (long) duration);
    }
  }

  public static void sendActionBarToAllPlayers(JavaPlugin plugin, String message) {
    sendActionBarToAllPlayers(plugin, message, -1);
  }

  public static void sendActionBarToAllPlayers(JavaPlugin plugin, String message, int duration) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      sendActionBar(plugin, p, message, duration);
    }
  }
}