package com.wizardlybump17.seniorteam.regionmanager.api;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagTypes;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.BooleanFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.reader.BooleanFlagValueReader;
import com.wizardlybump17.seniorteam.regionmanager.api.registry.RegionFlagValueReaderRegistry;
import com.wizardlybump17.wlib.database.BukkitDatabaseHolder;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.DatabaseRegister;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class RegionManager extends JavaPlugin {

    private final RegionFlagTypeCache regionFlagTypeCache = new RegionFlagTypeCache();
    private final RegionCache regionCache = new RegionCache();
    private Database<?> regionsDatabase;
    private final RegionFlagValueReaderRegistry regionFlagValueReaderRegistry = new RegionFlagValueReaderRegistry();

    @Override
    public void onLoad() {
        initFlagTypes();
        initFlagValueReaders();
        initConfigs();
    }

    private void initFlagTypes() {
        regionFlagTypeCache.add(RegionFlagTypes.BREAK_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.PLACE_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.LEFT_CLICK_AIR);
        regionFlagTypeCache.add(RegionFlagTypes.LEFT_CLICK_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.RIGHT_CLICK_AIR);
        regionFlagTypeCache.add(RegionFlagTypes.RIGHT_CLICK_BLOCK);
        regionFlagTypeCache.add(RegionFlagTypes.INTERACT_PHYSICAL);
    }

    private void initFlagValueReaders() {
        regionFlagValueReaderRegistry.add(new BooleanFlagValueReader());
    }

    protected void initConfigs() {
        ConfigurationSerialization.registerClass(BooleanFlagValue.class);
    }

    @Override
    public void onEnable() {
        initDatabases();
    }

    private void initDatabases() {
        initRegionsDatabase();
    }

    private void initRegionsDatabase() {
        saveResource("databases/regions.properties", false);
        regionsDatabase = DatabaseRegister.getInstance().createDatabase("databases/regions.properties", new BukkitDatabaseHolder(this));
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            regionsDatabase.open();
            Region.setupDatabase(regionsDatabase);
            RegionFlag.setupDatabase(regionsDatabase);

            try (ResultSet query = regionsDatabase.query("SELECT * FROM region;")) {
                while (query.next()) {
                    Map<RegionFlagType, RegionFlag> flags = new HashMap<>();
                    Region region = Region.load(query, flags);

                    try (ResultSet flagQuery = regionsDatabase.query("SELECT * FROM flag WHERE region_name = ?;", region.getName())) {
                        while (flagQuery.next()) {
                            RegionFlag flag = RegionFlag.load(flagQuery, regionFlagTypeCache);
                            flag.setInDatabase(true);
                            flags.put(flag.getType(), flag);
                        }
                    }

                    region.setInDatabase(true);
                    regionCache.add(region);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDisable() {
        if (regionsDatabase != null)
            regionsDatabase.close();
    }

    public static RegionManager getInstance() {
        return getPlugin(RegionManager.class);
    }
}
