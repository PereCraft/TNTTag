/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import java.util.logging.Level;
import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.managers.ArenaManager;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

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
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == DamageCause.ENTITY_ATTACK) return;

            Player player = (Player) event.getEntity();
            Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
            
            if (arena != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            
            ArenaManager arenaManager = plugin.getArenaManager();
            Arena arena = arenaManager.getArenaFromPlayer(victim);
            
            if (arena == null) return;
            
            if (!arena.isInGame()) {
                event.setCancelled(true);
                return;
            }
            
            if (arena.getTNTPlayers().contains(damager.getUniqueId())) {
                arenaManager.removeBomb(damager, arena);
                arenaManager.giveBomb(victim, arena);
            }
            
            victim.setHealth(20.0D);
            event.setDamage(1.0D);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
        
        if (arena != null) {
            plugin.getArenaManager().removePlayer(player);
            event.setDeathMessage(null);
            plugin.getLogger().log(Level.WARNING, "Player {0} died in arena", player.getName());
        }
    }
    
}
