package com.github.discordia;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;


public class Discordia extends JavaPlugin {
    private JDA jda;
    private volatile boolean stopped = true;
    private static Discordia instance;
    private boolean vault_found = false;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vault_found = true;

            RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
            chat = rsp.getProvider();
        }

        getCommand("discordia").setExecutor(new CommandHandler());
        getCommand("discordia").setTabCompleter(new AutoCompleter());

        connectDiscord();
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    boolean connectDiscord() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(this.getConfig().getString("token"))
                    .addEventListener(new DiscordListener(this))
                    .build().awaitReady();
            this.getLogger().info("Connected to discord.");
            stopped = false;
            return true;
        } catch (InterruptedException | LoginException e) {
            this.getLogger().warning("Unable to login to discord, please edit the plugin configuration and add a discord bot token.");
            return false;
        }

    }

    void disconnectDiscord() {
        stopped = true;
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

    public static Discordia getInstance() {
        return instance;
    }

    public JDA getJDA() {
        return jda;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isVaultFound() {
        return vault_found;
    }

    public static Chat getChat() {
        return chat;
    }
}
