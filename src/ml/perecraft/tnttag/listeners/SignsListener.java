/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author deka
 */
public class SignsListener implements Listener {
    
    private final TNTTag plugin;
    
    public SignsListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getBlock() == null) return;
        
        if (event.getLine(0).toLowerCase().contains("[tnttag]")) {
            plugin.getSignsManager().createSign(event);
        }
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            
            if(block != null && block.getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                plugin.getSignsManager().handleClick(event.getPlayer(), sign);
            }
        }
    }
    
}
