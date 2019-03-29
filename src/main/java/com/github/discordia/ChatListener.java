package com.github.discordia;

import net.dv8tion.jda.core.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
    private Pattern discordPattern = Pattern.compile("(@[^# ]{2,32})");

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (Discordia.getInstance().isStopped())
            return;
        if (!event.getPlayer().hasPermission("discordia.chat.send"))
            return;

        List<String> ignoreUsers = Discordia.getInstance().getConfig().getStringList("ignore_mc_users");

        if (ignoreUsers.contains(event.getPlayer().getName()))
            return;

        String format = Discordia.getInstance().getConfig().getString("format_discord");

        String message = event.getMessage();
        List<String> ignoreStartWith = Discordia.getInstance().getConfig().getStringList("ignore_starts_with");

        for (String x : ignoreStartWith) {
            if (message.startsWith(x))
                return;
        }

        if (event.getPlayer().hasPermission("discordia.mention")) {
            Matcher m = discordPattern.matcher(message);
            while (m.find()) {
                List<User> users = Discordia.getInstance().getJDA().getUsersByName(m.group(1).substring(1), true);
                if (!users.isEmpty()) {
                    message = m.replaceAll(users.get(0).getAsMention());
                }
            }
        }
        String messageFormatted = MessageFormat.format(
                format,
                event.getPlayer().getName(),
                message,
                Discordia.getInstance().isVaultFound() ? Discordia.getChat().getPlayerPrefix(event.getPlayer()) : null,
                Discordia.getInstance().isVaultFound() ? Discordia.getChat().getPlayerSuffix(event.getPlayer()) : null);

        Discordia.getInstance().sendToDiscord(ChatColor.stripColor(messageFormatted));
    }
}
