package com.wizardlybump17.seniorteam.regionmanager.api.region.flag;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionFlag {

    private final String name;
    private final RegionFlagType<?> type;
    private RegionFlagValue<?> value;
}
