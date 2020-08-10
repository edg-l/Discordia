package xyz.ryozuki.minecraft.discordia;

import net.dv8tion.jda.api.entities.User;
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
    private Discordia discordia;

    public ChatListener(Discordia discordia) {
        super();
        this.discordia = discordia;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (discordia.isStopped())
            return;
        if (!event.getPlayer().hasPermission("discordia.chat.send"))
            return;

        List<String> ignoreUsers = discordia.getConfig().getStringList("ignore_mc_users");

        if (ignoreUsers.contains(event.getPlayer().getName()))
            return;

        String format = discordia.getConfig().getString("format_discord");

        if(format == null) {
            discordia.getLogger().warning("Configuration 'format_discord' is null, message will not be sent.");
            return;
        }

        String message = event.getMessage();
        List<String> ignoreStartWith = discordia.getConfig().getStringList("ignore_starts_with");

        for (String x : ignoreStartWith) {
            if (message.startsWith(x))
                return;
        }

        if (event.getPlayer().hasPermission("discordia.mention")) {
            Matcher m = discordPattern.matcher(message);
            while (m.find()) {
                List<User> users = discordia.getJDA().getUsersByName(m.group(1).substring(1), true);
                if (!users.isEmpty()) {
                    message = m.replaceAll(users.get(0).getAsMention());
                }
            }
        }

        String messageFormatted = MessageFormat.format(
                format,
                event.getPlayer().getName(),
                message,
                discordia.isVaultFound() ? Discordia.getChat().getPlayerPrefix(event.getPlayer()) : null,
                discordia.isVaultFound() ? Discordia.getChat().getPlayerSuffix(event.getPlayer()) : null);

        discordia.sendToDiscord(ChatColor.stripColor(messageFormatted));
    }
}
