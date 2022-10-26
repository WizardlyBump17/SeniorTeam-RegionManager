package com.wizardlybump17.seniorteam.regionmanager.api.cache;

import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.object.Cache;
import com.wizardlybump17.wlib.object.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public boolean has(@Nullable String key) {
        return super.has(key == null ? null : key.toLowerCase());
    }

    @Override
    public Optional<Region> remove(@Nullable String key) {
        return super.remove(key == null ? null : key.toLowerCase());
    }
}
