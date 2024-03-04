/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author deka
 */
public class CommandListener implements Listener {
    
    private final TNTTag plugin;
    
    public CommandListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
        String[] args = event.getMessage().toLowerCase().split(" ");
        
        if (arena == null) return;
        
        if (args[0].equals("/leave")) {
            event.setCancelled(true);
            plugin.getArenaManager().removePlayer(player);
            player.sendMessage("§cSei uscito dall'arena");
            return;
        }

        List<String> allowedCmds = plugin.getConfig().getStringList("allowed-cmds");
        
        if (!allowedCmds.contains(args[0].replaceFirst("/", "")) && !player.hasPermission("tnttag.cmdbypass")) {
            String denyMsg = plugin.getConfig().getString("messages.command-denied").replaceAll("&", "§");
            
            event.setCancelled(true);
            player.sendMessage(denyMsg);
        }
    }
    
}
