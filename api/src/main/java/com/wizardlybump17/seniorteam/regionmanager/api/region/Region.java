package com.wizardlybump17.seniorteam.regionmanager.api.region;

import com.wizardlybump17.seniorteam.regionmanager.api.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.DatabaseStorable;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Region implements DatabaseStorable {

    private final String name;
    private final String world;
    private final Vector minPos;
    private final Vector maxPos;
    private final Map<String, RegionFlag> flags;
    private final Set<UUID> players;
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
        data.put("players", players.stream().map(UUID::toString).collect(Collectors.joining(",")));
    }

    @Override
    public void updateToDatabase(Map<String, Object> where, Map<String, Object> data) {
        where.put("name", name);
        savePosition(data);
        data.put("players", players.stream().map(UUID::toString).collect(Collectors.joining(",")));
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

    public boolean isInside(Vector vector) {
        return vector.isInAABB(minPos, maxPos);
    }

    public boolean isInside(Entity entity) {
        return isInside(entity.getLocation());
    }

    public boolean isInside(Location location) {
        if (location.getWorld() == null)
            return false;
        return location.getWorld().getName().equals(name) && isInside(location.toVector());
    }

    @Nullable
    public RegionFlag getFlag(RegionFlagType type) {
        return flags.get(type.getName());
    }
    
    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(RegionManager.getInstance(), () -> RegionManager.getInstance().getRegionsDatabase().save(this, "region"));
        for (RegionFlag flag : flags.values())
            flag.save();
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        for (RegionFlag flag : flags.values())
            flag.setDeleted(deleted);
    }

    public boolean test(RegionFlag flag, Player player) {
        if (players.contains(player.getUniqueId()))
            return true;
        return flag != null && flag.test(player);
    }

    public boolean hasPlayer(UUID player) {
        return players.contains(player);
    }

    public void addPlayer(UUID player) {
        players.add(player);
        dirty = true;
    }

    public void removePlayer(UUID player) {
        players.remove(player);
        dirty = true;
    }

    public static Region load(ResultSet set, Map<String, RegionFlag> flags) throws SQLException {
        Set<UUID> players = new HashSet<>();
        for (String s : set.getString("players").split(","))
            if (!s.isEmpty())
                players.add(UUID.fromString(s));
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
                flags,
                players
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
