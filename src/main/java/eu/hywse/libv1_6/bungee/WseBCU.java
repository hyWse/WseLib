package eu.hywse.libv1_6.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class WseBCU {

    public static void sendMessage(CommandSender sender, String prefix, String message) {
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8▌ &c" + prefix + " &8> &7" + message)));
    }

}
