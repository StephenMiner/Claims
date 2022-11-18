package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RegionCreation implements Listener {
    private final Claims plugin;
    private final HashMap<UUID, Location> pos1s;
    private final Set<UUID> canName;
    public static final Map<UUID, String> resizing = new HashMap<>();
    private final HashMap<UUID, Location> pos2s;
    public RegionCreation(Claims plugin){
        this.plugin = plugin;
        pos1s = new HashMap<>();
        pos2s = new HashMap<>();
        canName = new HashSet<>();

    }


    @EventHandler
    public void setPosition(PlayerInteractEvent event){
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (!itemIsWand(item)) return;
        if (player.hasCooldown(Material.GOLDEN_SHOVEL)){
            event.setCancelled(true);
            return;
        }
        Location loc;
        switch (event.getAction()){
            case LEFT_CLICK_BLOCK -> {
                loc = event.getClickedBlock().getLocation();
                if (overlapping(loc)) {
                    ClaimedRegion region = getOverlapping(loc);
                    player.sendMessage(ChatColor.RED + "This position is overlapping another region " + region.getId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> region.setShowing(false), 40);
                    break;
                }
                pos1s.put(player.getUniqueId(), loc);
                player.sendMessage(ChatColor.GREEN + "Set first corner, right click to set your second corner if you haven't already!");
            }
            case LEFT_CLICK_AIR -> {
                loc = player.getLocation();
                if (overlapping(loc)) {
                    ClaimedRegion region = getOverlapping(loc);
                    player.sendMessage(ChatColor.RED + "This position is overlapping another region " + region.getId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> region.setShowing(false), 40);
                    break;
                }
                pos1s.put(player.getUniqueId(), loc);
                player.sendMessage(ChatColor.GREEN + "Set first corner, right click to set your second corner if you haven't already!");
            }
            case RIGHT_CLICK_AIR -> {
                loc = player.getLocation();
                if (overlapping(loc)) {
                    ClaimedRegion region = getOverlapping(loc);
                    player.sendMessage(ChatColor.RED + "This position is overlapping another region " + region.getId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> region.setShowing(false), 40);
                    break;
                }
                pos2s.put(player.getUniqueId(), loc);
                player.sendMessage(ChatColor.GREEN + "Set second corner, left click to set your first corner if you haven't already");
            }
            case RIGHT_CLICK_BLOCK -> {
                loc = event.getClickedBlock().getLocation();
                if (overlapping(loc)) {
                    ClaimedRegion region = getOverlapping(loc);
                    player.sendMessage(ChatColor.RED + "This position is overlapping another region " + region.getId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> region.setShowing(false), 40);
                    break;
                }
                pos2s.put(player.getUniqueId(), loc);
                player.sendMessage(ChatColor.GREEN + "Set second corner, left click to set your first corner if you haven't already");
            }
        }
        event.setCancelled(true);
        player.setCooldown(Material.GOLDEN_SHOVEL, 5);
        if (pos1s.containsKey(player.getUniqueId()) && pos2s.containsKey(player.getUniqueId())){
            if (resize(player)){
                player.sendMessage(ChatColor.GREEN + "Redefined your claims borders!");
                return;
            }
            canName.add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Please type in chat what you want the name of your claim to be");
        }
    }


    public boolean resize(Player player){
        UUID uuid = player.getUniqueId();
        if (resizing.containsKey(uuid)){
            String id = resizing.get(uuid);
            ClaimedRegion region = null;
            for (ClaimedRegion entry : plugin.regions){
                if (entry.getId().equalsIgnoreCase(id)){
                    region = entry;
                    break;
                }
            }
            if (region == null){
                player.sendMessage(ChatColor.RED + "Something went wrong! Was the region you were resizing deleted?");
                return false;
            }
            int needed = areaOfRegion(region.getLoc1(), region.getLoc2());
            if (plugin.claimBlocks.containsKey(uuid)){
                long current = plugin.claimBlocks.get(uuid);
                if(current >= needed){
                    plugin.claimBlocks.put(uuid, current - needed);
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "-" + (current-needed) + " claim-blocks");
                }else{
                    player.sendMessage(ChatColor.RED + "You do not have enough claim-blocks saved up to claim this region!");
                    player.sendMessage(ChatColor.AQUA + "Your claim-blocks: " + current);
                    player.sendMessage(ChatColor.AQUA + "Area of region: " + needed);
                    return false;
                }
            }
            final ClaimedRegion claim = region;
            claim.setLoc2(pos2s.get(uuid));
            claim.setLoc1(pos1s.get(uuid));
            claim.setShowing(true);
            claim.showBorder();
            claim.save();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> claim.setShowing(false), 100);
            player.sendMessage(ChatColor.GREEN + "Resized your claim!");
            player.sendMessage(ChatColor.YELLOW + "Now temporarily showing claim-border");
            resizing.remove(uuid);
            pos1s.remove(uuid);
            pos2s.remove(uuid);
        }
        return false;
        /*
        if (!resizing.containsKey(player.getUniqueId())) return false;
        resizing.remove(player.getUniqueId());
        byte posNum = resizing.get(player.getUniqueId());
        ClaimedRegion region = getOverlapping(loc);
        if (posNum == 1) region.setLoc1(loc.getBlock().getLocation());
        else if (posNum == 2) region.setLoc2(loc.getBlock().getLocation());
        else return false;
        int needed = areaOfRegion(region.getLoc1(), region.getLoc2());
        if (plugin.claimBlocks.containsKey(player.getUniqueId())){
            long current = plugin.claimBlocks.get(player.getUniqueId());
            if(current >= needed){
                plugin.claimBlocks.put(player.getUniqueId(), current - needed);
            }else{
                player.sendMessage(ChatColor.RED + "You do not have enough claim-blocks saved up to claim this region!");
                player.sendMessage(ChatColor.AQUA + "Your claim-blocks: " + current);
                player.sendMessage(ChatColor.AQUA + "Area of region: " + needed);
                return false;
            }
        }
        player.sendMessage(ChatColor.GREEN + "Resized your region!");
        player.sendMessage(ChatColor.YELLOW + "Now temporarily showing region border!");
        region.setShowing(true);
        region.save();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> region.setShowing(false), 15*20);
        return true;
         */
    }



    public boolean overlapping(Location location){
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(location)) return true;
        }
        return false;
    }
    public ClaimedRegion getOverlapping(Location loc){
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(loc)) return region;
        }
        return null;
    }


    @EventHandler
    public void cleanLeave(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        pos1s.remove(uuid);
        pos2s.remove(uuid);
        canName.remove(uuid);
        resizing.remove(uuid);
    }

    @EventHandler
    public void createRegion(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = event.getMessage();
        msg = msg.replace(' ', '_');
        if (resizing.containsKey(uuid)){
            if (msg.equalsIgnoreCase("cancel")){
                pos1s.remove(uuid);
                pos2s.remove(uuid);
                player.sendMessage(ChatColor.RED + "No longer resizing claim " + resizing.remove(uuid) + "!");
            }
        }
        if (canName.contains(uuid)) {
            Location pos1 = pos1s.get(uuid);
            Location pos2 = pos2s.get(uuid);

            if (msg.equalsIgnoreCase("cancel")){
                player.sendMessage(ChatColor.RED + "Cancelled region creation!");
                event.setCancelled(true);
                pos1s.remove(uuid);
                pos2s.remove(uuid);
                canName.remove(uuid);
                return;
            }
            int needed = areaOfRegion(pos1, pos2);

            if (plugin.claimBlocks.containsKey(player.getUniqueId())){
                long current = plugin.claimBlocks.get(player.getUniqueId());
                if(current >= needed){
                    plugin.claimBlocks.put(player.getUniqueId(), current - needed);
                }else{
                    player.sendMessage(ChatColor.RED + "You do not have enough claim-blocks saved up to claim this region!");
                    player.sendMessage(ChatColor.AQUA + "Your claim-blocks: " + current);
                    player.sendMessage(ChatColor.AQUA + "Area of region: " + needed);
                    return;
                }
            }else return;
            ClaimedRegion region = new ClaimedRegion(player, msg, pos1, pos2);
            plugin.regions.add(region);
            player.sendMessage(ChatColor.GREEN + "Created your claim named " + msg + "!");
            player.sendMessage(ChatColor.YELLOW + "If you want to show the bounderies of this claim, use /showclaim [claim-name]");
            pos1s.remove(uuid);
            pos2s.remove(uuid);
            canName.remove(uuid);
            resizing.remove(uuid);
            region.save();
            event.setCancelled(true);
        }
    }

    private boolean itemIsWand(ItemStack item){
        if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getType() == Material.GOLDEN_SHOVEL){
            List<String> lore = item.getItemMeta().getLore();
            for (String entry : lore){
                String temp = ChatColor.stripColor(entry).toLowerCase();
                if (temp.equalsIgnoreCase("claimwand")) return true;
            }
        }
        return false;
    }

    private int areaOfRegion(Location loc1, Location loc2){
        int maxx = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minx = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        return (1 + maxx - minx) * (1 + maxz - minz);
    }
}
