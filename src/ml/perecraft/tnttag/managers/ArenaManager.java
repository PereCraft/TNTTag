/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.managers;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author deka
 */
public class ArenaManager {
    
    private final TNTTag plugin;
    
    private ArrayList<Arena> arenaObjects = new ArrayList<>();
    
    public ArenaManager(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    public Arena getArena(String name) {
        for (Arena arena : getArenas()) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }
    
    public Arena getArenaFromPlayer(Player player) {
        for (Arena arena : getArenas()) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }
    
    public boolean isArenaAvailable(Arena arena) {
        return !(arena == null || arena.isFull() || arena.isInGame() || arena.getLobbyLocation() == null || arena.getStartLocation() == null);
    }
    
    public void addPlayer(Player player, Arena arena) {
        if (arena.getPlayers().contains(player.getUniqueId())) {
            plugin.getLogger().warning("Player " + player.getName() + " is already in arena " + arena.getName());
            return;
        };

        int countdown = plugin.getConfig().getInt("countdown");
        
        if (plugin.getConfig().getBoolean("send-arenajoin-msg")) {
            player.sendMessage(plugin.getConfig().getString("messages.arenajoin").replaceAll("&", "§"));
        }
        
        arena.getPlayers().add(player.getUniqueId());
        arena.getAlivePlayers().add(player.getUniqueId());
        player.getInventory().clear();
        giveArenaItems(player);
        player.updateInventory();
        player.teleport(arena.getLobbyLocation());
        player.setGameMode(GameMode.ADVENTURE);
        arena.setBoard(player, countdown);
        arena.sendMessage(player.getDisplayName() + "§7 entra nell'arena.");
        plugin.getUtils().sendTitle(player, "§8[§6TNTTag§8]", "§7Arena: §e" + arena.getName(), 5, 40, 5);
        
        if (arena.getAlivePlayers().size() >= arena.getMinPlayers() && !arena.isRunningCountdown()) {
            plugin.getCountdownManager().startCountdown(arena, countdown);
        }
    }
    
    public void removePlayer(Player player) {
        Arena arena = getArenaFromPlayer(player);
        
        if (arena != null) {
            UUID playerId = player.getUniqueId();
            
            restorePlayer(player);
            arena.getPlayers().remove(playerId);
            arena.removeBoard(player);
            
            if (arena.getAlivePlayers().contains(playerId)) {
                arena.getAlivePlayers().remove(playerId);
            }
            if (arena.getTntPlayers().contains(playerId)) {
                arena.getTntPlayers().remove(playerId);
            }
            
            arena.sendMessage("§c" + player.getName() + "§c ha lasciato la partita!");
        }
    }
    
    public void restorePlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setLevel(0);
        player.getActivePotionEffects().clear();
        player.updateInventory();
        player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
    }
    
    public void addSpectator(Player player, Arena arena) {
        if (arena.getPlayers().contains(player.getUniqueId())) return;
        
        arena.getPlayers().add(player.getUniqueId());
        player.getInventory().clear();
        player.updateInventory();
        player.teleport(arena.getSpectatorLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("§7Sei in modalità spettatore. Digita §e/leave §7per uscire.");
    }
    
    public void giveArenaItems(Player player) {
        ItemStack leaveItem = new ItemStack(Material.REDSTONE, 1);
        ItemMeta leaveItemMeta = leaveItem.getItemMeta();
        
        leaveItemMeta.setDisplayName(plugin.getConfig().getString("leave-item.name").replaceAll("&", "§"));
        leaveItem.setItemMeta(leaveItemMeta);
        player.getInventory().setItem(8, leaveItem);
    }
    
    public void addTNTPlayer(Player player, Arena arena) {
        arena.getTntPlayers().add(player.getUniqueId());
        arena.sendMessage(plugin.getConfig().getString("messages.player-is-it")
                .replace("%player%", player.getDisplayName())
                .replaceAll("&", "§")
        );
        player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.CREEPER_HISS, 0.7F, 0.7F);
    }
    
    public void removeTNTPlayer(Player player, Arena arena) {
        arena.getTntPlayers().remove(player.getUniqueId());
        player.sendMessage(plugin.getConfig().getString("messages.tnt-given")
                .replaceAll("&", "§")
        );     
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setItem(0, new ItemStack(Material.AIR, 1));
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.7F, 0.7F);
    }
    
    public void stopGame(Arena arena) {
        plugin.getLogger().log(Level.INFO, "Stopping arena {0}", arena.getName());
        
        for (UUID loopPlayer : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            arena.removeBoard(player);
            restorePlayer(player);
        }
        
        plugin.getCountdownManager().cancelTask(arena);
        arena.getPlayers().clear();
        arena.getAlivePlayers().clear();
        arena.getTntPlayers().clear();
    }
    
    public void stopGames() {
        for (Arena loopArena : arenaObjects) {
            for (UUID loopPlayer : loopArena.getPlayers()) {
                Player player = Bukkit.getPlayer(loopPlayer);
                
                loopArena.removeBoard(player);
                restorePlayer(player);
                
                plugin.getLogger().log(
                        Level.WARNING, 
                        "Arresto forzato arena {0} (Player kicked)", 
                        loopArena.getName()
                );
            }
            plugin.getCountdownManager().cancelTask(loopArena);
            loopArena.getPlayers().clear();
            loopArena.getTntPlayers().clear();
            loopArena.getAlivePlayers().clear();
        }
    }
    
    public void createArena(String arenaName, Location lobbyLocation, Location startLocation, Location spectLocation) {
        FileConfiguration gameData = plugin.getGameData().getDataConfig();
        
        gameData.set("arenas." + arenaName, null);
        
        String path = "arenas." + arenaName + ".";
        
        gameData.set(path + "lobby", lobbyLocation);
        gameData.set(path + "spawn", startLocation);
        gameData.set(path + "spect", spectLocation);
        
        plugin.getLogger().log(Level.INFO, "Saving arena {0}", arenaName);
        plugin.getGameData().save();
        loadArena(arenaName, lobbyLocation, startLocation, spectLocation);
    }
    
    public void loadArena(String arenaName, Location lobbyLocation, Location spawnLocation, Location spectLocation) {
        int maxPlayers = plugin.getConfig().getInt("max-players");
        int minPlayers = plugin.getConfig().getInt("min-players");
        
        Arena arena = new Arena(arenaName, lobbyLocation, spawnLocation, spectLocation, maxPlayers, minPlayers);
        arenaObjects.add(arena);
        setWorldOptions(arena.getStartLocation().getWorld());
        plugin.getLogger().log(Level.INFO, "Loaded arena {0}", arenaName);
    }
    
    public void loadArenas() {
        FileConfiguration gameData = plugin.getGameData().getDataConfig();
        
        if (gameData.getString("arenas") == null) {
            plugin.getLogger().info("Non sono state trovate arene da caricare");
            return;
        }
        
        int maxPlayers = plugin.getConfig().getInt("max-players");
        int minPlayers = plugin.getConfig().getInt("min-players");
        
        for (String key : gameData.getConfigurationSection("arenas").getKeys(false)) {
            String path = "arenas." + key + ".";
            
            Location lobbyLocation = (Location) gameData.get(path + "lobby");
            Location spawnLocation = (Location) gameData.get(path + "spawn");
            Location spectLocation = (Location) gameData.get(path + "spect");
            
            if (lobbyLocation == null || spawnLocation == null || spectLocation == null) {
                plugin.getLogger().log(Level.WARNING, "Impossibile caricare arena {0} location non valida", key);
                continue;
            }
            
            Arena arena = new Arena(key, lobbyLocation, spawnLocation, spectLocation, maxPlayers, minPlayers);
            
            arenaObjects.add(arena);
            setWorldOptions(arena.getStartLocation().getWorld());
            plugin.getLogger().log(Level.INFO, "Loaded arena {0}", key);
        }
    }
    
    public void unloadArena(Arena arena) {
        if (arena.isInGame() || arena.isRunningCountdown()) {
            stopGame(arena);
        }
        
        getArenas().remove(arena);
        plugin.getLogger().log(Level.INFO, "Arena {0} unloaded", arena.getName());
    }
    
    public void setWorldOptions(World world) {
        if (!plugin.getConfig().getBoolean("world-options.enabled")) return;
        
        double border = plugin.getConfig().getDouble("world-options.worldborder");
        boolean moballowed = plugin.getConfig().getBoolean("world-options.allow-creature-spawn");
        
        world.getWorldBorder().setCenter(0.0, 0.0);
        world.getWorldBorder().setSize(border);
        world.setSpawnFlags(moballowed, moballowed);
    }
    
    public ArrayList<Arena> getArenas() {
        return arenaObjects;
    }
}
