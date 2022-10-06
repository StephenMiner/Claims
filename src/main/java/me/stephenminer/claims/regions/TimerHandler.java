package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import me.stephenminer.claims.LinkedBag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TimerHandler implements Listener {
    private final Claims plugin;
    public TimerHandler(Claims plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void addTimer(PlayerJoinEvent event){
        Player player = event.getPlayer();
        ClaimBlockTimer timer = new ClaimBlockTimer(plugin, player);
        timer.runTimer();
        plugin.timers.add(timer);
    }

    @EventHandler
    public void removeTimer(PlayerQuitEvent event){
        Player player = event.getPlayer();
        LinkedBag<ClaimBlockTimer>.Node node = plugin.timers.iterate();
        while(node != null){
            if (node.data().getPlayer().equals(player)) {
                node.data().save();
                plugin.timers.remove(node.data());
                Bukkit.broadcastMessage("A");
                return;
            }
            node = node.next();
        }
    }
}
