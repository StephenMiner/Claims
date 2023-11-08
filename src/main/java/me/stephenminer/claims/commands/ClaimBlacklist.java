package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClaimBlacklist implements CommandExecutor, TabCompleter {
    private final Claims plugin;
    public ClaimBlacklist(){
        this.plugin = JavaPlugin.getPlugin(Claims.class);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("claims.commands.blacklist")){
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            if (args.length < 2){
                player.sendMessage(ChatColor.RED + "You need to say whether you want to add/remove an entry and the entry itself!");
                return false;
            }
            String arg = args[0].toLowerCase();
            String worldName = args[1].toLowerCase();
            if (arg.equals("add")){
                add(worldName);
                player.sendMessage(ChatColor.GREEN + "added world to whitelist");
            }
            if (arg.equals("remove")){
                remove(worldName);
                player.sendMessage(ChatColor.GREEN + "removed world from blacklist");
            }

        }
        return false;
    }

    private void add(String name){
        List<String> blacklist;
        if (!plugin.settings.getConfig().contains("settings.blacklist")) blacklist = new ArrayList<>();
        else blacklist = plugin.settings.getConfig().getStringList("settings.blacklist");
        if (!blacklist.contains(name)) blacklist.add(name);
        plugin.settings.getConfig().set("settings.blacklist", blacklist);
        plugin.settings.saveConfig();
    }
    private void remove(String name){
        List<String> blacklist;
        if (!plugin.settings.getConfig().contains("settings.blacklist")) blacklist = new ArrayList<>();
        else blacklist = plugin.settings.getConfig().getStringList("settings.blacklist");
        blacklist.remove(name);
        plugin.settings.getConfig().set("settings.blacklist", blacklist);
        plugin.settings.saveConfig();
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return options();
        else return null;
    }

    private List<String> options(){
        List<String> out = new ArrayList<>();
        out.add("add");
        out.add("remove");
        return out;
    }



}
