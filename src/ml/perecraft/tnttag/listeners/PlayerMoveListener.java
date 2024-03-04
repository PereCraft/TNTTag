/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author deka
 */
public class PlayerMoveListener implements Listener {
    
    private final TNTTag plugin;
    
    public PlayerMoveListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            Location to = event.getTo();
            
            if (to == null) return;

            boolean forcedBorder = plugin.getConfig().getBoolean("world-options.force-border");

            if (forcedBorder && plugin.getUtils().isOutsideOfBorder(to)) {
                event.setTo(event.getFrom());
            }
        }
    }
}
