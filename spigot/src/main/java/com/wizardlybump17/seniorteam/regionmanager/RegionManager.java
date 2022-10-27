package com.wizardlybump17.seniorteam.regionmanager;

import com.wizardlybump17.seniorteam.regionmanager.listener.RegionListener;
import org.bukkit.Bukkit;

public class RegionManager extends com.wizardlybump17.seniorteam.regionmanager.api.RegionManager {

    @Override
    public void onEnable() {
        super.onEnable();

        Bukkit.getPluginManager().registerEvents(new RegionListener(this), this);
    }
}
