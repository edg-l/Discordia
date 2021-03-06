package xyz.ryozuki.minecraft.discordia;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.text.MessageFormat;


public class Discordia extends JavaPlugin {
    private JDA jda = null;
    private volatile boolean stopped = true;
    private boolean vaultFound = false;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 6289);

        saveDefaultConfig();
        reloadConfig();

        boolean usesChatProvider = false;

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp != null) {
                vaultFound = true;
                chat = rsp.getProvider();
                getLogger().info("Using Vault chat provider.");
                usesChatProvider = true;
            }
        }

        String pieValue = usesChatProvider ? "yes" : "no";
        metrics.addCustomChart(new Metrics.SimplePie("vault_chat", () -> pieValue));

        getCommand("discordia").setExecutor(new CommandHandler(this));
        getCommand("discordia").setTabCompleter(new AutoCompleter());

        if (!connectDiscord()) {
            getLogger().warning("Plugin disabled, fix your configuration.");
            getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    boolean connectDiscord() {
        try {
            JDABuilder builder = JDABuilder.createDefault(this.getConfig().getString("token"));
            builder.addEventListeners(new DiscordListener(this));
            builder.setActivity(Activity.playing(MessageFormat.format("{0}help",
                    getConfig().getString("command_prefix"))));
            jda = builder.build();
            getLogger().info("Connected to discord.");
            stopped = false;
            return true;
        } catch (LoginException e) {
            getLogger().warning("Unable to login to discord, please edit the plugin " +
                    "configuration and add a discord bot token.");
            return false;
        }

    }

    void disconnectDiscord() {
        stopped = true;
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

    /**
     * Sends a message to the discord channel
     *
     * @param message The message
     */
    void sendToDiscord(String message) {
        if (jda != null) {
            String id = getConfig().getString("send_channel");

            if (id == null) {
                getLogger().warning("'send_channel' is null, message could not be sent.");
                return;
            }

            TextChannel channel = jda.getTextChannelById(id);

            if (channel == null) {
                getLogger().warning("'send_channel' channel not found, message could not be sent.");
                return;
            }

            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void onDisable() {
        disconnectDiscord();
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
        return vaultFound;
    }

    public static Chat getChat() {
        return chat;
    }
}
