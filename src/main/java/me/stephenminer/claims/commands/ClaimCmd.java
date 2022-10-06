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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ClaimCmd implements CommandExecutor, TabCompleter {
    private final Claims plugin;
    public ClaimCmd(Claims plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            int size = args.length;
            if (size < 3){
                player.sendMessage(ChatColor.RED + "Not enough arguments! You must input a claim-id (see tabcompleter), a subcmd (see the tabcompleter), and its argument (see the tabcompleter)");
                return false;
            }
            ClaimedRegion region = fromString(player.getUniqueId(), args[0]);
            if (region == null){
                player.sendMessage(ChatColor.RED + "Inputted region does not exist!");
                return false;
            }
            if (!region.getOwner().equals(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "You do not own the claim " + region.getId() + ". You are not allowed to edit claims you do not own!");
            }
            if (args[1].equalsIgnoreCase("showBorder")){
                boolean on = Boolean.parseBoolean(args[2]);
                if (on) {
                    region.setShowing(true);
                    region.showBorder();
                    player.sendMessage(ChatColor.GREEN + "Showing region border for " + region.getId());
                }else{
                    region.setShowing(false);
                    player.sendMessage(ChatColor.GREEN + "Stopped showing region border for " + region.getId());
                }
                return true;
            }

            if (args[1].equalsIgnoreCase("setDenyMsg")){
                region.setDenyMessage(ChatColor.stripColor(args[2]));
                player.sendMessage(ChatColor.GREEN + "Set denyMsg to " + args[2]);
                return true;
            }
            if (args[1].equalsIgnoreCase("allowExplosions")){
                boolean value = Boolean.parseBoolean(args[2]);
                region.setAllowExplosions(Boolean.parseBoolean(args[2]));
                player.sendMessage(ChatColor.GREEN + "Set allowExplosions to " + value);
                return true;
            }
            if (args[1].equalsIgnoreCase("allowLiquidFlow")){
                boolean value = Boolean.parseBoolean(args[2]);
                region.setAllowFlowIO(Boolean.parseBoolean(args[2]));
                player.sendMessage(ChatColor.GREEN + "Set allowLiquidFlow to " + value);
                return true;
            }
            if (args[1].equalsIgnoreCase("allowInteraction")){
                boolean value = Boolean.parseBoolean(args[2]);
                region.setAllowInteraction(Boolean.parseBoolean(args[2]));
                player.sendMessage(ChatColor.GREEN + "Set allowInteraction to " + value);
                return true;
            }
            if (args[1].equalsIgnoreCase("allowEntityInteraction")){
                boolean value = Boolean.parseBoolean(args[2]);
                region.setAllowEntityInteraction(Boolean.parseBoolean(args[2]));
                player.sendMessage(ChatColor.GREEN + "Set allowEntityInteraction to " + value);
                return true;
            }
            if (args[1].equalsIgnoreCase("trust")){
                Player p = getPlayer(args[2]);
                if (p == null){
                    player.sendMessage(ChatColor.RED + "The player you inputted doesn't exist!");
                    return false;
                }
                region.addTrustedPlayer(p);
                player.sendMessage(ChatColor.GREEN + "Made " + p.getName() + " a trusted player in claim " + region.getId());
                p.sendMessage(ChatColor.GOLD + "You are now trusted in " + player.getName() + "'s claim " + region.getId());
                return true;
            }

            if (args[1].equalsIgnoreCase("untrust")){
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                if (!region.getTrusted().contains(p.getUniqueId())){
                    player.sendMessage(ChatColor.RED + "The player you inputted doesn't exist!");
                    return false;
                }
                region.removeTrustedPlayer(p);
                if (p.isOnline()){
                    p.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer trusted in " + player.getName() + "'s claim " + region.getId());
                }
                player.sendMessage(ChatColor.GREEN + "Took away trust from player " + p.getName() + " in region " + region.getId());

                return true;
            }

            if (args[1].equalsIgnoreCase("delete")){
                if (args[2].equalsIgnoreCase("confirm")){
                    region.delete();
                    plugin.regions.remove(region);
                    int refund = region.getArea();
                    plugin.claimBlocks.put(player.getUniqueId(), plugin.claimBlocks.get(player.getUniqueId()) + refund);
                    player.sendMessage(ChatColor.GREEN + "Deleted your claim");
                    player.sendMessage(ChatColor.YELLOW + "You have been refunded " + refund + " claim-blocks");
                    return true;
                }else player.sendMessage(ChatColor.RED + "You must type 'confirm' as your third argument to confirm your deletion!");
            }

        }else sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        return false;
    }

    private Player getPlayer(String name){
        try {
            return Bukkit.getPlayerExact(name);
        }catch (Exception e){
            plugin.getLogger().warning("Attempted to get player " + name + ", but player doesn't exist!");
        }
        return null;
    }

    private ClaimedRegion fromString(UUID owner, String id){
        for (ClaimedRegion region : plugin.regions){
            if (region.getOwner().equals(owner) && region.getId().equalsIgnoreCase(id)) return region;
        }
        return null;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player) {
            int size = args.length;
            if (size == 1) return currentRegions(player.getUniqueId(), args[0]);
            if (size == 2) return subCmds(args[1]);
            if (size == 3) {
                if (args[1].equalsIgnoreCase("trust")) return null;
                else if (args[1].equalsIgnoreCase("setDenyMsg")) return yourMsg();
                else if(args[1].equalsIgnoreCase("untrust")){
                    ClaimedRegion region = fromString(player.getUniqueId(), args[0]);
                    if (region != null) return currentTrusted(region, args[2]);
                }else return booleans(args[2]);
            }
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase();
        for (String entry : base){
            String temp = entry.toLowerCase();
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    private List<String> currentRegions(UUID uuid, String match){
        List<String> regionIds = new ArrayList<>();
        for (ClaimedRegion region : plugin.regions){
            if (region.getOwner().equals(uuid)) regionIds.add(region.getId());
        }
        return filter(regionIds, match);
    }
    private List<String> subCmds(String match){
        List<String> subs = new ArrayList<>();
        subs.add("showBorder");
        subs.add("allowExplosions");
        subs.add("allowLiquidFlow");
        subs.add("allowInteraction");
        subs.add("allowEntityInteraction");
        subs.add("setDenyMsg");
        subs.add("delete");
        subs.add("trust");
        subs.add("untrust");
        return filter(subs, match);
    }
    private List<String> booleans(String match){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return filter(bools, match);
    }
    private List<String> currentTrusted(ClaimedRegion region, String match){
        List<String> current = new ArrayList<>();
        for (UUID uuid : region.getTrusted()){
            current.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return filter(current, match);
    }
    private List<String> yourMsg(){
        List<String> out = new ArrayList<>();
        out.add("[your-msg-here]");
        return out;
    }
}
