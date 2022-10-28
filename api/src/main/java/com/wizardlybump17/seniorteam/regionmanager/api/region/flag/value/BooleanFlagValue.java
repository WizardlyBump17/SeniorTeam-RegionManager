package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value;

import lombok.Data;

@Data
public class BooleanFlagValue implements RegionFlagValue<Boolean> {

    public static final BooleanFlagValue TRUE = new BooleanFlagValue(true);
    public static final BooleanFlagValue FALSE = new BooleanFlagValue(false);

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
