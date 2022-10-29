package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.reader;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.BooleanFlagValue;

public class BooleanFlagValueReader extends RegionFlagValueReader<BooleanFlagValue> {

    @Override
    public Class<BooleanFlagValue> getType() {
        return BooleanFlagValue.class;
    }

    @Override
    public BooleanFlagValue read(String string) {
        if (string.equalsIgnoreCase("true"))
            return new BooleanFlagValue(true);
        if (string.equalsIgnoreCase("false"))
            return new BooleanFlagValue(false);
        return null;
    }
}
