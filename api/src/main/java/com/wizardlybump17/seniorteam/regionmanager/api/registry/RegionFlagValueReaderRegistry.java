package com.wizardlybump17.seniorteam.regionmanager.api.registry;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.reader.RegionFlagValueReader;
import com.wizardlybump17.wlib.object.Registry;

public class RegionFlagValueReaderRegistry extends Registry<Class<?>, RegionFlagValueReader<?>> {

    public void add(RegionFlagValueReader<?> reader) {
        put(reader.getType(), reader);
    }

    @SuppressWarnings("unchecked")
    public <T extends RegionFlagValue<?>> T read(String string) {
        for (RegionFlagValueReader<?> reader : getMap().values())
            if (reader.read(string) != null)
                return (T) reader.read(string);
        return null;
    }
}
