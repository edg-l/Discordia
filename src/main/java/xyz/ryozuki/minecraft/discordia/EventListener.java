package xyz.ryozuki.minecraft.discordia;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private Discordia discordia;

    public EventListener(Discordia discordia) {
        super();
        this.discordia = discordia;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(discordia.getConfig().getBoolean("enable_join_event")) {
            discordia.sendToDiscord(ChatColor.stripColor(event.getJoinMessage()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(discordia.getConfig().getBoolean("enable_leave_event")) {
            discordia.sendToDiscord(ChatColor.stripColor(event.getQuitMessage()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled())
            return;
        if(discordia.getConfig().getBoolean("enable_kick_event")) {
            discordia.sendToDiscord(ChatColor.stripColor(event.getLeaveMessage()));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(discordia.getConfig().getBoolean("enable_player_death_event")) {
            discordia.sendToDiscord(ChatColor.stripColor(event.getDeathMessage()));
        }
    }
}
