package me.stephenminer.claims.commands;

import me.stephenminer.claims.Claims;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GiveShovel implements CommandExecutor {
    private final Claims plugin;
    public GiveShovel(Claims plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("claims.commands.givewand")){
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            if (args.length < 1){
                addItem(player);
                return true;
            }else{
                try{
                    Player p = Bukkit.getPlayerExact(ChatColor.stripColor(args[0]));
                    addItem(p);
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error has occured! The most likely cause is that the player you input doesn't exist!");
                }
                return false;
            }
        }else sender.sendMessage(ChatColor.RED + "Waltuh, only players can use this command waltuh.");
        return false;
    }

    private void addItem(Player player){
        HashMap<Integer, ItemStack> drop = player.getInventory().addItem(plugin.wand());
        if (!drop.isEmpty()){
            player.getWorld().dropItemNaturally(player.getLocation(), plugin.wand());
            player.sendMessage(ChatColor.GREEN + "Your inventory was full so item was dropped on the floor!");

        } else player.sendMessage(ChatColor.GREEN + "Added item to your inventory");
    }
}
