/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author deka
 */
public class PlayerQuitListener implements Listener {
    
    private final TNTTag plugin;
    
    public PlayerQuitListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getArenaManager().removePlayer(event.getPlayer());
    }
    
}
