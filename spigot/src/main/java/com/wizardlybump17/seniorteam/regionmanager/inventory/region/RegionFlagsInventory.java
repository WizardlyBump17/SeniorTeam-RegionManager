package com.wizardlybump17.seniorteam.regionmanager.inventory.region;

import com.wizardlybump17.seniorteam.regionmanager.api.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionFlagTypeCache;
import com.wizardlybump17.seniorteam.regionmanager.api.config.Configuration;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.RegionFlag;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.type.RegionFlagType;
import com.wizardlybump17.seniorteam.regionmanager.api.region.flag.value.RegionFlagValue;
import com.wizardlybump17.seniorteam.regionmanager.api.registry.RegionFlagValueReaderRegistry;
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

@ConfigInfo(name = "inventories/region/region-flags.yml", holderType = RegionManager.class)
public record RegionFlagsInventory(Region region, RegionInventory previous, RegionFlagTypeCache cache, RegionFlagValueReaderRegistry valueReaderRegistry) {

    @Path("inventory")
    public static PaginatedInventoryBuilder inventory = PaginatedInventoryBuilder.create()
            .title("Flags")
            .shape("    R    " +
                    " xxxxxxx " +
                    "<   @   >"
            )
            .shapeReplacement('R', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BRICKS)
                            .displayName("§aRegion: §f{region}")
            ))
            .shapeReplacement(' ', new ItemButton(new ItemBuilder().type(Material.BLACK_STAINED_GLASS_PANE).displayName(" ")))
            .shapeReplacement('@', new ItemButton(
                    new ItemBuilder()
                            .type(Material.BARRIER)
                            .displayName("§cBack")
                            .customData("action", "back")
            ))
            .shapeReplacement('x', new ItemButton(
                    new ItemBuilder()
                            .type(Material.YELLOW_BANNER)
                            .displayName("§f{flag}")
                            .lore(
                                    "§eValue: §f{value}",
                                    "",
                                    "§7Click to edit",
                                    "§7Drop to clear"
                            )
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
            ));

    public void show(Player player, int page) {
        PaginatedInventoryBuilder builder = inventory.clone();
        InventoryUtil.formatCloseButton(builder);
        InventoryUtil.formatBackButton(builder, () -> previous.show(player));
        builder.title(builder.title().replace("{region}", region.getName()));

        for (Map.Entry<Character, ItemButton> entry : builder.shapeReplacements().entrySet()) {
            ItemButton button = entry.getValue();
            entry.setValue(new ItemButton(
                    InventoryUtil.formatRegionItem(button.getItem().get(), region),
                    button.getClickAction(),
                    button.getCustomData()
            ));
        }

        List<ItemButton> content = new ArrayList<>();
        ItemButton base = builder.shapeReplacements().get('x');
        for (RegionFlagType type : cache.getAll())
            content.add(getFlagItem(type, base));

        builder
                .content(content)
                .listener(InventoryListener.<AsyncPlayerChatEvent>builder()
                        .eventClass(AsyncPlayerChatEvent.class)
                        .plugin(RegionManager.getInstance())
                        .consumer(this::handleEditValue)
                        .build()
                )
                .build()
                .show(player, page);
    }

    private ItemButton getFlagItem(RegionFlagType type, ItemButton base) {
        RegionFlag flag = region.getFlag(type);
        return new ItemButton(
                ItemBuilder.fromItemStack(base.getItem().get().clone())
                        .replaceDisplayNameLore(Map.of(
                                "{flag}", type.getName(),
                                "{value}", flag == null ? "§cnot set" : "§a" + flag.getValue().getValue().toString()
                        )),
                (event, inventory) -> {
                    Player player = (Player) event.getWhoClicked();

                    if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                        player.performCommand("region \"" + region.getName() + "\" \"flag\" \"unset\" \"" + type.getName() + "\"");
                        show(player, inventory.getCurrentPage());
                        return;
                    }

                    inventory.setUnregisterListeners(false);

                    HumanEntity entity = event.getWhoClicked();
                    inventory.setData("player", entity);
                    inventory.setData("action", "edit-flag");
                    inventory.setData("flag", type);

                    entity.sendMessage(Configuration.Messages.Region.typeNewValue.replace("{flag}", type.getName()));
                    entity.closeInventory();
                }
        );
    }

    private void handleEditValue(AsyncPlayerChatEvent event, PaginatedInventory inventory) {
        Player player = event.getPlayer();

        if (player != inventory.getData("player") || !"edit-flag".equals(inventory.getData("action")))
            return;

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            inventory.stopListeners();
            Bukkit.getScheduler().runTask(com.wizardlybump17.seniorteam.regionmanager.RegionManager.getInstance(), () -> show(player, inventory.getCurrentPage()));
            return;
        }

        RegionFlagType type = inventory.getData("flag");
        RegionFlagValue<?> value = valueReaderRegistry.read(event.getMessage());
        if (!type.isValidValue(value)) {
            player.sendMessage(Configuration.Messages.invalidFlagValue);
            player.sendMessage(Configuration.Messages.Region.typeNewValue.replace("{flag}", type.getName()));
            return;
        }

        inventory.stopListeners();
        Bukkit.getScheduler().runTask(com.wizardlybump17.seniorteam.regionmanager.RegionManager.getInstance(), () -> {
            player.performCommand("region \"" + region.getName() + "\" \"flag\" \"set\" \"" + type.getName() + "\" \"" + value.getValue().toString() + "\"");
            show(player, inventory.getCurrentPage());
        });
    }
}
