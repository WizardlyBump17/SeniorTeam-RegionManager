package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import org.bukkit.entity.Player;

public interface RegionFlagType<T extends RegionFlagValue<?>> {

    String getName();

    boolean isValidValue(RegionFlagValue<?> value);

    boolean test(T value, Player player);
}
