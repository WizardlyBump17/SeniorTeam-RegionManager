package com.wizardlybump17.seniorteam.regionmanager.listener;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public record RegionListener(RegionManager plugin) implements Listener {

    private boolean testRegion(RegionFlagType type, Location location, Player player) {
        for (Region region : plugin.getRegionCache().get(location)) {
            RegionFlag flag = region.getFlag(type);
            if (region.test(flag, player))
                continue;

            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        event.setCancelled(!testRegion(RegionFlagTypes.BREAK_BLOCK, event.getBlock().getLocation(), event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event) {
        event.setCancelled(!testRegion(RegionFlagTypes.PLACE_BLOCK, event.getBlock().getLocation(), event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY && event.useInteractedBlock() == Event.Result.DENY)
            return;

        Player player = event.getPlayer();
        RegionFlagType type;
        Location location;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                type = RegionFlagTypes.LEFT_CLICK_BLOCK;
                location = event.getClickedBlock().getLocation();
            }
            case RIGHT_CLICK_BLOCK -> {
                type = RegionFlagTypes.RIGHT_CLICK_BLOCK;
                location = event.getClickedBlock().getLocation();
            }
            case LEFT_CLICK_AIR -> {
                type = RegionFlagTypes.LEFT_CLICK_AIR;
                location = player.getLocation();
            }
            case RIGHT_CLICK_AIR -> {
                type = RegionFlagTypes.RIGHT_CLICK_AIR;
                location = player.getLocation();
            }
            case PHYSICAL -> {
                type = RegionFlagTypes.INTERACT_PHYSICAL;
                location = event.getClickedBlock().getLocation();
            }
            default -> {
                type = null;
                location = null;
            }
        }

        if (type != null)
            event.setCancelled(!testRegion(type, location, player));
    }
}
