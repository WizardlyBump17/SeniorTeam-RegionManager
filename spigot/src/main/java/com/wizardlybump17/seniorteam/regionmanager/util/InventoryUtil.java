package com.wizardlybump17.seniorteam.regionmanager.util;

import com.wizardlybump17.seniorteam.regionmanager.api.cache.RegionCache;
import com.wizardlybump17.seniorteam.regionmanager.api.region.Region;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import lombok.experimental.UtilityClass;

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

    public static void formatRegionsInventory(PaginatedInventoryBuilder builder, RegionCache cache) {
        formatCloseButton(builder);

        List<ItemButton> content = new ArrayList<>();

        ItemButton base = builder.shapeReplacements().get('x');
        for (Region region : cache.getAll()) {
            content.add(new ItemButton(
                    ItemBuilder.fromItemStack(base.getItem().get().clone()).replaceDisplayNameLore(Map.of("{region}", region.getName())),
                    (event, inventory) -> {

                    }
            ));
        }

        builder.content(content);
    }
}
