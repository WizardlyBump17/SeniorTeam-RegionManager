package com.wizardlybump17.seniorteam.regionmanager.api.config;

import com.wizardlybump17.seniorteam.regionmanager.api.RegionManager;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Configuration {

    @UtilityClass
    @ConfigInfo(name = "messages/global.yml", holderType = RegionManager.class)
    public static class Messages {

        @Path(value = "invalid-region", options = "fancy")
        public static String invalidRegion = "§cInvalid region";
        @Path(value = "invalid-player", options = "fancy")
        public static String invalidPlayer = "§cInvalid player";
        @Path(value = "invalid-flag-type", options = "fancy")
        public static String invalidFlagType = "§cInvalid flag type";
        @Path(value = "invalid-flag-value", options = "fancy")
        public static String invalidFlagValue = "§cInvalid flag value";
        @Path(value = "invalid-positions", options = "fancy")
        public static String invalidPositions = "§cInvalid positions";

        @UtilityClass
        @ConfigInfo(name = "messages/region.yml", holderType = RegionManager.class)
        public static class Region {

            @Path(value = "pos-1", options = "fancy")
            public static String pos1 = "§aPosition 1 set to §f{position}";
            @Path(value = "pos-2", options = "fancy")
            public static String pos2 = "§aPosition 2 set to §f{position}";
            @Path(value = "type-name", options = "fancy")
            public static String typeName = "\n§aType the new region name\nType §ccancel §ato cancel\n ";
            @Path(value = "type-player", options = "fancy")
            public static String typePlayer = "\n§aType the player name\nType §ccancel §ato cancel\n ";
            @Path(value = "type-new-value", options = "fancy")
            public static String typeNewValue = "\n§aType the new value for the flag §f{flag}\n§aType §ccancel §ato cancel\n ";
        }
    }
}
