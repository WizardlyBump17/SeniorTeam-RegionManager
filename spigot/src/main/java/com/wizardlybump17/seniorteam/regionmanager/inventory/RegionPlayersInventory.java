package com.wizardlybump17.seniorteam.regionmanager.inventory;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.config.Configuration;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.util.InventoryUtil;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import com.wizardlybump17.wlib.inventory.item.InventoryNavigator;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.listener.InventoryListener;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventory;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ConfigInfo(name = "inventories/region-players.yml", holderType = RegionManager.class)
public record RegionPlayersInventory(Region region, RegionInventory previous) {

    @Path("inventory")
    public static PaginatedInventoryBuilder inventory = PaginatedInventoryBuilder.create()
            .title("Players")
            .shape("    R    " +
                    " xxxxxxx " +
                    "< A @   >"
            )
            .shapeReplacement('R', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BRICKS)
                            .displayName("§aRegion: §f{region}")
            ))
            .shapeReplacement('A', new ItemButton(
                    new ItemBuilder()
                            .type(Material.PLAYER_HEAD)
                            .displayName("§aAdd player")
                            .customData("action", "add-player")
            ))
            .shapeReplacement('x', new ItemButton(
                    new ItemBuilder()
                            .type(Material.PLAYER_HEAD)
                            .displayName("§f{player}")
                            .lore("§7Double-click to remove")
                            .customData("apply-head", true)
            ))
            .shapeReplacement(' ', new ItemButton(new ItemBuilder().type(Material.BLACK_STAINED_GLASS_PANE).displayName(" ")))
            .nextPage(new InventoryNavigator(
                    new ItemBuilder()
                            .type(Material.ARROW)
                            .displayName("§aNext page")
                            .build(),
                    ' '
            ))
            .previousPage(new InventoryNavigator(
                    new ItemBuilder()
                            .type(Material.ARROW)
                            .displayName("§aPrevious page")
                            .build(),
                    ' '
            ))
            .shapeReplacement('@', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BARRIER)
                            .displayName("§cBack")
                            .customData("action", "back")
            ));

    public void show(Player player, int page) {
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
                case "add-player" -> getAddPlayerItem(button);
                default -> button;
            });
        }

        addContent(builder);

        builder
                .listener(InventoryListener.<AsyncPlayerChatEvent>builder()
                        .plugin(RegionManager.getInstance())
                        .eventClass(AsyncPlayerChatEvent.class)
                        .consumer(this::handleAddPlayer)
                        .build()
                )
                .build()
                .show(player, page);
    }

    private void addContent(PaginatedInventoryBuilder builder) {
        List<ItemButton> content = new ArrayList<>();
        for (UUID player : region.getPlayers()) {
            content.add(InventoryUtil.applyHead(
                    new ItemButton(
                            InventoryUtil.formatRegionItem(builder.shapeReplacements().get('x').getItem().get().clone(), region),
                            (event, inventory) -> {
                                if (event.getClick() != ClickType.DOUBLE_CLICK)
                                    return;

                                ((Player) event.getWhoClicked()).performCommand("region \"" + region.getName() + "\" \"player\" \"remove\" \"" + player + "\"");
                                show(((Player) event.getWhoClicked()), inventory.getCurrentPage());
                            }
                    ),
                    player
            ));
        }
        builder.content(content);
    }

    private ItemButton getAddPlayerItem(ItemButton button) {
        return new ItemButton(
                InventoryUtil.formatRegionItem(button.getItem().get(), region),
                (event, inventory) -> {
                    inventory.setUnregisterListeners(false);

                    HumanEntity entity = event.getWhoClicked();
                    inventory.setData("player", entity);
                    inventory.setData("action", "add-player");

                    entity.sendMessage(Configuration.Messages.Region.typePlayer);
                    entity.closeInventory();
                },
                button.getCustomData()
        );
    }

    private void handleAddPlayer(AsyncPlayerChatEvent event, PaginatedInventory inventory) {
        Player player = event.getPlayer();

        if (player != inventory.getData("player") || !"add-player".equals(inventory.getData("action")))
            return;

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            inventory.stopListeners();
            Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> show(player, inventory.getCurrentPage()));
            return;
        }

        Player target = Bukkit.getPlayerExact(event.getMessage());
        if (target == null) {
            player.sendMessage(Configuration.Messages.invalidPlayer);
            player.sendMessage(Configuration.Messages.Region.typePlayer);
            return;
        }

        inventory.stopListeners();

        Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> {
            player.performCommand("region \"" + region.getName() + "\" \"player\" \"add\" \"" + target.getUniqueId() + "\"");
            show(player, inventory.getCurrentPage());
        });
    }
}
