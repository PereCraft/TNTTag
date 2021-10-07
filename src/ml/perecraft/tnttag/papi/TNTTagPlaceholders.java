/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.perecraft.tnttag.TNTTag;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author deka
 */
public class TNTTagPlaceholders extends PlaceholderExpansion {
    
    private final TNTTag plugin;
    
    public TNTTagPlaceholders(TNTTag plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "tnttag";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if(player == null || !player.hasPlayedBefore()) {
            return "";
        }
        
        if(identifier.equals("arena_count")) {
            return String.valueOf(plugin.getArenaManager().getArenas().size());
            
        }else if(identifier.equals("played")) {
            return String.valueOf(plugin.getPlayerDataManager().getGamesPlayed(player.getName()));
            
        }else if(identifier.equals("wins")) {
            return String.valueOf(plugin.getPlayerDataManager().getPlayerWins(player.getName()));
            
        }
        
        return null;
    }
    
}
