package com.wizardlybump17.seniorteam.regionmanager.api.region;

import lombok.Data;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Region {

    private final String name;
    private final String world;
    private final Vector minPos;
    private final Vector maxPos;

    public void setBounds(Vector v1, Vector v2) {
        Vector min = Vector.getMinimum(v1, v2);
        Vector max = Vector.getMaximum(v1, v2);

        minPos.setX(min.getX());
        minPos.setY(min.getY());
        minPos.setZ(min.getZ());

        maxPos.setX(max.getX());
        maxPos.setY(max.getY());
        maxPos.setZ(max.getZ());
    }

    public static Region load(ResultSet set) throws SQLException {
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
                )
        );
    }
}
