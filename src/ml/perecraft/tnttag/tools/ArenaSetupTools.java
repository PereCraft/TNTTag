/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.tools;

import java.util.HashMap;
import ml.perecraft.tnttag.TNTTag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author deka
 */
public class ArenaSetupTools {
    
    private final TNTTag plugin;
    
    public ArenaSetupTools(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    private static HashMap<Player, String> tempArenas = new HashMap<>();
    private static HashMap<Player, Location> tempLobbyLocation = new HashMap<>();
    private static HashMap<Player, Location> tempStartLocation = new HashMap<>();
    private static HashMap<Player, Location> tempSpectLocation = new HashMap<>();
    
    public void createArena(Player player, String arenaName) {
        if(tempArenas.containsKey(player)) {
            player.sendMessage("§cStai già creando l'arena" + tempArenas.get(player) + ". Usa /ttsetup cancel per annullare.");
            return;
        }
        
        tempArenas.put(player, arenaName);
        player.sendMessage("§aArena creata temporaneamente. Per salvarla usa /ttsetup savearena");
    }
    
    public void cancelArenaCreation(Player player) {
        if(!tempArenas.containsKey(player)) {
            if(player.isOnline()) {
                player.sendMessage("§cNon stai creando alcuna arena.");
            }
            return;
        }
        
        tempArenas.remove(player);
        tempLobbyLocation.remove(player);
        tempStartLocation.remove(player);
        tempSpectLocation.remove(player);
        player.sendMessage("§cHai annullato la creazione dell'arena");
    }
    
    public void setLobbyLocation(Player player, Location lobbyLocation) {
        if(!tempArenas.containsKey(player)) {
            player.sendMessage("§cDevi prima creare l'arena. Usa /ttsetup create [arena]");
            return;
        }
        
        tempLobbyLocation.put(player, lobbyLocation);
        player.sendMessage("§aLobby location aggiunta all'arena " + tempArenas.get(player));
    }
    
    public void setStartLocation(Player player, Location startLocation) {
        if(!tempArenas.containsKey(player)) {
            player.sendMessage("§cDevi prima creare l'arena. Usa /ttsetup create [arena]");
            return;
        }
        
        tempStartLocation.put(player, startLocation);
        player.sendMessage("§aStart location aggiunta all'arena " + tempArenas.get(player));
    }
    
    public void setSpectLocation(Player player, Location spectLocation) {
        if(!tempArenas.containsKey(player)) {
            player.sendMessage("§cDevi prima creare l'arena. Usa /ttsetup create [arena]");
            return;
        }
        
        tempSpectLocation.put(player, spectLocation);
        player.sendMessage("§aSpectator location aggiunta all'arena " + tempArenas.get(player));
    }
    
    public void saveArena(Player player) {
        if(!tempArenas.containsKey(player)) {
            player.sendMessage("§cNon stai creando nessuna arena.");
            return;
        }
        
        Location lobbyLocation = tempLobbyLocation.get(player);
        Location startLocation = tempStartLocation.get(player);
        Location spectLocation = tempSpectLocation.get(player);
        
        if(lobbyLocation == null) {
            player.sendMessage("§cLobby location non impostata.");
            return;
        }
        
        if(startLocation == null) {
            player.sendMessage("§cSpawn location non impostata.");
            return;
        }
        
        if(spectLocation == null) {
            player.sendMessage("§cSpectator location non impostata.");
            return;
        }
        
        plugin.getArenaManager().createArena(tempArenas.get(player), lobbyLocation, startLocation, spectLocation);
        player.sendMessage("§aL'arena " + tempArenas.get(player) + " è stata salvata.");
        tempArenas.remove(player);
        tempLobbyLocation.remove(player);
        tempStartLocation.remove(player);
        tempSpectLocation.remove(player);
    }
    
}
