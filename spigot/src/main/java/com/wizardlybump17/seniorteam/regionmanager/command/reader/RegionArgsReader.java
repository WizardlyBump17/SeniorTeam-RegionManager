package com.wizardlybump17.seniorteam.regionmanager.command.reader;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.command.args.reader.ArgsReader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegionArgsReader extends ArgsReader<Region> {

    private final RegionCache cache;

    @Override
    public Class<Region> getType() {
        return Region.class;
    }

    @Override
    public Region read(String s) {
        return cache.get(s).orElse(null);
    }
}
