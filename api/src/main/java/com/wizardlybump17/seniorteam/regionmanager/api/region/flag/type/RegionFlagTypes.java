package com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type;

import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.BooleanFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class RegionFlagTypes {

    public static final RegionFlagType<BooleanFlagValue> BREAK_BLOCK = createBooleanFlagType("BREAK_BLOCK", "");
    public static final RegionFlagType<BooleanFlagValue> PLACE_BLOCK = createBooleanFlagType("PLACE_BLOCK", "");
    public static final RegionFlagType<BooleanFlagValue> LEFT_CLICK_AIR = createBooleanFlagType("LEFT_CLICK_AIR", "");
    public static final RegionFlagType<BooleanFlagValue> LEFT_CLICK_BLOCK = createBooleanFlagType("LEFT_CLICK_BLOCK", "");
    public static final RegionFlagType<BooleanFlagValue> RIGHT_CLICK_AIR = createBooleanFlagType("RIGHT_CLICK_AIR", "");
    public static final RegionFlagType<BooleanFlagValue> RIGHT_CLICK_BLOCK = createBooleanFlagType("RIGHT_CLICK_BLOCK", "");
    public static final RegionFlagType<BooleanFlagValue> INTERACT_PHYSICAL = createBooleanFlagType("INTERACT_PHYSICAL", "");

    private static RegionFlagType<BooleanFlagValue> createBooleanFlagType(String name, String permission) {
        return new RegionFlagType<>() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isValidValue(RegionFlagValue<?> value) {
                return value instanceof BooleanFlagValue;
            }

            @Override
            public boolean test(BooleanFlagValue value, Player player) {
                if (permission == null)
                    return value.getValue();
                if (permission.isEmpty())
                    return player.isOp() && value.getValue();
                return value.getValue();
            }
        };
    }
}
