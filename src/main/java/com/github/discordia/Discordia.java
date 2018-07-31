package com.github.discordia;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import javax.security.auth.login.LoginException;

@Plugin(name = "Discordia", version = "1.0.1")
@Description("Creates a bridge between discord and minecraft chat.")
@Author("Ryozuki")
@Permission(name = "discordia.chat.send",
        desc = "Only players with this permission will send messages to discord.")
@Permission(name = "discordia.commands",
        desc = "Allows the usage of discordia commands.")

public class Discordia extends JavaPlugin {
    JDA jda;
    volatile boolean Stopped = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        getCommand("discordia").setExecutor(new CommandHandler(this));
        getCommand("discordia").setTabCompleter(new AutoCompleter());

        connectDiscord();
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    boolean connectDiscord() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(this.getConfig().getString("token"))
                    .addEventListener(new DiscordListener(this))
                    .buildBlocking();
            this.getLogger().info("Connected to discord.");
            Stopped = false;
            return true;
        } catch (InterruptedException | LoginException e) {
            this.getLogger().warning("Unable to login to discord, please edit the plugin configuration and add a discord bot token.");
            return false;
        }

    }

    void disconnectDiscord() {
        Stopped = true;
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    /**
     * Sends a message to the discord channel
     *
     * @param message The message
     */
    void sendToDiscord(String message) {
        if (jda != null) {
            TextChannel channel = jda.getTextChannelById(getConfig().getString("send_channel"));
            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void onDisable() {
        disconnectDiscord();
    }
}
