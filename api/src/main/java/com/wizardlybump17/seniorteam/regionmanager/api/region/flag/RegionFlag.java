package com.wizardlybump17.seniorteam.regionmanager.api.region.flag;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.util.BukkitStreamsUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Data
@AllArgsConstructor
public class RegionFlag {

    private final String name;
    private final RegionFlagType<?> type;
    private RegionFlagValue<?> value;

    public static RegionFlag load(ResultSet set, RegionFlagTypeCache typeCache) throws SQLException {
        Optional<RegionFlagType<?>> typeOptional = typeCache.get(set.getString("type"));
        if (typeOptional.isEmpty())
            return null;

        return new RegionFlag(
                set.getString("name"),
                typeOptional.get(),
                (RegionFlagValue<?>) BukkitStreamsUtil.deserialize(set.getBytes("value"))
        );
    }
}
