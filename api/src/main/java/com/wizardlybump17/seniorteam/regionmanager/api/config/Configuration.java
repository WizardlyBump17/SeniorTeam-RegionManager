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
    }
}
