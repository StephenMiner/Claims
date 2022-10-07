package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class RegionInteraction implements Listener {
    private final Claims plugin;
    public RegionInteraction(Claims plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void cancelInteraction(PlayerInteractEvent event){
        if (!event.hasBlock()) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Material mat = block.getType();
        for (ClaimedRegion region : plugin.regions) {
            if (region.isInRegion(block.getLocation())){
                if (!(region.getOwner().equals(player.getUniqueId()) ||
                        region.getTrusted().contains(player.getUniqueId()))){
                    if (player.hasPermission("claims.regions.override")){
                        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NOTE: YOU ARE EDITING THE REGION " + region.getId() + " BELONGING TO " + Bukkit.getOfflinePlayer(region.getOwner()).getName());
                        player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                        return;
                    }
                    if (blacklist().contains(mat) || !region.interactionAllowed()) {
                        event.setCancelled(true);
                        if (!region.getDenyMessage().isEmpty()) player.sendMessage(ChatColor.RED + region.getDenyMessage());
                        return;
                    }
                }
            }
        }

    }


    @EventHandler
    public void cancelBreaking(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(block.getLocation())){
                boolean cancel = !region.tryBreak(player);
                if (cancel){
                    if (player.hasPermission("claims.regions.override")){
                        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NOTE: YOU ARE EDITING THE REGION " + region.getId() + " BELONGING TO " + Bukkit.getOfflinePlayer(region.getOwner()).getName());
                        player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                        return;
                    }
                    event.setCancelled(true);
                    if (!region.getDenyMessage().isEmpty()) player.sendMessage(ChatColor.RED + region.getDenyMessage());
                }
            }
        }
    }

    @EventHandler
    public void cancelPlacement(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(block.getLocation())){
                boolean cancel = !region.tryBreak(player);
                if (cancel) {
                    if (player.hasPermission("claims.regions.override")) {
                        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NOTE: YOU ARE EDITING THE REGION " + region.getId() + " BELONGING TO " + Bukkit.getOfflinePlayer(region.getOwner()).getName());
                        player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                        return;
                    }
                    event.setCancelled(true);
                    if (!region.getDenyMessage().isEmpty()) player.sendMessage(ChatColor.RED + region.getDenyMessage());
                }
            }
        }
    }
    @EventHandler
    public void cancelExplosion(EntityExplodeEvent event){
        for (int i = event.blockList().size() - 1; i >= 0; i--){
            Block block = event.blockList().get(i);
            for (ClaimedRegion region : plugin.regions){
                if (region.explosionsAllowed()) continue;
                if (region.isInRegion(block.getLocation())) {
                    event.blockList().remove(i);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void cancelBlockSpread(BlockFromToEvent event){
        Block from = event.getBlock();
        Block to = event.getToBlock();
        for (ClaimedRegion region : plugin.regions){
            if (!region.isInRegion(from.getLocation()) && region.isInRegion(to.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void cancelFire(BlockIgniteEvent event){
        Block block = event.getBlock();
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(block.getLocation())){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void cancelBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(block.getLocation())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void cancelArmorStand(PlayerArmorStandManipulateEvent event){
        Entity clicked = event.getRightClicked();
        Player player = event.getPlayer();
        for (ClaimedRegion region : plugin.regions){
            if (region.isInRegion(clicked.getLocation().clone().add(0,1,0)) && !region.entityInteractionAllowed()){
                boolean cancel = !region.tryBreak(player);
                if (cancel) {
                    if (player.hasPermission("claims.regions.override")) {
                        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NOTE: YOU ARE EDITING THE REGION " + region.getId() + " BELONGING TO " + Bukkit.getOfflinePlayer(region.getOwner()).getName());
                        player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                        return;
                    }
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot edit armorstands!");

                }
            }
        }
    }

    @EventHandler
    public void cancelKillEntity(EntityDamageByEntityEvent event){
        Player player = null;
        if (event.getDamager() instanceof Player) player = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player) player = (Player) proj.getShooter();
        if (player != null){
            for (ClaimedRegion region : plugin.regions){
                if ((!region.getOwner().equals(player.getUniqueId()) || !region.getTrusted().contains(player.getUniqueId())) && region.isInRegion(event.getEntity().getLocation())){
                    if (player.hasPermission("claims.regions.override")) {
                        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NOTE: YOU ARE EDITING THE REGION " + region.getId() + " BELONGING TO " + Bukkit.getOfflinePlayer(region.getOwner()).getName());
                        player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                        return;
                    }
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to hurt entities in this region!");
                    return;
                }
            }
        }
    }


    private Set<Material> blacklist(){
        Set<Material> blacklisted = new HashSet<>();
        blacklisted.add(Material.CHEST);
        blacklisted.add(Material.BARREL);
        blacklisted.add(Material.ITEM_FRAME);
        blacklisted.add(Material.GLOW_ITEM_FRAME);
        blacklisted.add(Material.TRAPPED_CHEST);
        blacklisted.add(Material.SHULKER_BOX);
        blacklisted.add(Material.BLACK_SHULKER_BOX);
        blacklisted.add(Material.WHITE_SHULKER_BOX);
        blacklisted.add(Material.BLUE_SHULKER_BOX);
        blacklisted.add(Material.RED_SHULKER_BOX);
        blacklisted.add(Material.GREEN_SHULKER_BOX);
        blacklisted.add(Material.ORANGE_SHULKER_BOX);
        blacklisted.add(Material.YELLOW_SHULKER_BOX);
        blacklisted.add(Material.PURPLE_SHULKER_BOX);
        blacklisted.add(Material.MAGENTA_SHULKER_BOX);
        blacklisted.add(Material.PINK_SHULKER_BOX);
        blacklisted.add(Material.CYAN_SHULKER_BOX);
        blacklisted.add(Material.LIGHT_BLUE_SHULKER_BOX);
        blacklisted.add(Material.LIME_SHULKER_BOX);
        blacklisted.add(Material.LIGHT_GRAY_SHULKER_BOX);
        blacklisted.add(Material.GRAY_SHULKER_BOX);
        blacklisted.add(Material.BROWN_SHULKER_BOX);
        blacklisted.add(Material.FURNACE);
        blacklisted.add(Material.CHEST_MINECART);
        blacklisted.add(Material.SMOKER);
        blacklisted.add(Material.BLAST_FURNACE);
        blacklisted.add(Material.FARMLAND);
        return blacklisted;
    }
}
