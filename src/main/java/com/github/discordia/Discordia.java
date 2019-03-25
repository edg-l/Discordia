package com.github.discordia;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;


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
                    .build().awaitReady();
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
            jda.shutdown();
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
