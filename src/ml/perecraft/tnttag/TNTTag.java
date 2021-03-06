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
import ml.perecraft.tnttag.papi.TNTTagPlaceholders;
import ml.perecraft.tnttag.tools.ArenaSetupTools;
import ml.perecraft.tnttag.tools.JoinTools;
import ml.perecraft.tnttag.tools.TitleSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author deka
 */
public class TNTTag extends JavaPlugin {

    private static TNTTag plugin;
    
    private static ArenaManager arenaManager;
    private static CountdownManager countdownManager;
    private static PlayerDataManager playerDataManager;
    private static JoinTools joinTools;
    private static ArenaSetupTools arenaSetupTools;
    private static GameData gameData;
    private static PlayerData playerData;
    private static TitleSender titleSender;
    
    @Override
    public void onEnable() {
        plugin = this;
        
        arenaManager = new ArenaManager(this);
        countdownManager = new CountdownManager(this);
        playerDataManager = new PlayerDataManager(this);
        joinTools = new JoinTools(this);
        arenaSetupTools = new ArenaSetupTools(this);
        gameData = new GameData(this);
        playerData = new PlayerData(this);
        titleSender = new TitleSender(this);
        
        saveDefaultConfig();
        gameData.load();
        playerData.load();
        arenaManager.loadArenas();
        getCommand("tnttag").setExecutor(new TagCommand(this));
        getCommand("tnttagadmin").setExecutor(new AdminCommand(this));
        getCommand("tnttagsetup").setExecutor(new SetupCommand(this));
        registerEvents();
        
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TNTTagPlaceholders(this).register();
        }
    }
    
    @Override
    public void onDisable() {
        arenaManager.stopGames();
    }
    
    public void reloadPlugin() {
        onDisable();
        reloadConfig();
        getServer().getPluginManager().disablePlugin(plugin);
        getServer().getPluginManager().enablePlugin(plugin);
        getLogger().info("Plugin reloaded.");
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
    
    public TitleSender getTitleSender() {
        return titleSender;
    }
    
    public static TNTTag getInstance() {
        return plugin;
    }
    
}
