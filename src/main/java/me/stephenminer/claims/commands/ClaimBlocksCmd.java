package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimBlocksCmd implements CommandExecutor {
    private final Claims plugin;
    public ClaimBlocksCmd(Claims plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (plugin.claimBlocks.containsKey(player.getUniqueId())){
                player.sendMessage(ChatColor.GREEN + "You have " + plugin.claimBlocks.get(player.getUniqueId()) + " claim-blocks");
                return true;
            }else sender.sendMessage(ChatColor.RED + "You do not have any claim-blocks to spend");
        }else sender.sendMessage(ChatColor.RED + "Only players can use this command");
        return false;
    }
}
