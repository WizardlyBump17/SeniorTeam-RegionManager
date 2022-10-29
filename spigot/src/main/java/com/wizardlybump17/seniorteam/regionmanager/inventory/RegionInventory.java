package com.wizardlybump17.seniorteam.regionmanager.inventory;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.config.Configuration;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.command.RegionCommand;
import com.wizardlybump17.seniorteam.regionmanager.util.InventoryUtil;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.listener.InventoryListener;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventory;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

@ConfigInfo(name = "inventories/region.yml", holderType = RegionManager.class)
public record RegionInventory(Region region, RegionsInventory previous) {

    @Path("inventory")
    public static PaginatedInventoryBuilder inventory = PaginatedInventoryBuilder.create()
            .title("{region}")
            .shape("    R    " +
                    " N  P  L " +
                    "    @    "
            )
            .shapeReplacement('R', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BRICKS)
                            .displayName("§aRegion: §f{region}")
            ))
            .shapeReplacement('N', new ItemButton(
                    new ItemBuilder()
                            .type(Material.NAME_TAG)
                            .displayName("§aRename")
                            .customData("action", "rename")
            ))
            .shapeReplacement('P', new ItemButton(
                    new ItemBuilder()
                            .type(Material.PLAYER_HEAD)
                            .displayName("§aPlayers")
                            .customData("action", "players")
            ))
            .shapeReplacement('L', new ItemButton(
                    new ItemBuilder()
                            .type(Material.COMPASS)
                            .displayName("§aLocation")
                            .customData("action", "location")
            ))
            .shapeReplacement(' ', new ItemButton(new ItemBuilder().type(Material.BLACK_STAINED_GLASS_PANE).displayName(" ")))
            .shapeReplacement('@', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BARRIER)
                            .displayName("§cBack")
                            .customData("action", "back")
            ));

    public void show(Player player) {
        PaginatedInventoryBuilder builder = inventory.clone();
        InventoryUtil.formatCloseButton(builder);
        InventoryUtil.formatBackButton(builder, () -> previous.show(player));
        builder.title(builder.title().replace("{region}", region.getName()));

        for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
            ItemButton button = entry.getValue();
            String action = (String) button.getCustomData().get("action");
            if (action == null) {
                entry.setValue(new ItemButton(
                        InventoryUtil.formatRegionItem(button.getItem().get(), region),
                        button.getClickAction(),
                        button.getCustomData()
                ));
                continue;
            }

            entry.setValue(switch (action.toLowerCase()) {
                case "rename" -> getRenameItem(button);
                case "players" -> getPlayersItem(button);
                default -> button;
            });
        }

        builder
                .listener(InventoryListener.<AsyncPlayerChatEvent>builder()
                        .plugin(RegionManager.getInstance())
                        .eventClass(AsyncPlayerChatEvent.class)
                        .consumer(this::handleRename)
                        .build()
                )
                .build()
                .show(player);
    }

    private ItemButton getRenameItem(ItemButton button) {
        return new ItemButton(
                InventoryUtil.formatRegionItem(button.getItem().get(), region),
                (event, inventory) -> {
                    inventory.setUnregisterListeners(false);

                    HumanEntity entity = event.getWhoClicked();
                    inventory.setData("player", entity);
                    inventory.setData("action", "rename");

                    entity.sendMessage(Configuration.Messages.Region.typeName);
                    entity.closeInventory();
                }
        );
    }

    private void handleRename(AsyncPlayerChatEvent event, PaginatedInventory inventory) {
        Player player = event.getPlayer();

        if (player != inventory.getData("player") || !"rename".equals(inventory.getData("action")))
            return;

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            inventory.stopListeners();
            Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> show(player));
            return;
        }

        if (RegionManager.getInstance().getRegionCache().get(event.getMessage()).isPresent()) {
            player.sendMessage(RegionCommand.regionAlreadyExists);
            return;
        }

        inventory.stopListeners();
        Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> {
            player.performCommand("region \"" + region.getName() + "\" \"rename\" \"" + event.getMessage() + "\"");
            Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> show(player));
        });
    }

    private ItemButton getPlayersItem(ItemButton button) {
        return new ItemButton(
                InventoryUtil.formatRegionItem(button.getItem().get(), region),
                (event, inventory) -> new RegionPlayersInventory(region, this).show((Player) event.getWhoClicked(), 0)
        );
    }
}
