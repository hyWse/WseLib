package eu.hywse.lib.bukkit;

import eu.hywse.lib.misc.WseQueue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WseAutoQueue<T> extends WseQueue<T> {

  private JavaPlugin plugin;

  public WseAutoQueue(JavaPlugin plugin) {
    super();

    this.plugin = plugin;
  }

  public WseAutoQueue(JavaPlugin plugin, int max) {
    super(max);

    this.plugin = plugin;
  }

  public BukkitTask createTask(int seconds, QueueCallback<T> callback) {
    return new BukkitRunnable() {
      @Override
      public void run() {
        if (!hasNext()) {
          return;
        }

        T t = next();
        if (t == null) {
          return;
        }

        callback.accept(t);
      }
    }.runTaskTimer(plugin, 20 * seconds, 20 * seconds);
  }

  public interface QueueCallback<T> {

    void accept(T t);
  }

}
