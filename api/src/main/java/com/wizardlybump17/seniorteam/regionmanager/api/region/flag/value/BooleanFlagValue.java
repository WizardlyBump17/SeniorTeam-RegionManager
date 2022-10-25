package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value;

import lombok.Data;

@Data
public class BooleanFlagValue implements RegionFlagValue<Boolean> {

    private final boolean value;

    @Override
    public Class<Boolean> getType() {
        return boolean.class;
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
