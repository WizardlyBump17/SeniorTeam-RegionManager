package com.wizardlybump17.seniorteam.regionmanager.command;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.util.PlayerUtil;
import com.wizardlybump17.wlib.command.Command;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import com.wizardlybump17.wlib.command.sender.PlayerSender;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
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
    @Path(value = "messages.invalid-player", options = "fancy")
    public static String invalidPlayer = "§cInalid player";
    @Path(value = "messages.player-added", options = "fancy")
    public static String playerAdded = "§aAdded §f{player} §ato region §f{region}";
    @Path(value = "messages.player-removed", options = "fancy")
    public static String playerRemoved = "§aPlayer §f{player} §afrom region §f{region}";
    @Path(value = "messages.invalid-flag-type", options = "fancy")
    public static String invalidFlagType = "§cInvalid flag type";
    @Path(value = "messages.invalid-flag-value", options = "fancy")
    public static String invalidFlagValue = "§cInvalid flag value";
    @Path(value = "messages.flag-set", options = "fancy")
    public static String flagSet = "§aFlag §f{type} §aset to §f{value} §ain region §f{region}";

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
        if (positions[0] == null || positions[1] == null || !positions[0].getWorld().equals(positions[1].getWorld())) {
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
                new HashMap<>(),
                new HashSet<>()
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

    @Command(execution = "region <region> player add <player>", permission = PERMISSION)
    public void playerAdd(GenericSender sender, Region region, Player player) {
        if (region == null) {
            sender.sendMessage(invalidRegion);
            return;
        }

        if (player == null) {
            sender.sendMessage(invalidPlayer);
            return;
        }

        region.addPlayer(player.getUniqueId());
        region.save();

        sender.sendMessage(
                playerAdded
                        .replace("{region}", region.getName())
                        .replace("{player}", player.getName())
        );
    }

    @Command(execution = "region <region> player remove <player>", permission = PERMISSION)
    public void playerRemove(GenericSender sender, Region region, Player player) {
        if (region == null) {
            sender.sendMessage(invalidRegion);
            return;
        }

        if (player == null) {
            sender.sendMessage(invalidPlayer);
            return;
        }

        region.removePlayer(player.getUniqueId());
        region.save();

        sender.sendMessage(
                playerRemoved
                        .replace("{region}", region.getName())
                        .replace("{player}", player.getName())
        );
    }

    @Command(execution = "region <region> flag set <type> <value>", permission = PERMISSION)
    public void flagSet(GenericSender sender, Region region, RegionFlagType type, RegionFlagValue<?> value) {
        if (region == null) {
            sender.sendMessage(invalidRegion);
            return;
        }

        if (type == null) {
            sender.sendMessage(invalidFlagType);
            return;
        }

        if (value == null) {
            sender.sendMessage(invalidFlagValue);
            return;
        }

        region.addFlag(new RegionFlag(
                ThreadLocalRandom.current().nextInt(),
                type,
                value,
                region.getName()
        ));
        region.save();
        sender.sendMessage(
                flagSet
                        .replace("{region}", region.getName())
                        .replace("{type}", type.getName())
                        .replace("{value}", value.toString())
        );
    }
}
