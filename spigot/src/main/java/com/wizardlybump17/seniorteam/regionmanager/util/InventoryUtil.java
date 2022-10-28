package com.wizardlybump17.seniorteam.regionmanager.util;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.command.RegionCommand;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class InventoryUtil {

    public static void formatCloseButton(PaginatedInventoryBuilder builder) {
        for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
            ItemButton button = entry.getValue();
            String action = (String) button.getCustomData().get("action");
            if ("close".equalsIgnoreCase(action))
                entry.setValue(new ItemButton(
                        button.getItem(),
                        (event, inventory) -> event.getWhoClicked().closeInventory()
                ));
        }
    }

    public static void formatBackButton(PaginatedInventoryBuilder builder, Runnable previous) {
        for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
            ItemButton button = entry.getValue();
            String action = (String) button.getCustomData().get("action");
            if ("back".equalsIgnoreCase(action))
                entry.setValue(new ItemButton(
                        button.getItem(),
                        (event, inventory) -> previous.run()
                ));
        }
    }

    @UtilityClass
    public static class RegionInventory {

        public static void formatRegionsInventory(PaginatedInventoryBuilder builder, RegionCache cache) {
            formatCloseButton(builder);

            List<ItemButton> content = new ArrayList<>();

            ItemButton base = builder.shapeReplacements().get('x');
            for (Region region : cache.getAll()) {
                content.add(new ItemButton(
                        formatRegionItem(base.getItem().get().clone(), region),
                        (event, inventory) -> {
                            PaginatedInventoryBuilder regionInventory = RegionCommand.regionInventory.clone();
                            formatRegionInventory(
                                    regionInventory,
                                    region,
                                    () -> ((Player) event.getWhoClicked()).performCommand("region list")
                            );
                            regionInventory.build().show(event.getWhoClicked());
                        }
                ));
            }

            builder.content(content);
        }

        public static void formatRegionInventory(PaginatedInventoryBuilder builder, Region region, Runnable previous) {
            formatBackButton(builder, previous);
            formatCloseButton(builder);

            builder.title(builder.title().replace("{region}", region.getName()));

            for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
                ItemButton button = entry.getValue();
                String action = (String) button.getCustomData().get("action");
                if (action == null) {
                    entry.setValue(new ItemButton(
                            formatRegionItem(button.getItem().get(), region),
                            button.getClickAction(),
                            button.getCustomData()
                    ));
                    continue;
                }

                entry.setValue(switch (action.toLowerCase()) {
                    case "rename" -> new ItemButton(
                            formatRegionItem(button.getItem().get(), region),
                            (event, inventory) -> {

                            }
                    );
                    case "players" -> new ItemButton(
                            formatRegionItem(button.getItem().get(), region),
                            (event, inventory) -> {

                            }
                    );
                    case "location" -> new ItemButton(
                            formatRegionItem(button.getItem().get(), region),
                            (event, inventory) -> {

                            }
                    );
                    default -> button;
                });
            }
        }

        public static ItemBuilder formatRegionItem(ItemStack original, Region region) {
            return ItemBuilder.fromItemStack(original)
                    .replaceDisplayNameLore(
                            Map.of("{region}", region.getName())
                    );
        }
    }
}
