package com.wizardlybump17.seniorteam.regionmanager.api.region.flag;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.util.BukkitStreamsUtil;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.DatabaseStorable;
import lombok.Data;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Data
public class RegionFlag implements DatabaseStorable {

    private final String name;
    private final RegionFlagType<?> type;
    private RegionFlagValue<?> value;
    private final String region;
    private boolean dirty;
    private boolean deleted;
    private boolean inDatabase;

    public RegionFlag(String name, RegionFlagType<?> type, RegionFlagValue<?> value, String region) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.region = region;
    }

    public void setValue(RegionFlagValue<?> value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public void saveToDatabase(Map<String, Object> data) {
        data.put("name", name);
        data.put("type", type.getName());
        data.put("value", BukkitStreamsUtil.serialize(value));
        data.put("region", region);
    }

    @Override
    public void updateToDatabase(Map<String, Object> where, Map<String, Object> data) {
        where.put("name", name);
        data.put("value", BukkitStreamsUtil.serialize(value));
    }

    @Override
    public void deleteFromDatabase(Map<String, Object> data) {
        data.put("name", name);
    }

    public boolean test(Player player) {
        return type.test(value.getValue(), player);
    }

    public static RegionFlag load(ResultSet set, RegionFlagTypeCache typeCache) throws SQLException {
        Optional<RegionFlagType<?>> typeOptional = typeCache.get(set.getString("type"));
        if (typeOptional.isEmpty())
            return null;

        return new RegionFlag(
                set.getString("name"),
                typeOptional.get(),
                (RegionFlagValue<?>) BukkitStreamsUtil.deserialize(set.getBytes("value")),
                set.getString("region")
        );
    }

    public static void setupDatabase(Database<?> database) {
        database.update("CREATE TABLE IF NOT EXISTS flag (" +
                "name VARCHAR(255) PRIMARY KEY NOT NULL, " +
                "type VARCHAR(255) NOT NULL, " +
                "value BLOB NOT NULL, " +
                "region VARCHAR(255) NOT NULL" + //foreign key
                ");"
        );
    }
}
