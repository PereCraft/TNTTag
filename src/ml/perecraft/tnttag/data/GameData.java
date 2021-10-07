/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import ml.perecraft.tnttag.TNTTag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author deka
 */
public class GameData {
    
    private final TNTTag plugin;
    
    public GameData(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    File gameDataFile;
    FileConfiguration gameDataConfig;
    
    public void load() {
        gameDataFile = new File("plugins/TNTTAG/GameData.yml");
        
        if(!gameDataFile.exists()) {
            try {
                gameDataFile.createNewFile();
            }catch(IOException e){
                plugin.getLogger().log(Level.SEVERE, "Impossibile creare GameData file");
                plugin.getLogger().log(Level.SEVERE, e.getMessage());
            }
        }
        plugin.getLogger().info("Loading GameData");
        
        gameDataConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(gameDataFile);
        
        gameDataConfig.options().copyDefaults();
    }
    
    public void reload() {
        if(gameDataFile == null || !gameDataFile.exists()) {
            load();
        }else {
            gameDataConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(gameDataFile);
        }
        plugin.getLogger().info("GameData reloaded");
    }
    
    public void save() {
        if(gameDataFile == null || gameDataConfig == null) {
            plugin.getLogger().log(Level.WARNING, "Impossibile trovare GameData file");
            return;
        }
        plugin.getLogger().info("Saving GameData");
        try {
            gameDataConfig.save(gameDataFile);
        }catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Impossibile salvare GameData file");
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
        }
    }
    
    public FileConfiguration getDataConfig() {
        return gameDataConfig;
    }
    
    public void clearData() {
        if(gameDataFile == null || !gameDataFile.exists()) {
            plugin.getLogger().severe("Impossibile trovare PlayerData file");
            return;
        }
        
        gameDataConfig = null;
        
        plugin.getLogger().info("Removing GameData file");
        gameDataFile.delete();
        load();
    }
    
}
