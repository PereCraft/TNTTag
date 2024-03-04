/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author deka
 */
public class ChatListener implements Listener {
    
    private final TNTTag plugin;
    
    public ChatListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("tnttag-chat")) {
            return;
        }
        
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
        
        if (arena != null) {
            String message = event.getMessage();
            
            event.setFormat(ChatColor.GRAY + "[TNTTAG] "
                + ChatColor.RED + "[" + arena.getName() + "] "
                + ChatColor.WHITE + player.getDisplayName()
                + ChatColor.RED + " » "
                + ChatColor.WHITE + message);
        }
    }
    
}
