package com.wizardlybump17.seniorteam.regionmanager.api.cache;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.wlib.object.Cache;
import com.wizardlybump17.wlib.object.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RegionFlagTypeCache extends Cache<String, RegionFlagType, RegionFlagType> {

    @Override
    public @NotNull Pair<String, RegionFlagType> apply(RegionFlagType type) {
        return new Pair<>(type.getName().toLowerCase(), type);
    }

    @Override
    public @NotNull Optional<RegionFlagType> get(@Nullable String key) {
        return super.get(key == null ? null : key.toLowerCase());
    }

    @Override
    public boolean has(@Nullable String key) {
        return super.has(key == null ? null : key.toLowerCase());
    }

    @Override
    public Optional<RegionFlagType> remove(@Nullable String key) {
        return super.remove(key == null ? null : key.toLowerCase());
    }

    @Override
    protected @NotNull Map<String, RegionFlagType> getInitialMap() {
        return new LinkedHashMap<>();
    }
}
