package me.stephenminer.claims;

import me.stephenminer.claims.commands.*;
import me.stephenminer.claims.regions.*;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Claims extends JavaPlugin {
    public List<ClaimedRegion> regions;
    public LinkedBag<ClaimBlockTimer> timers;
    public HashMap<UUID, Long> claimBlocks;

    public ConfigFile regionFile;
    public ConfigFile settings;
    @Override
    public void onEnable() {
        regions = new ArrayList<>();
        claimBlocks = new HashMap<>();
        regionFile = new ConfigFile(this, "regions");
        settings = new ConfigFile(this, "settings");
        timers = new LinkedBag<>();
        registerCommands();
        registerEvents();
        loadRegions();
        loadClaimBlocks();
    }

    @Override
    public void onDisable() {
        getLogger().info("----------------------------");
        getLogger().info("SAVING REGIONS");
        for (ClaimedRegion region : regions){
            region.save();
        }
        LinkedBag<ClaimBlockTimer>.Node current = timers.iterate();
        while (current != null){
            current.data().save();
            current = current.next();
        }
        getLogger().info("SAVED REGIONS");
        getLogger().info("----------------------------");
    }

    private void registerCommands(){
        getCommand("giveWand").setExecutor(new GiveShovel(this));
        getCommand("claimShovel").setExecutor(new ClaimShovel(this));
        getCommand("claimBlocks").setExecutor(new ClaimBlocksCmd(this));
        getCommand("claimsReload").setExecutor(new ClaimsReload(this));
        ClaimCmd claimCmd = new ClaimCmd(this);
        getCommand("claims").setExecutor(claimCmd);
        getCommand("claims").setTabCompleter(claimCmd);
        ForceDelete deleteCmd = new ForceDelete(this);
        getCommand("forceDelete").setExecutor(deleteCmd);
        getCommand("forceDelete").setTabCompleter(deleteCmd);
    }

    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new RegionInteraction(this), this);
        pm.registerEvents(new RegionCreation(this), this);
        pm.registerEvents(new TimerHandler(this), this);
    }

    public String fromBlockLoc(Location loc){
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getEnvironment().name();
    }

    public Location fromString(String str){
        String[] unbox = str.split(",");
        String worldName = unbox[0];
        World world = null;
        String env = unbox[4];
        World.Environment environment = World.Environment.valueOf(env);
        try{
            world = Bukkit.getWorld(worldName);
        }catch (Exception e){
            e.printStackTrace();
            getLogger().warning("Attempted to load world " + unbox[0] + ", but received null. Now attempting to force load the world. (May create new world)");
        }
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(unbox[0]).environment(environment));
            getLogger().warning("Loading world");
        }
        int x = Integer.parseInt(unbox[1]);
        int y= Integer.parseInt(unbox[2]);
        int z = Integer.parseInt(unbox[3]);

        return new Location(world, x, y, z);
    }

    public ItemStack wand(){
        ItemStack item = new ItemStack(Material.GOLDEN_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Jesse! We need to claim!");
        lore.add(ChatColor.YELLOW + "Left click to set your first corner");
        lore.add(ChatColor.YELLOW + "Right click to set the other");
        lore.add(ChatColor.BLACK + "claimwand");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    public void loadRegions(){
        if (regionFile.getConfig().contains("regions")){
            Set<String> playerIds = regionFile.getConfig().getConfigurationSection("regions").getKeys(false);
            for (String id : playerIds){
                UUID uuid = UUID.fromString(id);
                Set<String> ownedClaimIds = regionFile.getConfig().getConfigurationSection("regions." + id).getKeys(false);
                for (String claimId : ownedClaimIds){
                    ClaimedRegion claimedRegion = new RegionBuilder(this, uuid, claimId).build();
                    regions.add(claimedRegion);
                }
            }
        }
    }
    public void loadClaimBlocks(){
        if (regionFile.getConfig().contains("players")){
            Set<String> playerIds = regionFile.getConfig().getConfigurationSection("players").getKeys(false);
            for (String id : playerIds){
                UUID uuid = UUID.fromString(id);
                long claimblocks = regionFile.getConfig().getLong("players." + id + ".claim-blocks");
                claimBlocks.put(uuid, claimblocks);
            }
        }
    }

}
