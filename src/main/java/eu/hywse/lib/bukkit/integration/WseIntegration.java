package eu.hywse.lib.bukkit.integration;

import org.bukkit.plugin.java.JavaPlugin;

public class WseIntegration<I> {

    private boolean enabled;

    private JavaPlugin plugin;
    private TargetPlugin target;

    private I integration;

    public WseIntegration(JavaPlugin plugin, TargetPlugin target, IntegrationCallback<I> found) {
        this.plugin = plugin;
        this.target = target;

        check(found);
    }

    public void check(IntegrationCallback<I> found) {

        System.out.println("[WseIntegration @ " + plugin.getDescription().getName() + "] Checking integrations...");

        if(!target.check(plugin.getServer())) {
            System.out.println("[WseIntegration @ " + plugin.getDescription().getName() + "] Integrations: " + String.join(", ", target.getTargets()) + " not found!");
            return;
        }

        System.out.println("[WseIntegration @ " + plugin.getDescription().getName() + "] Integrations: " + String.join(", ", target.getTargets()) + " found!");

        enabled = true;
        this.integration = found.found();
    }

    public I getIntegration() {
        return this.integration;
    }

    public interface IntegrationCallback<I> {
        I found();
    }

    public boolean isEnabled() {
        return enabled;
    }

}
