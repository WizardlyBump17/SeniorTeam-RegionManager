package com.wizardlybump17.seniorteam.regionmanager.listener;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.api.config.Configuration;
import com.wizardlybump17.seniorteam.regionmanager.util.PlayerUtil;
import com.wizardlybump17.wlib.util.bukkit.StringUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public record PlayerListener(RegionManager plugin) implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void markPositions(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || event.getHand() != EquipmentSlot.HAND || !item.isSimilar(Configuration.wand.build()) || !player.hasPermission("regionmanager.admin"))
            return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location location = event.getClickedBlock().getLocation();
            PlayerUtil.markPosition(player, location, "Pos1");
            event.setCancelled(true);
            player.sendMessage(Configuration.Messages.Region.pos1.replace("{position}", StringUtil.toString(location)));
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Location location = event.getClickedBlock().getLocation();
        PlayerUtil.markPosition(player, event.getClickedBlock().getLocation(), "Pos2");
        event.setCancelled(true);
        player.sendMessage(Configuration.Messages.Region.pos2.replace("{position}", StringUtil.toString(location)));
    }
}
