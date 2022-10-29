package com.wizardlybump17.seniorteam.regionmanager.inventory;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.util.InventoryUtil;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import com.wizardlybump17.wlib.inventory.item.InventoryNavigator;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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
                default -> button;
            });
        }

        addContent(builder);

        builder
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
}
