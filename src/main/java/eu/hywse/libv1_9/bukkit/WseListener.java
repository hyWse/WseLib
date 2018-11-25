package eu.hywse.libv1_9.bukkit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author hyWse
 * @version 0.1
 */
public class WseListener implements Listener {

	JavaPlugin plugin;

	public WseListener(JavaPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

}
