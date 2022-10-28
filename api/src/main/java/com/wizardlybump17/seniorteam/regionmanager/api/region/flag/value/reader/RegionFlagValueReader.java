package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.reader;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;

public abstract class RegionFlagValueReader<T extends RegionFlagValue<?>> {

    public abstract Class<T> getType();

    public abstract T read(String string);
}
