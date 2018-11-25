package eu.hywse.libv1_9.databases.mysql.bungee;

import eu.hywse.libv1_9.databases.mysql.WseMySqlWrapper;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;

public class WseMySql extends WseMySqlWrapper {

    private Plugin plugin;

    public WseMySql(@NonNull Plugin plugin) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName());
        this.plugin = plugin;
    }

    public WseMySql(@NonNull Plugin plugin, @NonNull String sqlInfoFile) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName(), sqlInfoFile);
        this.plugin = plugin;
    }

    public WseMySql(@NonNull Plugin plugin, @NonNull String host, @NonNull String database, @NonNull String user, @NonNull String password, int port) {
        super(plugin.getDescription().getName(), host, database, user, password, port);
        this.plugin = plugin;
    }

    public WseMySql(@NonNull Plugin plugin, @NonNull String sqlInfoFile, @NonNull String database) throws WseSqlInfoFileException {
        super(plugin.getDescription().getName(), sqlInfoFile, database);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
