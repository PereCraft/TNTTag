/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ml.perecraft.tnttag.TNTTag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author deka
 */
public class PlayerData {
    
    private final TNTTag plugin;
    
    public PlayerData(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    File playerDataFile;
    FileConfiguration playerDataConfig;
    
    public void load() {
        playerDataFile = new File("plugins/TNTTAG/PlayerData.yml");
        
        if(!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            }catch(IOException e) {
                plugin.getLogger().severe("Impossibile creare PlayerData file");
                plugin.getLogger().severe(e.getMessage());
            }
        }
        plugin.getLogger().info("Loading PlayerData");
        
        playerDataConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(playerDataFile);
        
        playerDataConfig.options().copyDefaults();
    }
    
    public void reload() {
        if(playerDataFile == null || !playerDataFile.exists()) {
            load();
        }else {
            playerDataConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(playerDataFile);
        }
        plugin.getLogger().info("PlayerData reloaded");
    }
    
    public void save() {
        if(playerDataFile == null || playerDataConfig == null) {
            plugin.getLogger().severe("Impossibile trovare PlayerData file");
            return;
        }
        
        plugin.getLogger().info("Saving PlayerData");
        try {
            playerDataConfig.save(playerDataFile);
        }catch(IOException e) {
            plugin.getLogger().severe("Impossibile salvare PlayerData file");
            plugin.getLogger().severe(e.getMessage());
        }
    }
    
    public FileConfiguration getDataConfig() {
        return playerDataConfig;
    }
    
    public void clearData() {
        if(playerDataFile == null || !playerDataFile.exists()) {
            plugin.getLogger().severe("Impossibile trovare PlayerData file");
            return;
        }
        
        playerDataConfig = null;
        
        plugin.getLogger().info("Removing PlayerData file");
        playerDataFile.delete();
        load();
    }
    
}
