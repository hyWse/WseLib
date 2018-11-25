package eu.hywse.libv1_9.bukkit;

import eu.hywse.libv1_9.bukkit.config.WseConfig;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.LinkedList;

import static eu.hywse.libv1_9.bukkit.WseTextUtil.c;

@Log
public abstract class WsePlugin extends JavaPlugin {

    @Getter
    private static WsePlugin instance;

    @Getter
    private LinkedList<WseListener> listeners = new LinkedList<>();

    @Getter
    private LinkedList<WseCommand> commands = new LinkedList<>();

    @Getter
    private String prefix;

    private WseConfig config = new WseConfig(this, "config.yml");

    public WsePlugin(String prefix) {
        this.prefix = "&8▌ &c" + prefix + " &8> &7";
    }

    public abstract void onEnable();

    @Override
    public void onLoad() {
        // Set instance
        instance = this;

        super.onLoad();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public WseConfig getWseConfig() {
        return config;
    }

    public void registerListener(WseListener listener) {
        listeners.add(listener);
        log.info("[" + getPluginName() + "]: [EVT] * Registering Listener [" + listener.getClass().getSimpleName() + ".java]");
    }

    public void registerCommands(WseCommand command) {
        commands.add(command);
        log.info("[" + getPluginName() + "]: [CMD] * Registering Command [" + command.getClass().getSimpleName() + ".java]");
    }

    public WseListener getListener(Class<? extends WseListener> clazz) {
        Iterator<WseListener> it = listeners.stream().filter(listener -> clazz == listener.getClass()).iterator();
        if (!it.hasNext()) {
            return null;
        }
        return it.next();
    }

    public WseCommand getCommand(Class<? extends WseCommand> clazz) {
        Iterator<WseCommand> it = commands.stream().filter(command -> clazz == command.getClass()).iterator();
        if (!it.hasNext()) {
            return null;
        }
        return it.next();
    }

    public String getPluginName() {
        return getDescription().getName();
    }

    public static void sendMessage(CommandSender sender, String messsage) {
        sender.sendMessage(c(instance.getPrefix() + messsage));
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

}