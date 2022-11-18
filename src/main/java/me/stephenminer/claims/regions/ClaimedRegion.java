package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ClaimedRegion {
    private Location loc1;
    private Location loc2;
    private final UUID owner;
    private final Claims plugin;
    private List<UUID> trusted;
    private final String id;

    //As the name suggests explosions refers to ALL explosions
    private boolean allowExplosions;
    //FlowIO refers to liquids flowing INTO and OUT of regions (on by default)
    private boolean allowFlowIO;
    //Interaction refers to using doors, buttons, crafting tables, etc. NOT blocks with inventories!
    private boolean allowInteraction;
    private boolean allowEntityInteraction;
    private String denyMessage;
    private boolean showing;



    public ClaimedRegion(Player owner, String id, Location loc1, Location loc2){
        this(owner.getUniqueId(), id, loc1, loc2);
    }
    public ClaimedRegion(UUID owner, String id, Location loc1, Location loc2){
        this.owner = owner;
        this.id = id;
        loc1.setY(loc1.getWorld().getMinHeight());
        loc2.setY(loc2.getWorld().getMaxHeight()-1);
        this.loc1 = loc1.clone().add(0.5,0.5,0.5);
        this.loc2 = loc2.clone().add(0.5,0.5,0.5);
        this.plugin = Claims.getPlugin(Claims.class);
        denyMessage = "You cannot interact with this region here!";
        allowExplosions = false;
        allowEntityInteraction = false;
        trusted = new ArrayList<>();
    }


    public boolean tryBreak(Player player){
        return owner.equals(player.getUniqueId()) || trusted.contains(player.getUniqueId());
    }


    public BoundingBox getBounds(){
        return BoundingBox.of(loc1, loc2);
    }

    public boolean isInRegion(Location loc){
        Vector blockVec = loc.getBlock().getLocation().toVector();
        Vector max = blockVec.clone().add(new Vector(1,1,1));
        return getBounds().overlaps(blockVec, max);
    }

    public void save(){
        String base = "regions." + owner.toString() + "." + id;
        plugin.regionFile.getConfig().set(base + ".pos1", plugin.fromBlockLoc(loc1));
        plugin.regionFile.getConfig().set(base + ".pos2", plugin.fromBlockLoc(loc2));
        plugin.regionFile.getConfig().set(base + ".deny-message", denyMessage);
        plugin.regionFile.getConfig().set(base + ".explosions", allowExplosions);
        plugin.regionFile.getConfig().set(base + ".liquids", allowFlowIO);
        plugin.regionFile.getConfig().set(base + ".interaction", allowInteraction);
        plugin.regionFile.getConfig().set(base + ".entity-interaction", allowEntityInteraction);
        List<String> trustedToString = new ArrayList<>();
        for (UUID uuid : trusted){
            trustedToString.add(uuid.toString());
        }
        plugin.regionFile.getConfig().set(base + ".trusted", trustedToString);
        plugin.regionFile.saveConfig();
    }
    public void showBorder(){
        World world = loc1.getWorld();
        Vector fp = loc1.toVector();
        Vector sp = loc2.toVector();
        Vector max = Vector.getMaximum(fp, sp);
        Vector min = Vector.getMinimum(fp, sp);
        Set<Location> locSet = new HashSet<>();
        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
            for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++) {
                locSet.add(new Location(world, x, y, min.getBlockZ() - 1));
                locSet.add(new Location(world, x, y, max.getBlockZ() + 1));
            }
            for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++) {
                locSet.add(new Location(world,min.getBlockX() - 1, y, z));
                locSet.add(new Location(world,max.getBlockX() + 1, y, z));
            }
        }
        new BukkitRunnable(){
            final int max = 60*60*20;
            int count = 0;
            @Override
            public void run(){
                if (!showing || count >= max) this.cancel();
                for (Location loc : locSet){
                    Location l = loc.clone().add(0.5,0.5,0.5);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, l, 0);
                }
                count++;
            }
        }.runTaskTimer(plugin, 1, 20);

    }
    /**
     * Sets the deny-message for the claim
     * @param msg [name] will be replaced with region name
     */
    public void setDenyMessage(String msg){
        denyMessage = msg.replace("[name]", id);
    }
    public String getDenyMessage(){
        return denyMessage;
    }

    public UUID getOwner(){
        return owner;
    }

    public Location getLoc1(){
        return loc1;
    }
    public Location getLoc2(){
        return loc2;
    }
    public String getId(){
        return id;
    }

    public void addTrustedPlayer(Player player){
        if (trusted.contains(player.getUniqueId())) return;
        trusted.add(player.getUniqueId());
    }
    public void removeTrustedPlayer(OfflinePlayer player){
        trusted.remove(player.getUniqueId());
    }
    public List<UUID> getTrusted(){
        return trusted;
    }
    public void setTrustedPlayers(List<UUID> playerIds){
        this.trusted = playerIds;
    }

    public boolean flowIOAllowed(){ return allowFlowIO; }
    public boolean explosionsAllowed(){ return allowExplosions; }
    public boolean interactionAllowed(){ return allowInteraction; }
    public boolean isShowing(){
        return showing;
    }
    public boolean entityInteractionAllowed(){ return allowEntityInteraction; }

    public void setAllowFlowIO(boolean allow){
        this.allowFlowIO = allow;
    }
    public void setAllowExplosions(boolean allow){
        this.allowExplosions = allow;
    }
    public void setAllowInteraction(boolean allow){
        this.allowInteraction = allow;
    }
    public void setShowing(boolean showing){
        this.showing = showing;
    }
    public void setAllowEntityInteraction(boolean allow){
        this.allowEntityInteraction = allow;
    }
    public void setLoc1(Location loc1){
        loc1.setY(loc1.getWorld().getMinHeight());
        this.loc1 = loc1.clone().add(0.5,0.5,0.5);
    }
    public void setLoc2(Location loc2){
        loc2.setY(loc2.getWorld().getMaxHeight()-1);
        this.loc2 = loc2.clone().add(0.5,0.5,0.5);
    }

    public int getArea(){
        int maxx = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minx = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        return (1 + maxx - minx) * (1 + maxz - minz);
    }

    public void delete(){
        showing = false;
        plugin.regionFile.getConfig().set("regions." + owner + "." + id, null);
        plugin.regionFile.saveConfig();
    }




}
