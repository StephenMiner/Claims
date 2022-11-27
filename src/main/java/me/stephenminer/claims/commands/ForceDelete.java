package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import me.stephenminer.claims.regions.ClaimedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class ForceDelete implements CommandExecutor, TabCompleter {
    private final Claims plugin;
    public ForceDelete(Claims plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("claims.commands.forcedelete")){
                player.sendMessage(ChatColor.RED + "You do not have permission o use this command");
                return false;
            }
        }
        if (args.length < 2){
            sender.sendMessage(ChatColor.RED + "Not enough arguments!",
                    ChatColor.YELLOW + "You must input a player-name & a region name you'd like to remove to use this command!");
            return false;
        }
        String pName = args[0];
        String id = args[1];
        UUID owner = fromName(pName);
        if (regionReal(owner, id)){
            ClaimedRegion claim = null;
            for (ClaimedRegion region : plugin.regions){
                if (region.getOwner().equals(owner) && region.getId().equals(id)){
                    claim = region;
                    break;
                }
            }
            if (claim != null) {
                plugin.regions.remove(claim);
                claim.delete();
                sender.sendMessage(ChatColor.GREEN + "Deleted the region " + id + " with the owner " + pName);
                return true;
            }else sender.sendMessage(ChatColor.RED + "The region exists in files but not in memory, maybe the region's information is damaged? Check its region entry!");
        }else sender.sendMessage(ChatColor.RED + "the region " + id + " with owner " + pName + " doesn't exist!");
        return false;
    }


    private boolean regionReal(UUID owner, String id){
        return plugin.regionFile.getConfig().contains("regions." + owner + "." + id);
    }

    private UUID fromName(String name){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return playerEntries(args[0]);
        if (size == 2) return regions(args[0], args[1]);
        return null;
    }


    private List<String> filter(Collection<String> base, String match){
        match = match.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = entry.toLowerCase();
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }
    private List<String> playerEntries(String match){
        Set<String> uuids = plugin.regionFile.getConfig().getConfigurationSection("regions").getKeys(false);
        List<String> names = new ArrayList<>();
        for (String entry : uuids){
            UUID uuid = UUID.fromString(entry);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            names.add(offlinePlayer.getName());
        }
        return filter(names, match);
    }

    private List<String> regions(String name, String match){
        UUID uuid = fromName(name);
        Set<String> entries = plugin.regionFile.getConfig().getConfigurationSection("regions." + uuid).getKeys(false);
        return filter(entries, match);
    }
}
