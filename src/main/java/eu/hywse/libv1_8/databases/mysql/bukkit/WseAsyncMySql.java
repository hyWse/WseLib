package eu.hywse.libv1_8.databases.mysql.bukkit;

import eu.hywse.libv1_8.databases.mysql.WseAsyncMySqlWrapper;
import eu.hywse.libv1_8.databases.mysql.WseMySqlWrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class WseAsyncMySql extends WseAsyncMySqlWrapper {

    private JavaPlugin plugin;

    public WseAsyncMySql(JavaPlugin plugin, WseMySqlWrapper mySql) {
        super(mySql);
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

}
