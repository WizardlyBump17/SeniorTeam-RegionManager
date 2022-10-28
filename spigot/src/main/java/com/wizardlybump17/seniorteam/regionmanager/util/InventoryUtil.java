package com.wizardlybump17.seniorteam.regionmanager.util;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.config.Configuration;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.command.RegionCommand;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.listener.InventoryListener;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventory;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
                    case "rename" -> getRenameItem(button, region);
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

            builder
                    .listener(InventoryListener.<AsyncPlayerChatEvent>builder()
                            .plugin(RegionManager.getInstance())
                            .eventClass(AsyncPlayerChatEvent.class)
                            .consumer(RegionInventory::handleRename)
                            .build()
                    );
        }

        public static ItemBuilder formatRegionItem(ItemStack original, Region region) {
            return ItemBuilder.fromItemStack(original)
                    .replaceDisplayNameLore(
                            Map.of("{region}", region.getName())
                    );
        }

        private static ItemButton getRenameItem(ItemButton button, Region region) {
            return new ItemButton(
                    formatRegionItem(button.getItem().get(), region),
                    (event, inventory) -> {
                        inventory.setUnregisterListeners(false);

                        HumanEntity entity = event.getWhoClicked();
                        inventory.setData("player", entity);
                        inventory.setData("region", region);
                        inventory.setData("action", "rename");

                        entity.sendMessage(Configuration.Messages.Region.typeName);
                        entity.closeInventory();
                    }
            );
        }

        private static void handleRename(AsyncPlayerChatEvent event, PaginatedInventory inventory) {
            Player player = event.getPlayer();

            if (player != inventory.getData("player") || !"rename".equals(inventory.getData("action")))
                return;

            event.setCancelled(true);

            Region region = inventory.getData("region");

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                inventory.stopListeners();

                PaginatedInventoryBuilder builder = RegionCommand.regionInventory.clone();
                formatRegionInventory(builder, region, () -> player.performCommand("region list"));
                Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> builder.build().show(player));
                return;
            }

            if (RegionManager.getInstance().getRegionCache().get(event.getMessage()).isPresent()) {
                player.sendMessage(RegionCommand.regionAlreadyExists);
                return;
            }

            inventory.stopListeners();
            Bukkit.getScheduler().runTask(RegionManager.getInstance(), () -> {
                player.performCommand("region \"" + region.getName() + "\" \"rename\" \"" + event.getMessage() + "\"");

                PaginatedInventoryBuilder builder = RegionCommand.regionInventory.clone();
                formatRegionInventory(builder, region, () -> player.performCommand("region list"));
                builder.build().show(player);
            });
        }
    }
}
