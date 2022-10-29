package com.wizardlybump17.seniorteam.regionmanager.inventory.region;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
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

import java.util.ArrayList;
import java.util.List;

@ConfigInfo(name = "inventories/region/regions.yml", holderType = RegionManager.class)
public record RegionsInventory(RegionCache cache) {

    @Path("inventory")
    public static PaginatedInventoryBuilder inventory = PaginatedInventoryBuilder.create()
            .title("Regions")
            .shape("         " +
                    " xxxxxxx " +
                    "<   @   >"
            )
            .shapeReplacement(' ', new ItemButton(new ItemBuilder().type(Material.BLACK_STAINED_GLASS_PANE).displayName(" ")))
            .shapeReplacement('x', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BRICKS)
                            .displayName("§f{region}")
                            .lore("§7Click to see more info")
            ))
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
                            .displayName("§cClose")
                            .customData("action", "close")
            ));

    public void show(Player player) {
        PaginatedInventoryBuilder builder = inventory.clone();
        InventoryUtil.formatCloseButton(builder);

        List<ItemButton> content = new ArrayList<>();

        ItemButton base = builder.shapeReplacements().get('x');
        for (Region region : cache.getAll()) {
            content.add(new ItemButton(
                    InventoryUtil.formatRegionItem(base.getItem().get().clone(), region),
                    (event, inventory) -> new RegionInventory(region, this).show(player)
            ));
        }

        builder.content(content);

        builder.build().show(player);
    }
}
