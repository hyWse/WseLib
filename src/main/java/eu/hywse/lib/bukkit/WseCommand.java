package eu.hywse.lib.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * @author hyWse
 * @version 1.1
 */
public abstract class WseCommand {

    private static CommandMap commandMap;
    private final JavaPlugin plugin;
    private final String command;
    private final String description;
    private final List<String> alias;
    private final String usage;
    private final String permMessage;

    private String prefix = null;

    /*
     * Constructors
     */
    public WseCommand(JavaPlugin plugin, String command) {
        this(plugin, command, null, null, null, null);
    }

    public WseCommand(JavaPlugin plugin, String command, String usage) {
        this(plugin, command, usage, null, null, null);
    }

    public WseCommand(JavaPlugin plugin, String command, String usage, String description) {
        this(plugin, command, usage, description, null, null);
    }

    public WseCommand(JavaPlugin plugin, String command, String usage, String description, String permissionMessage) {
        this(plugin, command, usage, description, permissionMessage, null);
    }

    public WseCommand(JavaPlugin plugin, String command, String usage, String description, List<String> aliases) {
        this(plugin, command, usage, description, null, aliases);
    }

    public WseCommand(JavaPlugin plugin, String command, String usage, String description, String permissionMessage, List<String> aliases) {
        this.command = command.toLowerCase();
        this.usage = usage;
        this.description = description;
        this.permMessage = permissionMessage;
        this.alias = aliases;
        this.plugin = plugin;
        register();
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void register() {
        ReflectCommand cmd = new ReflectCommand(this.command, this);

        // Aliases
        if (this.alias != null)
            cmd.setAliases(this.alias);

        // Description
        if (this.description != null)
            cmd.setDescription(this.description);

        // Usage
        if (this.usage != null)
            cmd.setUsage(this.usage);

        // Permission message
        if (this.permMessage != null)
            cmd.setPermissionMessage(this.permMessage);

        getCommandMap().register("", cmd);
    }

    private CommandMap getCommandMap() {
        if (commandMap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return commandMap;
        }
        return getCommandMap();
    }

    /*
     * Abstracts
     */
    public abstract void onConsoleCommand(CommandSender sender, Command command, String[] args) throws WseCommandNoPermissionException;

    public abstract void onPlayerCommand(Player player, Command command, String[] args) throws WseCommandNoPermissionException;

    public abstract List<String> onTabComplete(CommandSender sender, Command command, String[] args) throws WseCommandNoPermissionException;


    /*
     * Messages
     */
    public void definePrefix(String prefix) {
        this.prefix = prefix;
    }

    public void sendMessage(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8▌ &c" + (prefix == null || prefix.length() == 0 ? "" : prefix) + " &8> &7" + message));
    }

    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, this.prefix);
    }

    public void sendNoPermissionMessage(CommandSender sender, String permission) {
        sendMessage(sender, "&cDir fehlen folgende Permissions: &6" + permission + "&c!");
    }

    /*
     * Permission
     */
    public void checkPermission(CommandSender sender, String permission) throws WseCommandNoPermissionException {
        if (!sender.hasPermission(permission)) {
            throw new WseCommandNoPermissionException("The player \"" + plugin.getName() + "\" lacks the following permissions: " + permission, permission);
        }
    }
    public class WseCommandNoPermissionException extends Exception {
        String permission;

        WseCommandNoPermissionException(String message, String permission) {
            super(message);

            this.permission = permission;
        }

        String getPermission() {
            return permission;
        }
    }

    /*
     * Functions
     */
    public String joinArgs(String[] args, int start, int end) {

        if(args.length == 0) {
            return "";
        }

        if(start < 0) start = 0;
        if(end < 0) end = 0;

        if (end < start) {
            int _end = end;

            end = start;
            start = _end;
        }

        StringBuilder res = new StringBuilder();

        for (int i = start; i < end; i++) {
            res.append(args[i]).append(" ");
        }

        return res.toString().trim();
    }
    public String joinArgs(String[] args, int start) {
        return joinArgs(args, start, args.length);
    }
    public String joinArgs(String[] args) {
        return joinArgs(args, 0);
    }

    private enum ParseMode {
        UNIX,
        COLON
    }

    public HashMap<String, String> parseArgs(String[] args, ParseMode mode) {
        HashMap<String, String> keys = new HashMap<>();

        String key = "";
        String temp = "";

        if(mode == ParseMode.UNIX) {
            for (String arg : args) {
                // Key
                if (arg.startsWith("--")) {

                    // Save
                    if (key.trim().length() > 0) {
                        keys.put(key, temp.trim());
                    }

                    // Reset temp
                    temp = "";

                    // Next
                    key = arg.substring(2);
                    continue;
                }

                // Value
                temp += arg + " ";
            }

            if(temp.trim().length() > 0) {
                keys.put(key, temp.trim());
            }
        }

        if(mode == ParseMode.COLON) {
            for (String arg : args) {
                // Key
                if (arg.contains(":")) {

                    // Save
                    if (key.trim().length() > 0) {
                        keys.put(key, temp.trim());
                    }

                    // Next
                    key = arg.split(":")[0];
                    temp = arg.split(":")[1];
                    continue;
                }

                // Value
                temp += arg + " ";
            }

            if(temp.trim().length() > 0) {
                keys.put(key, temp.trim());
            }
        }

        return keys;
    }

    /*
     * Command Execution
     */
    private class ReflectCommand extends Command {

        private WseCommand executor;

        ReflectCommand(String command, WseCommand executor) {
            super(command);

            this.executor = executor;
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (executor == null) {
                return false;
            }

            // Player command
            if (sender instanceof Player) {
                try {
                    executor.onPlayerCommand((Player) sender, this, args);
                } catch (WseCommandNoPermissionException ex) {
                    sendNoPermissionMessage(sender, ex.getPermission());
                }
                return true;
            }

            // Console command
            try {
                executor.onConsoleCommand(sender, this, args);
            } catch (WseCommandNoPermissionException ex) {
                sendNoPermissionMessage(sender, ex.getPermission());
            }
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) {
            if (executor == null) {
                return null;
            }

            // Tab
            try {
                return executor.onTabComplete(sender, this, args);
            } catch (WseCommandNoPermissionException e) {
                sendNoPermissionMessage(sender, e.getPermission());
                return null;
            }
        }
    }

}