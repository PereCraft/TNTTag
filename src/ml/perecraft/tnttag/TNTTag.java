/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag;

import ml.perecraft.tnttag.commands.AdminCommand;
import ml.perecraft.tnttag.commands.SetupCommand;
import ml.perecraft.tnttag.commands.TagCommand;
import ml.perecraft.tnttag.data.GameData;
import ml.perecraft.tnttag.data.PlayerData;
import ml.perecraft.tnttag.listeners.BlockListener;
import ml.perecraft.tnttag.listeners.ChatListener;
import ml.perecraft.tnttag.listeners.CommandListener;
import ml.perecraft.tnttag.listeners.EntityDamageListener;
import ml.perecraft.tnttag.listeners.InteractListener;
import ml.perecraft.tnttag.listeners.InventoryListener;
import ml.perecraft.tnttag.listeners.PlayerMoveListener;
import ml.perecraft.tnttag.listeners.PlayerQuitListener;
import ml.perecraft.tnttag.listeners.SignsListener;
import ml.perecraft.tnttag.managers.ArenaManager;
import ml.perecraft.tnttag.managers.CountdownManager;
import ml.perecraft.tnttag.managers.PlayerDataManager;
import ml.perecraft.tnttag.managers.SignsManager;
import ml.perecraft.tnttag.tools.ArenaSetupTools;
import ml.perecraft.tnttag.tools.JoinTools;
import ml.perecraft.tnttag.util.Utils;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author deka
 */
public class TNTTag extends JavaPlugin {

    private static TNTTag plugin;
    
    private ArenaManager arenaManager;
    private CountdownManager countdownManager;
    private PlayerDataManager playerDataManager;
    private SignsManager signsManager;
    private JoinTools joinTools;
    private ArenaSetupTools arenaSetupTools;
    private GameData gameData;
    private PlayerData playerData;
    private Utils utils;
    
    @Override
    public void onEnable() {
        plugin = this;
        
        gameData = new GameData(this);
        playerData = new PlayerData(this);
        arenaManager = new ArenaManager(this);
        countdownManager = new CountdownManager(this);
        playerDataManager = new PlayerDataManager(this);
        signsManager = new SignsManager(this);
        joinTools = new JoinTools(this);
        arenaSetupTools = new ArenaSetupTools(this);
        utils = new Utils(this);
        
        saveDefaultConfig();
        gameData.load();
        playerData.load();
        arenaManager.loadArenas();
        countdownManager.setTimesToBroadcast(getConfig().getIntegerList("times-to-broadcast"));
        signsManager.loadConfig();
        getCommand("tnttag").setExecutor(new TagCommand(this));
        getCommand("tnttagadmin").setExecutor(new AdminCommand(this));
        getCommand("tnttagsetup").setExecutor(new SetupCommand(this));
        registerEvents();
    }
    
    @Override
    public void onDisable() {
        arenaManager.stopGames();
    }
    
    public void reloadPlugin() {
        getLogger().info("Reloading TNTTAG");
        onDisable();
        reloadConfig();
        getServer().getPluginManager().disablePlugin(plugin);
        getServer().getPluginManager().enablePlugin(plugin);
    }
    
    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new CommandListener(plugin), plugin);
        pm.registerEvents(new InventoryListener(plugin), plugin);
        pm.registerEvents(new BlockListener(plugin), plugin);
        pm.registerEvents(new EntityDamageListener(plugin), plugin);
        pm.registerEvents(new PlayerQuitListener(plugin), plugin);
        pm.registerEvents(new InteractListener(plugin), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new SignsListener(plugin), plugin);
        pm.registerEvents(new PlayerMoveListener(plugin), plugin);
    }
    
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
    
    public CountdownManager getCountdownManager() {
        return countdownManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SignsManager getSignsManager() {
        return signsManager;
    }
    
    public JoinTools getJoinTools() {
        return joinTools;
    }
    
    public ArenaSetupTools getArenaSetupTools() {
        return arenaSetupTools;
    }
    
    public GameData getGameData() {
        return gameData;
    }
    
    public PlayerData getPlayerData() {
        return playerData;
    }

    public Utils getUtils() {
        return utils;
    }
    
    public static TNTTag getInstance() {
        return plugin;
    }
   
}