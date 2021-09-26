/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 *
 * @author deka
 */
public class InventoryListener implements Listener {
    
    private final TNTTag plugin;
    
    public InventoryListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory() == null || event.getView() == null) return;
        
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            if(plugin.getArenaManager().getArenaFromPlayer(player) != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if(plugin.getArenaManager().getArenaFromPlayer(player) != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if(plugin.getArenaManager().getArenaFromPlayer(player) != null) {
            event.setCancelled(true);
        }
    }
    
}
