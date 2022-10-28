package com.wizardlybump17.seniorteam.regionmanager.command.reader;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.registry.RegionFlagValueReaderRegistry;
import com.wizardlybump17.wlib.command.args.reader.ArgsReader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegionFlagValueArgsReader extends ArgsReader<RegionFlagValue<?>> {

    private final RegionFlagValueReaderRegistry registry;

    @SuppressWarnings("unchecked")
    @Override
    public Class<RegionFlagValue<?>> getType() {
        return (Class<RegionFlagValue<?>>) getType0();
    }

    @Override
    public RegionFlagValue<?> read(String s) {
        return registry.read(s);
    }

    private Object getType0() {
        return RegionFlagValue.class;
    }
}
