package com.wizardlybump17.seniorteam.regionmanager.command.reader;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.wlib.command.args.reader.ArgsReader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegionFlagTypeArgsReader extends ArgsReader<RegionFlagType> {

    private final RegionFlagTypeCache cache;

    @Override
    public Class<RegionFlagType> getType() {
        return RegionFlagType.class;
    }

    @Override
    public RegionFlagType read(String s) {
        return cache.get(s).orElse(null);
    }
}
