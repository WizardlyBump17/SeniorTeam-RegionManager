package com.wizardlybump17.seniorteam.regionmanager.api;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagTypes;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class RegionManager extends JavaPlugin {

    private final RegionFlagTypeCache regionFlagTypeCache = new RegionFlagTypeCache();
    private final RegionCache regionCache = new RegionCache();

    @Override
    public void onLoad() {
        initFlagTypes();
    }

    private void initFlagTypes() {
        regionFlagTypeCache.add(RegionFlagTypes.BREAK_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.PLACE_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.LEFT_CLICK_AIR);
        regionFlagTypeCache.add(RegionFlagTypes.LEFT_CLICK_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.RIGHT_CLICK_AIR);
        regionFlagTypeCache.add(RegionFlagTypes.RIGHT_CLICK_BLOCK);
    }

    public static RegionManager getInstance() {
        return getPlugin(RegionManager.class);
    }
}
