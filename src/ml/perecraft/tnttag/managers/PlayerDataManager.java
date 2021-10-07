/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.managers;

import ml.perecraft.tnttag.TNTTag;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author deka
 */
public class PlayerDataManager {
    
    private final TNTTag plugin;
    
    public PlayerDataManager(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    public int getPlayerWins(String playerName) {
        String path = "players." + playerName + ".";
        
        return plugin.getPlayerData().getDataConfig().getInt(path + "wins");
    }
    
    public void addWins(OfflinePlayer player, int amount) {
        if(!plugin.getConfig().getBoolean("playerdata-saving")) return;
        
        String path = "players." + player.getName() + ".";
        int playerWins = plugin.getPlayerData().getDataConfig().getInt(path + "wins");
        
        plugin.getPlayerData().getDataConfig().set(path, playerWins + amount);
    }
    
    public void setWins(OfflinePlayer player, int amount) {
        if(!plugin.getConfig().getBoolean("playerdata-saving")) return;
        
        String path = "players." + player.getName() + ".";
        
        plugin.getPlayerData().getDataConfig().set(path, amount);
    }
    
    public int getGamesPlayed(String playerName) {
        String path = "players." + playerName + ".";
        
        return plugin.getPlayerData().getDataConfig().getInt(path + "games-played");
    }
    
    public void addGamesPlayed(OfflinePlayer player, int amount) {
        if(!plugin.getConfig().getBoolean("playerdata-saving")) return;
        
        String path = "players." + player.getName() + ".";
        int gamesPlayed = plugin.getPlayerData().getDataConfig().getInt(path + "games-played");
        
        plugin.getPlayerData().getDataConfig().set(path, gamesPlayed + amount);
    }
    
    public void setGamesPlayed(OfflinePlayer player, int amount) {
        if(!plugin.getConfig().getBoolean("playerdata-saving")) return;
        
        String path = "players." + player.getName() + ".";
        
        plugin.getPlayerData().getDataConfig().set(path, amount);
    }
    
}
