package com.wizardlybump17.seniorteam.regionmanager.command;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.command.Command;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;

import java.util.stream.Collectors;

@ConfigInfo(name = "configs/commands/region.yml", holderType = RegionManager.class)
public record RegionCommand(RegionManager plugin) {

    public static final String PERMISSION = "regionmanager.admin";

    @Path(value = "messages.region-list", options = "fancy")
    public static String regionList = "§aRegions: §f{regions}";

    @Command(execution = "region list", permission = PERMISSION)
    public void list(GenericSender sender) {
        sender.sendMessage(
                regionList
                        .replace(
                                "{regions}",
                                plugin.getRegionCache().getAll().stream()
                                        .map(Region::getName)
                                        .collect(Collectors.joining(", "))
                        )
        );
    }
}
