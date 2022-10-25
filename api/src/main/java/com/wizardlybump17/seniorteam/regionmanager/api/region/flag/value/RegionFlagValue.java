package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value;

public interface RegionFlagValue<T> {

    Class<T> getType();

    T getValue();
}
