/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author deka
 */
public class BlockListener implements Listener {
    
    private final TNTTag plugin;
    
    public BlockListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
        
        if (player != null && arena != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
        
        if (player != null && arena != null) {
            event.setCancelled(true);
        }
    }
    
}
