package com.wizardlybump17.seniorteam.regionmanager.listener;

import com.wizardlybump17.seniorteam.regionmanager.RegionManager;
import com.wizardlybump17.seniorteam.regionmanager.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public record PlayerListener(RegionManager plugin) implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void markPositions(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND || event.getItem().getType() != Material.WOODEN_AXE || !player.hasPermission("regionmanager.admin"))
            return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            PlayerUtil.markPosition(player, event.getClickedBlock().getLocation(), "Pos1");
            event.setCancelled(true);
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        PlayerUtil.markPosition(player, event.getClickedBlock().getLocation(), "Pos2");
        event.setCancelled(true);
    }
}
