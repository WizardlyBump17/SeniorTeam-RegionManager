package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface RegionFlagValue<T> extends ConfigurationSerializable {

    Class<T> getType();

    T getValue();

    @NotNull
    @Override
    default Map<String, Object> serialize() {
        return Map.of("value", getValue());
    }
}
