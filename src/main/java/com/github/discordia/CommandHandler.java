package com.github.discordia;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@org.bukkit.plugin.java.annotation.command.Command(name = "discordia", permission = "discordia.commands")
public class CommandHandler implements CommandExecutor {
    private Discordia discordia;

    CommandHandler(Discordia discordia) {
        this.discordia = discordia;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commands = "&l&9Commands are&r: &3reload&r, &3disable&r, &3enable&r, &3reconnect&r";
        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commands));
            return true;
        }
        String subCommand = args[0];

        switch (subCommand) {
            case "reload":
                discordia.reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "[&l&5Discordia&r] Configuration &6reloaded&r."));
                return true;
            case "disable":
                discordia.Stopped = true;
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "[&l&5Discordia&r] Bridge &4stopped&r."));
                break;
            case "enable":
                discordia.Stopped = false;
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "[&l&5Discordia&r] Bridge &aenabled&r."));
                break;
            case "reconnect":
                discordia.disconnectDiscord();
                if (discordia.connectDiscord()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "[&l&5Discordia&r] &aReconnected&r."));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "[&l&5Discordia&r] &4Couldn't connect&r. &eCheck terminal&r."));
                }
                break;
            default:
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commands));
                break;
        }

        return false;
    }
}
