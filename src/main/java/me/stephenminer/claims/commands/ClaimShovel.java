package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ClaimShovel implements CommandExecutor {
    private final Claims plugin;
    public ClaimShovel(Claims plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("claims.commands.shovel")){
                player.sendMessage(ChatColor.RED + "Jesse! Jesse! You can't use this command Jesse!");
                return false;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && item.getType() == Material.GOLDEN_SHOVEL) {
                item.setAmount(item.getAmount() - 1);
                HashMap<Integer, ItemStack> drop = player.getInventory().addItem(plugin.wand());
                if (!drop.isEmpty()){
                    player.sendMessage(ChatColor.RED + "Your inventory was full, dropping item on the floor!");
                    player.getWorld().dropItemNaturally(player.getLocation(), plugin.wand());
                }
            }else player.sendMessage(ChatColor.RED + "You need to be holding a golden shovel to use this command");
        }else sender.sendMessage(ChatColor.RED + "Jesse don't you understand! Only players can use this command Jesse! Not consoles! Jesse!");
        return false;
    }




}
