package eu.hywse.libv1_9.bukkit;

import eu.hywse.libv1_9.text.WseStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
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

    /**
     * Returns the plugin
     *
     * @return JavaPlugin
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * This function registers a command
     */
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

    /**
     * Returns the list of commands in Bukkit / Spigot
     *
     * @return CommandMap
     */
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

    /**
     * This method is executed when a player has entered the command
     *
     * @param player  The player who executed the command
     * @param command Command
     * @param args    Arguments
     * @throws WseCommandException In some checks, e.g. if the executor is a player, throw an exception
     */
    public abstract void onPlayerCommand(Player player, Command command, String[] args) throws WseCommandException;

    /**
     * This method is executed when the console has entered the command
     *
     * @param sender  Console
     * @param command Command
     * @param args    Arguments
     * @throws WseCommandException In some checks, e.g. if the executor is a player, throw an exception
     */
    public void onConsoleCommand(CommandSender sender, Command command, String[] args) throws WseCommandException {

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String[] args) throws WseCommandException {
        return null;
    }


    public void definePrefix(String prefix) {
        this.prefix = prefix;
    }

    public void sendMessage(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8▌ &c" + (prefix == null || prefix.length() == 0 ? getClass().getSimpleName() : prefix) + " &8> &7" + message));
    }

    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, this.prefix);
    }

    public void sendNoPermissionMessage(CommandSender sender, String permission) {
        sendMessage(sender, "&cDir fehlen folgende Permissions: &6" + permission + "&c!");
    }



    public enum ParseMode {
        UNIX,
        COLON
    }

    @Data
    @AllArgsConstructor
    private static class Argument {
        String name;
        ArgType type;

        public enum ArgType {
            INT,
            DBL,
            BOOL,
            STR,
            TXT;

            public static ArgType getType(String s) {
                for (ArgType type : ArgType.values()) {
                    if (type.name().toLowerCase().equalsIgnoreCase(s.toLowerCase())) return type;
                }
                return null;
            }
        }
    }

    /*
     * Functions
     */
    public void checkPermission(CommandSender sender, String permission) throws WseCommandException {
        if (sender.hasPermission(permission)) return;
        throw new WseCommandException("The player \"" + plugin.getName() + "\" lacks the following permissions: " + permission, WseCommandException.Reason.NO_PERMISSION, permission);
    }

    /*
     * checkArgs(player, args, "<player> <message> <money|int>");
     */
    public void checkArgs(String[] args, String pattern) throws WseCommandException {

        // Check if pattern exists
        if (pattern == null || pattern.length() == 0) {
            return;
        }

        /*
         * Get arguments
         */
        LinkedList<Argument> arguments = new LinkedList<>();

        // Every pattern word
        for (String s : pattern.split(" ")) {
            if (s.length() == 2) continue;

            s = s.substring(1, s.length() - 1);

            // Type
            Argument.ArgType type = Argument.ArgType.STR;
            if (s.contains("|")) {
                String typeStr = s.split("\\|")[1];
                Argument.ArgType typeF = Argument.ArgType.getType(typeStr);
                if (typeF != null) {
                    type = typeF;
                }
            }

            String name = s;
            if (s.contains("|")) {
                name = s.split("\\|")[0];
            }

            Argument argument = new Argument(name, type);
            arguments.add(argument);
        }

        if(arguments.size() == 0) {
            return;
        }

        if(args.length < arguments.size()) {
            throw new WseCommandException("Too few args!", WseCommandException.Reason.INVALID_ARGS, args);
        }

        /*
         * Check arguments
         */
        Argument lastArgument = arguments.getFirst();

        for (String arg : args) {
            if(lastArgument.getType() == Argument.ArgType.TXT) continue;

            lastArgument = arguments.getFirst();
            arguments.removeFirst();

            switch (lastArgument.getType()) {
                case STR:
                    continue;

                case INT:
                    if(!WseStringUtil.isInteger(arg)) {
                        throw new WseCommandException("Arg \"" + arg + "\" is not a valid integer!", WseCommandException.Reason.INVALID_ARGS, arg);
                    }

                case DBL:
                    if(!WseStringUtil.isDouble(arg)) {
                        throw new WseCommandException("Arg \"" + arg + "\" is not a valid double!", WseCommandException.Reason.INVALID_ARGS, arg);
                    }

                case BOOL:
                    if(!WseStringUtil.isBoolean(arg)) {
                        throw new WseCommandException("Arg \"" + arg + "\" is not a valid boolean!", WseCommandException.Reason.INVALID_ARGS, arg);
                    }
            }
        }
    }

    public void checkPlayer(CommandSender sender) throws WseCommandException {
        if(sender instanceof Player) return;
        throw new WseCommandException("Sender is not a player!", WseCommandException.Reason.NOT_PLAYER, sender.getName());
    }

    public boolean checkArgsBool(String[] args, String pattern) {
        try {
            checkArgs(args, pattern);
            return true;
        } catch (WseCommandException e) {
            return false;
        }
    }

    /*
     * Functions
     */
    public String joinArgs(String[] args, int start, int end) {

        if (args.length == 0) {
            return "";
        }

        if (start < 0) start = 0;
        if (end < 0) end = 0;

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

    public HashMap<String, String> parseArgs(String[] args, ParseMode mode) {
        HashMap<String, String> keys = new HashMap<>();

        String key = "";
        String temp = "";

        if (mode == ParseMode.UNIX) {
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

            if (temp.trim().length() > 0) {
                keys.put(key, temp.trim());
            }
        }

        if (mode == ParseMode.COLON) {
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

            if (temp.trim().length() > 0) {
                keys.put(key, temp.trim());
            }
        }

        return keys;
    }

    public static class WseCommandException extends Exception {
        String[] extra;
        Reason reason;

        WseCommandException(String message, Reason reason, String... extra) {
            super(message);

            this.reason = reason;
            this.extra = extra;
        }

        public String[] getExtraArr() {
            return extra;
        }

        public String getExtraStr() {
            if (getExtraArr().length > 0) {
                return getExtraArr()[0];
            }
            return null;
        }

        public Reason getReason() {
            return reason;
        }

        public enum Reason {
            NO_PERMISSION,
            NOT_PLAYER,
            INVALID_ARGS;
        }
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
                } catch (WseCommandException ex) {
                    handleException(sender, label, ex);
                }
                return true;
            }

            // Console command
            try {
                executor.onConsoleCommand(sender, this, args);
            } catch (WseCommandException ex) {
                handleException(sender, label, ex);
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
            } catch (WseCommandException e) {
                handleException(sender, label, e);
                return null;
            }
        }
    }

    private void handleException(CommandSender sender, String label, WseCommandException ex) {
        switch (ex.getReason()) {
            case NO_PERMISSION:
                sendNoPermissionMessage(sender, ex.getExtraStr());
                break;

            case INVALID_ARGS:
                sendMessage(sender, "Ungültiger Syntax!" + (ex.getExtraStr() != null ? " &8[&a/" + label + " " + ex.getExtraStr() + "&8]" : ""));
                break;

            case NOT_PLAYER:
                sendMessage(sender, "Nur Spieler können diesen Befehl ausführen!");
                break;
        }
    }

}