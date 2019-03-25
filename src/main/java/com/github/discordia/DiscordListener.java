package com.github.discordia;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    private Discordia discordia;

    DiscordListener(Discordia discordia) {
        super();
        this.discordia = discordia;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (discordia.Stopped)
            return;

        List<String> channelIDs = discordia.getConfig().getStringList("channels");

        if (event.isFromType(ChannelType.TEXT) && channelIDs.contains(event.getTextChannel().getId())
                && event.getMessage().getAuthor() != discordia.jda.getSelfUser()) {

            if (discordia.getConfig().getBoolean("ignore_bots") && event.getAuthor().isBot())
                return;

            List<String> ignoreUsers = discordia.getConfig().getStringList("ignore_discord_users");

            if (ignoreUsers.contains(event.getAuthor().getId()))
                return;

            String message = event.getMessage().getContentDisplay();
            String commandPrefix = discordia.getConfig().getString("command_prefix");
            if(commandPrefix == null)
                commandPrefix = "!";

            if(message.startsWith(commandPrefix)) {
                String command = message.substring(commandPrefix.length());
                switch (command) {
                    case "players":
                        StringBuilder players = new StringBuilder();
                        for(Player ply: discordia.getServer().getOnlinePlayers()) {
                            players.append(ply.getName());
                            players.append(" ");
                        }
                        event.getChannel().sendMessage("Current players: " + players).queue();
                }
                return;
            }


            List<String> ignoreStartWith = discordia.getConfig().getStringList("ignore_starts_with");

            for (String x : ignoreStartWith) {
                if (message.startsWith(x))
                    return;
            }

            String format = ChatColor.translateAlternateColorCodes('&', discordia.getConfig().getString("format"));
            String formatted = MessageFormat.format(
                    format,
                    event.getMember().getUser().getName(),
                    message,
                    event.getGuild().getName(),
                    event.getChannel().getName()
            );
            discordia.getServer().broadcastMessage(formatted);
        }
    }
}
