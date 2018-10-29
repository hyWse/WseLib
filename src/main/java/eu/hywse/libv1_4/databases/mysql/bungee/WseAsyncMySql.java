package eu.hywse.libv1_4.databases.mysql.bungee;

import eu.hywse.libv1_4.databases.mysql.WseAsyncMySqlWrapper;
import eu.hywse.libv1_4.databases.mysql.WseMySqlWrapper;
import net.md_5.bungee.api.plugin.Plugin;

public class WseAsyncMySql extends WseAsyncMySqlWrapper {

    private Plugin plugin;

    public WseAsyncMySql(Plugin plugin, WseMySqlWrapper mySql) {
        super(mySql);
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable runnable) {
        plugin.getProxy().getScheduler().runAsync(plugin, runnable);
    }

}
