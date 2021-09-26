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
        
        if(plugin.getArenaManager().getArenaFromPlayer(player) == null) return;
        
        String[] args = event.getMessage().toLowerCase().split(" ");
        
        if(args[0].equals("/leave")) {
            event.setCancelled(true);
            plugin.getArenaManager().removePlayer(player);
            player.sendMessage("§cSei uscito dall'arena");
            return;
        }
        
        if(!plugin.getConfig().getStringList("allowed-cmds").contains(args[0].replaceFirst("/", ""))) {
            if(!player.hasPermission("tnttag.cmdbypass")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.command-denied")
                        .replaceAll("&", "§")
                );
            }
        }
    }
    
}
