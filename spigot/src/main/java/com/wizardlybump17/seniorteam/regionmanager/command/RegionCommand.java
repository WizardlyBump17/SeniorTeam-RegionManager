package com.wizardlybump17.seniorteam.regionmanager.command;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.util.PlayerUtil;
import com.wizardlybump17.wlib.command.Command;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import com.wizardlybump17.wlib.command.sender.PlayerSender;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.stream.Collectors;

@ConfigInfo(name = "configs/commands/region.yml", holderType = RegionManager.class)
public record RegionCommand(RegionManager plugin) {

    public static final String PERMISSION = "regionmanager.admin";

    @Path(value = "messages.region-list", options = "fancy")
    public static String regionList = "§aRegions: §f{regions}";
    @Path(value = "messages.region-already-exists", options = "fancy")
    public static String regionAlreadyExists = "§cA region with this name already exists";
    @Path(value = "messages.invalid-positions", options = "fancy")
    public static String invalidPositions = "§cInvalid positions";
    @Path(value = "messages.region-created", options = "fancy")
    public static String regionCreated = "§aRegion §f{region} §acreated";
    @Path(value = "messages.invalid-region", options = "fancy")
    public static String invalidRegion = "§cInvalid region";
    @Path(value = "messages.region-deleted", options = "fancy")
    public static String regionDeleted = "§aRegion §f{region} §adeleted";

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

    @Command(execution = "region create <name>", permission = PERMISSION)
    public void create(PlayerSender sender, String name) {
        RegionCache cache = plugin.getRegionCache();

        if (cache.has(name)) {
            sender.sendMessage(regionAlreadyExists);
            return;
        }

        Location[] positions = PlayerUtil.getMarkedPositions(sender.getHandle());
        if (positions[0] == null || positions[1] == null || !positions[0].getWorld().equals(positions[2].getWorld())) {
            sender.sendMessage(invalidPositions);
            return;
        }

        Vector pos1 = positions[0].toVector();
        Vector pos2 = positions[1].toVector();

        Region region = new Region(
                name,
                positions[0].getWorld().getName(),
                Vector.getMinimum(pos1, pos2),
                Vector.getMaximum(pos1, pos2),
                new HashMap<>()
        );
        cache.add(region);
        region.save();

        sender.sendMessage(regionCreated.replace("{region}", region.getName()));
    }

    @Command(execution = "region delete <region>", permission = PERMISSION)
    public void delete(GenericSender sender, Region region) {
        if (region == null) {
            sender.sendMessage(invalidRegion);
            return;
        }

        region.setDeleted(true);
        region.save();
        plugin.getRegionCache().remove(region.getName());

        sender.sendMessage(regionDeleted.replace("{region}", region.getName()));
    }
}
