package com.wizardlybump17.seniorteam.regionmanager.util;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PlayerUtil {

    @SuppressWarnings("unchecked")
    public static void markPosition(Player player, Location location, String type) {
        Map<String, Location> map;
        if (player.getMetadata("RegionManager").isEmpty())
            map = new HashMap<>();
        else
            map = (Map<String, Location>) player.getMetadata("RegionManager").get(0).value();
        map.put(type, location);
        player.setMetadata("RegionManager", new FixedMetadataValue(RegionManager.getInstance(), map));
    }

    @SuppressWarnings("unchecked")
    public static Location[] getMarkedPositions(Player player) {
        Map<String, Location> map;
        if (player.getMetadata("RegionManager").isEmpty())
            map = new HashMap<>();
        else
            map = (Map<String, Location>) player.getMetadata("RegionManager").get(0).value();
        return new Location[] {map.get("Pos1"), map.get("Pos2")};
    }
}
