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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 *
 * @author deka
 */
public class EntityDamageListener implements Listener {
    
    private final TNTTag plugin;
    
    public EntityDamageListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getCause() == DamageCause.ENTITY_ATTACK) return;
            Player player = (Player) event.getEntity();
            
            if(plugin.getArenaManager().getArenaFromPlayer(player) != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            Arena arena = plugin.getArenaManager().getArenaFromPlayer(victim);
            
            if(arena == null) return;
            
            if(!arena.isInGame()) {
                event.setCancelled(true);
                return;
            }
            
            if(arena.getTntPlayers().contains(damager.getUniqueId())) {
                plugin.getArenaManager().removeTNTPlayer(damager, arena);
                plugin.getArenaManager().addTNTPlayer(victim, arena);
                //Call player tag event
            }
            
            victim.setHealth(20.0D);
        }
    }
    
}
