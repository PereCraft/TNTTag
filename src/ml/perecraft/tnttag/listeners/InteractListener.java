/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author deka
 */
public class InteractListener implements Listener {
 
    private final TNTTag plugin;
    
    public InteractListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            
            if(itemInHand == null) return;
            
            if(itemInHand.getType() == Material.REDSTONE && itemInHand.hasItemMeta()) {
                if(itemInHand.getItemMeta().getDisplayName().equals(plugin.getConfig().getString("leave-item.name").replaceAll("&", "§"))) {
                    event.setCancelled(true);
                    plugin.getArenaManager().removePlayer(event.getPlayer());
                }
            }
        }
    }
    
}
