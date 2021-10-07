/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.tools;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.managers.ArenaManager;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.entity.Player;

/**
 *
 * @author deka
 */
public class JoinTools {
    
    private final TNTTag plugin;
    
    public JoinTools(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    public void joinArena(Player player, String arenaName) {
        if(player == null) {
            return;
        }
        
        if(player.isSleeping() || player.isInsideVehicle()) {
            player.sendMessage("§cNon puoi entrare in una partita adesso.");
            return;
        }
        
        if(plugin.getArenaManager().getArenaFromPlayer(player) != null) {
            player.sendMessage("§cSei già in una arena.");
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        
        if(arena == null) {
            player.sendMessage("§cArena non trovata.");
            return;
        }
        
        if(arena.isFull()) {
            player.sendMessage("§cNon ci sono slot disponibili.");
            return;
        }
        
        if(arena.isInGame()) {
            player.sendMessage("§cQuesta partita è già in corso.");
            return;
        }
        
        if(arena.getLobbyLocation() == null) {
            player.sendMessage("§cLobby spawn non impostato. Contatta lo staff.");
            return;
        }
        
        if(arena.getStartLocation() == null) {
            player.sendMessage("§cStart spawn non impostato. Contatta lo staff.");
            return;
        }
        
        plugin.getArenaManager().addPlayer(player, arena);
    }
    
    public void autoJoin(Player player) {
        if(player == null) {
            return;
        }
        
        if(player.isSleeping() || player.isInsideVehicle()) {
            player.sendMessage("§cNon puoi entrare in una partita adesso.");
            return;
        }
        
        Arena actualArena = plugin.getArenaManager().getArenaFromPlayer(player);
        
        if(actualArena != null) {
            player.sendMessage("§cSei già in una arena.");
            return;
        }
        
        Arena autoArena = getAutoArena();
        
        if(autoArena == null) {
            player.sendMessage("§cNon ci sono arene disponibili.");
            return;
        }
        
        plugin.getArenaManager().addPlayer(player, autoArena);
    }
    
    public Arena getAutoArena() {
        for(Arena loopArena : plugin.getArenaManager().getArenas()) {
            if(plugin.getArenaManager().isArenaAvailable(loopArena)) {
                return loopArena;
            }
        }
        
        return null;
    }
    
}
