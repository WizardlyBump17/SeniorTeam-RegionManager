package com.wizardlybump17.seniorteam.regionmanager.api.region.flag;

import com.wizardlybump17.seniorteam.regionmanager.api.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.DatabaseStorable;
import com.wizardlybump17.wlib.util.bukkit.BukkitStreamsUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Data
public class RegionFlag implements DatabaseStorable {

    private final int id;
    private final RegionFlagType type;
    private RegionFlagValue<?> value;
    private String region;
    private boolean dirty;
    private boolean deleted;
    private boolean inDatabase;

    public RegionFlag(int id, RegionFlagType type, RegionFlagValue<?> value, String region) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.region = region;
    }

    public void setRegion(String region) {
        this.region = region;
        dirty = true;
    }

    public void setValue(RegionFlagValue<?> value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public void saveToDatabase(Map<String, Object> data) {
        data.put("id", id);
        data.put("type", type.getName());
        data.put("value", BukkitStreamsUtil.serialize(value));
        data.put("region_name", region);
    }

    @Override
    public void updateToDatabase(Map<String, Object> where, Map<String, Object> data) {
        where.put("id", id);
        data.put("value", BukkitStreamsUtil.serialize(value));
        data.put("region_name", region);
    }

    @Override
    public void deleteFromDatabase(Map<String, Object> data) {
        data.put("id", id);
    }

    public boolean test(Player player) {
        return type.test(value, player);
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(RegionManager.getInstance(), () -> RegionManager.getInstance().getRegionsDatabase().save(this, "flag"));
    }

    public static RegionFlag load(ResultSet set, RegionFlagTypeCache typeCache) throws SQLException {
        Optional<RegionFlagType> typeOptional = typeCache.get(set.getString("type"));
        if (typeOptional.isEmpty())
            return null;

        return new RegionFlag(
                set.getInt("id"),
                typeOptional.get(),
                (RegionFlagValue<?>) BukkitStreamsUtil.deserialize(set.getBytes("value")),
                set.getString("region_name")
        );
    }

    public static void setupDatabase(Database<?> database) {
        database.update("CREATE TABLE IF NOT EXISTS flag (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "type VARCHAR(255) NOT NULL, " +
                "value BLOB NOT NULL, " +
                "region_name VARCHAR(255) NOT NULL, " +
                "FOREIGN KEY (region_name) REFERENCES region(name)" +
                ");"
        );
    }
}
