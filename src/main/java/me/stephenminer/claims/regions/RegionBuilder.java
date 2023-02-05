package me.stephenminer.claims.regions;

import me.stephenminer.claims.Claims;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionBuilder {
    private final Claims plugin;
    private final String base;
    private final UUID owner;
    private final String id;
    private Location pos1;
    private Location pos2;
    private String denyMsg;
    private boolean allowInteraction;
    private boolean allowExplosion;
    private boolean allowLiquidIO;
    private boolean allowEntityInteraction;
    private boolean trustAll;

    private List<UUID> trusted;
    public RegionBuilder(Claims plugin, UUID owner, String id){
        base = "regions." + owner.toString() + "." + id;
        this.plugin = plugin;
        this.owner = owner;
        this.id = id;
        loadBooleans();
        loadPositions();
        loadDenyMsg();
        loadTrusted();
    }

    private void loadBooleans(){
        allowExplosion = plugin.regionFile.getConfig().getBoolean(base + ".explosions");
        allowInteraction = plugin.regionFile.getConfig().getBoolean(base + ".interaction");
        allowLiquidIO = plugin.regionFile.getConfig().getBoolean(base + ".liquids");
        allowEntityInteraction = plugin.regionFile.getConfig().getBoolean(base + ".entity-interaction");
        trustAll = plugin.regionFile.getConfig().getBoolean(base + ".trust-all");
    }

    private void loadPositions(){
        pos1 = plugin.fromString(plugin.regionFile.getConfig().getString(base + ".pos1"));
        pos2 = plugin.fromString(plugin.regionFile.getConfig().getString(base + ".pos2"));
    }
    private void loadDenyMsg(){
        denyMsg = plugin.regionFile.getConfig().getString(base + ".deny-message");
    }

    private void loadTrusted(){
        trusted = new ArrayList<>();
        List<String> uuidsAsStrings = plugin.regionFile.getConfig().getStringList(base + ".trusted");
        for (String uuidString : uuidsAsStrings){
            trusted.add(UUID.fromString(uuidString));
        }
    }

    public ClaimedRegion build(){
        ClaimedRegion region = new ClaimedRegion(owner, id, pos1, pos2);
        region.setDenyMessage(denyMsg);
        region.setAllowExplosions(allowExplosion);
        region.setAllowFlowIO(allowLiquidIO);
        region.setAllowInteraction(allowInteraction);
        region.setTrustedPlayers(trusted);
        region.setAllowEntityInteraction(allowEntityInteraction);
        region.setAllTrusted(trustAll);
        return region;
    }
}
