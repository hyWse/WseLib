package eu.hywse.libv1_8.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WseBungeeUtils {

    private static JavaPlugin plugin;
    public static void definePlugin(JavaPlugin plugin) {
        WseBungeeUtils.plugin = plugin;
    }

    // With JavaPlugin
    public static void registerBungeeCordChannel(JavaPlugin plugin) {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    // No Java Plugin
    public static void registerBungeeCordChannel() {
        if(plugin == null) {
            System.out.println("[! @ WseBungeeUtils#registerBungeeCordChannel] Err: JavaPlugin not found.");
            return;
        }

        registerBungeeCordChannel(plugin);
    }

    public static void send(JavaPlugin plugin, String key, String value) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(key);
        out.writeUTF(value);
        Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    // No JavaPlugin
    public static void send(String key, String value) {
        if(plugin == null) {
            System.out.println("[! @ WseBungeeUtils#send] Err: JavaPlugin not found.");
            return;
        }

        send(plugin, key, value);
    }

    public static void sendPlayerToServer(JavaPlugin plugin, Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    // No Java Plugin
    public static void sendPlayerToServer(Player player, String server) {
        if(plugin == null) {
            System.out.println("[! @ WseBungeeUtils#sendPlayerToServer] Err: JavaPlugin not found.");
            return;
        }

        sendPlayerToServer(plugin, player, server);
    }
}