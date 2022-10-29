package com.wizardlybump17.seniorteam.regionmanager.util;

import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

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

    public static ItemButton applyHead(ItemButton original, UUID player) {
        ItemBuilder builder = ItemBuilder.fromItemStack(original.getItem().get())
                .replaceDisplayNameLore(Map.of("{player}", Bukkit.getOfflinePlayer(player).getName()));
        if ((boolean) original.getCustomData().getOrDefault("apply-head", false))
            builder.skull(Bukkit.getOfflinePlayer(player));

        return new ItemButton(
                builder,
                original.getClickAction(),
                original.getCustomData()
        );
    }

    public static ItemBuilder formatRegionItem(ItemStack original, Region region) {
        return ItemBuilder.fromItemStack(original)
                .replaceDisplayNameLore(
                        Map.of("{region}", region.getName())
                );
    }

//    @UtilityClass
//    public static class RegionInventory {
//
//        public static void formatRegionsInventory(PaginatedInventoryBuilder builder, RegionCache cache) {
//            formatCloseButton(builder);
//
//
//        }
//
//        public static void formatRegionInventory(PaginatedInventoryBuilder builder, Region region, Runnable previous) {
//            formatBackButton(builder, previous);
//            formatCloseButton(builder);
//
//            builder.title(builder.title().replace("{region}", region.getName()));
//
//            for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
//                ItemButton button = entry.getValue();
//                String action = (String) button.getCustomData().get("action");
//                if (action == null) {
//                    entry.setValue(new ItemButton(
//                            formatRegionItem(button.getItem().get(), region),
//                            button.getClickAction(),
//                            button.getCustomData()
//                    ));
//                    continue;
//                }
//
//                entry.setValue(switch (action.toLowerCase()) {
//                    case "rename" -> getRenameItem(button, region);
//                    case "players" -> getPlayersItem(button, region);
//                    case "location" -> new ItemButton(
//                            formatRegionItem(button.getItem().get(), region),
//                            (event, inventory) -> {
//
//                            }
//                    );
//                    default -> button;
//                });
//            }
//
//            builder
//                    .listener(InventoryListener.<AsyncPlayerChatEvent>builder()
//                            .plugin(RegionManager.getInstance())
//                            .eventClass(AsyncPlayerChatEvent.class)
//                            .consumer(RegionInventory::handleRename)
//                            .build()
//                    );
//        }
//
//
//
//
//        private static void formatPlayersInventory(PaginatedInventoryBuilder builder, Region region, Runnable previous) {
//            formatCloseButton(builder);
//            formatBackButton(builder, previous);
//
//            builder.title(builder.title().replace("{region}", region.getName()));
//
//            for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
//                ItemButton button = entry.getValue();
//                String action = (String) button.getCustomData().get("action");
//                if (action == null) {
//                    entry.setValue(new ItemButton(
//                            formatRegionItem(button.getItem().get(), region),
//                            button.getClickAction(),
//                            button.getCustomData()
//                    ));
//                    continue;
//                }
//
//                switch (action.toLowerCase()) {
////                    case "add" -> entry.setValue(getAddPlayerItem(button, region));
//                }
//            }
//
//        }
//    }
}
