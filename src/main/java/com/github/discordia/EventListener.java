package com.github.discordia;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(Discordia.getInstance().getConfig().getBoolean("enable_join_event")) {
            Discordia.getInstance().sendToDiscord(ChatColor.stripColor(event.getJoinMessage()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(Discordia.getInstance().getConfig().getBoolean("enable_leave_event")) {
            Discordia.getInstance().sendToDiscord(ChatColor.stripColor(event.getQuitMessage()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled())
            return;
        if(Discordia.getInstance().getConfig().getBoolean("enable_kick_event")) {
            Discordia.getInstance().sendToDiscord(ChatColor.stripColor(event.getLeaveMessage()));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(Discordia.getInstance().getConfig().getBoolean("enable_player_death_event")) {
            Discordia.getInstance().sendToDiscord(ChatColor.stripColor(event.getDeathMessage()));
        }
    }
}
