package com.wizardlybump17.seniorteam.regionmanager.api;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class RegionManager extends JavaPlugin {

    public static RegionManager getInstance() {
        return getPlugin(RegionManager.class);
    }
}
