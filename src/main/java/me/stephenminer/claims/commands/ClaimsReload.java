package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimsReload implements CommandExecutor {
    private final Claims plugin;
    public ClaimsReload(Claims plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("claims.commands.reload")){
                player.sendMessage(ChatColor.RED + "Jesse! Jesse! We don't have permission to use this command Jesse!");
                return false;
            }
            plugin.settings.reloadConfig();
            plugin.regionFile.reloadConfig();

        }
        return false;
    }
}
