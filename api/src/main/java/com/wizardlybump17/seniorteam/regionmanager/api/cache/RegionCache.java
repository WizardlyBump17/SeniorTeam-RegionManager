package com.wizardlybump17.seniorteam.regionmanager.api.cache;

import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.object.Cache;
import com.wizardlybump17.wlib.object.Pair;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegionCache extends Cache<String, Region, Region> {

    @Override
    public @NotNull Pair<String, Region> apply(Region region) {
        return new Pair<>(region.getName().toLowerCase(), region);
    }

    @Override
    public @NotNull Optional<Region> get(@Nullable String key) {
        return super.get(key == null ? null : key.toLowerCase());
    }

    public List<Region> get(Location location) {
        List<Region> regions = new ArrayList<>();
        for (Region region : getAll())
            if (region.isInside(location))
                regions.add(region);
        return regions;
    }

    @Override
    public boolean has(@Nullable String key) {
        return super.has(key == null ? null : key.toLowerCase());
    }

    @Override
    public Optional<Region> remove(@Nullable String key) {
        return super.remove(key == null ? null : key.toLowerCase());
    }
}
