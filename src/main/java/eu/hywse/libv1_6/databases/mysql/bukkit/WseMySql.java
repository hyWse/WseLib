package eu.hywse.libv1_6.databases.mysql.bukkit;

import eu.hywse.libv1_6.databases.mysql.WseMySqlWrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class WseMySql extends WseMySqlWrapper {

    private JavaPlugin plugin;

    public WseMySql(JavaPlugin plugin) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName());
        this.plugin = plugin;
    }

    public WseMySql(JavaPlugin plugin, String sqlInfoFile) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName(), sqlInfoFile);
        this.plugin = plugin;
    }

    public WseMySql(JavaPlugin plugin, String host, String database, String user, String password, int port) {
        super(plugin.getDescription().getName(), host, database, user, password, port);
        this.plugin = plugin;
    }
    public WseMySql(JavaPlugin plugin, String sqlInfoFile, String database) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName(), sqlInfoFile, database);
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
