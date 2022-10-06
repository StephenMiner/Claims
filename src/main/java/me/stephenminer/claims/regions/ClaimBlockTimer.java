package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ClaimBlockTimer {
    private final Claims plugin;
    private final Player player;
    private int incrementBy;
    private long playTime;
    //time stored as hours;
    private double timeBetween;
    public ClaimBlockTimer(Claims plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        incrementBy = 1024;
        timeBetween = 2;
        syncWithFiles();
        playTime = getPlayTime();
    }


    public void runTimer(){
        new BukkitRunnable(){
            long countdown = Math.round(timeBetween*60*60*20);
            @Override
            public void run(){
                if (!player.isOnline()){
                    this.cancel();
                }
                if (player.isDead()) return;
                if (playTime % countdown == 0){
                    if (plugin.claimBlocks.containsKey(player.getUniqueId()))
                        plugin.claimBlocks.put(player.getUniqueId(), plugin.claimBlocks.get(player.getUniqueId()) + incrementBy);
                    else plugin.claimBlocks.put(player.getUniqueId(), (long)incrementBy);
                    syncWithFiles();
                    timeBetween = (int) timeBetween*60*20;
                }
                playTime++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public int getIncrementBy(){
        return incrementBy;
    }
    public void setIncrement(int increment){
        this.incrementBy = increment;
    }

    public void save(){
        UUID uuid = player.getUniqueId();
        plugin.regionFile.getConfig().set("players." + uuid + ".play-time",playTime);
        plugin.regionFile.getConfig().set("players." + uuid + ".claim-blocks", plugin.claimBlocks.get(uuid));
        plugin.regionFile.saveConfig();
    }
    public long getPlayTime(){
      return plugin.regionFile.getConfig().getLong("players." + player.getUniqueId() + ".play-time");
    }

    public void syncWithFiles(){
        timeBetween = plugin.settings.getConfig().getDouble("settings.hours-between-addition");
        incrementBy = plugin.settings.getConfig().getInt("settings.claim-blocks-per");
    }

    public Player getPlayer(){
        return player;
    }
}
