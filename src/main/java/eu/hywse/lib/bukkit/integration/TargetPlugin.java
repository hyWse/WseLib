package eu.hywse.lib.bukkit.integration;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class TargetPlugin {

    private String[] targets;
    private CheckMethod method;


    public TargetPlugin(String...names) {
        this(CheckMethod.OR, names);
    }

    public TargetPlugin(CheckMethod method, String...names) {
        this.targets = names;
        this.method = method;
    }

    public enum CheckMethod {
        AND,
        OR
    }


    public boolean check(Server server) {
        boolean res = false;

        for(String pluginName : targets) {
            Plugin plugin = server.getPluginManager().getPlugin(pluginName);

            if(plugin == null) {
                if(method == CheckMethod.OR) {
                    continue;
                }

                else if(method == CheckMethod.AND) {
                    res = false;
                    break;
                }
            }

            res = true;
        }

        return res;
    }

    public String[] getTargets() {
        return targets;
    }
}
