package com.wizardlybump17.seniorteam.regionmanager.api.region;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.DatabaseStorable;
import lombok.Data;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Data
public class Region implements DatabaseStorable {

    private final String name;
    private final String world;
    private final Vector minPos;
    private final Vector maxPos;
    private final Map<String, RegionFlag> flags;
    private boolean deleted;
    private boolean inDatabase;
    private boolean dirty;

    public void setBounds(Vector v1, Vector v2) {
        Vector min = Vector.getMinimum(v1, v2);
        Vector max = Vector.getMaximum(v1, v2);

        minPos.setX(min.getX());
        minPos.setY(min.getY());
        minPos.setZ(min.getZ());

        maxPos.setX(max.getX());
        maxPos.setY(max.getY());
        maxPos.setZ(max.getZ());

        dirty = true;
    }

    @Override
    public void saveToDatabase(Map<String, Object> data) {
        data.put("name", name);
        data.put("world", world);
        savePosition(data);
    }

    @Override
    public void updateToDatabase(Map<String, Object> where, Map<String, Object> data) {
        where.put("name", name);
        savePosition(data);
    }

    private void savePosition(Map<String, Object> data) {
        data.put("min_x", minPos.getBlockX());
        data.put("min_y", minPos.getBlockY());
        data.put("min_z", minPos.getBlockZ());
        data.put("max_x", maxPos.getBlockX());
        data.put("max_y", maxPos.getBlockY());
        data.put("max_z", maxPos.getBlockZ());
    }

    @Override
    public void deleteFromDatabase(Map<String, Object> data) {
        data.put("name", name);
    }

    public static Region load(ResultSet set, Map<String, RegionFlag> flags) throws SQLException {
        return new Region(
                set.getString("name"),
                set.getString("world"),
                new Vector(
                        set.getInt("min_x"),
                        set.getInt("min_y"),
                        set.getInt("min_z")
                ),
                new Vector(
                        set.getInt("max_x"),
                        set.getInt("max_y"),
                        set.getInt("max_z")
                ),
                flags
        );
    }

    public static void setupDatabase(Database<?> database) {
        database.update("CREATE TABLE IF NOT EXISTS region (" +
                "name VARCHAR(255) PRIMARY KEY NOT NULL, " +
                "world VARCHAR(255) NOT NULL, " +
                "min_x INTEGER NOT NULL, " +
                "min_y INTEGER NOT NULL, " +
                "min_z INTEGER NOT NULL, " +
                "max_x INTEGER NOT NULL, " +
                "max_y INTEGER NOT NULL, " +
                "max_z INTEGER NOT NULL" +
                ");"
        );
    }
}
