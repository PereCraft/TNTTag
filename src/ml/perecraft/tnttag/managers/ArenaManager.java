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
import ml.perecraft.tnttag.util.Utils;

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

    public void addPlayer(Player player, Arena arena) {
        if (arena.getPlayers().contains(player.getUniqueId())) {
            plugin.getLogger().warning("Player " + player.getName() + " is already in arena " + arena.getName());
            return;
        };

        int countdown = plugin.getConfig().getInt("countdown", 30);
        
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

    public void addSpectator(Player player, Arena arena) {
        if (arena.getPlayers().contains(player.getUniqueId())) return;
        
        arena.getPlayers().add(player.getUniqueId());
        player.getInventory().clear();
        player.updateInventory();
        player.teleport(arena.getSpectatorLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("§7Sei in modalità spettatore. Digita §e/leave §7per uscire.");
    }

    public void createArena(String arenaName, Location lobbyLoc, Location startLoc, Location spectLoc) {
        FileConfiguration gameData = plugin.getGameData().getDataConfig();
        int maxPlayers = plugin.getConfig().getInt("max-players", 24);
        int minPlayers = plugin.getConfig().getInt("min-players", 2);
        
        gameData.set("arenas." + arenaName, null);
        
        String path = "arenas." + arenaName + ".";
        
        gameData.set(path + "lobby", lobbyLoc);
        gameData.set(path + "spawn", startLoc);
        gameData.set(path + "spect", spectLoc);
        
        plugin.getLogger().log(Level.INFO, "Saving arena {0}", arenaName);
        plugin.getGameData().save();
        loadArena(arenaName, lobbyLoc, startLoc, spectLoc, maxPlayers, minPlayers);
    }

    public Arena getAvailableArena() {
        for (Arena arena : arenaObjects) {
            if (isArenaAvailable(arena)) {
                return arena;
            }
        }
        return null;
    }
    
    public Arena getArena(String name) {
        for (Arena arena : arenaObjects) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }
    
    public Arena getArenaFromPlayer(Player player) {
        for (Arena arena : arenaObjects) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public ArrayList<Arena> getArenas() {
        return arenaObjects;
    }

    public void giveBomb(Player player, Arena arena) {
        arena.getTNTPlayers().add(player.getUniqueId());

        arena.sendMessage(plugin.getConfig().getString("messages.player-is-it")
            .replace("%player%", player.getDisplayName())
            .replaceAll("&", "§")
        );
        
        player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.CREEPER_HISS, 0.7F, 0.7F);
    }
    
    public boolean isArenaAvailable(Arena arena) {
        return !(arena == null || arena.isFull() || arena.isInGame() || arena.getLobbyLocation() == null || arena.getStartLocation() == null);
    }

    public void loadArenas() {
        FileConfiguration gameData = plugin.getGameData().getDataConfig();
        Utils utils = plugin.getUtils();
        
        if (gameData.getString("arenas") == null) {
            plugin.getLogger().info("Non sono state trovate arene da caricare");
            return;
        }
        
        int maxPlayers = plugin.getConfig().getInt("max-players", 24);
        int minPlayers = plugin.getConfig().getInt("min-players", 2);
        
        for (String key : gameData.getConfigurationSection("arenas").getKeys(false)) {
            String path = "arenas." + key + ".";
            
            Location lobbyLocation = utils.parseLocation(gameData.get(path + "lobby"));
            Location spawnLocation = utils.parseLocation(gameData.get(path + "spawn"));
            Location spectLocation = utils.parseLocation(gameData.get(path + "spect"));
            
            if (lobbyLocation == null || spawnLocation == null || spectLocation == null) {
                plugin.getLogger().log(Level.WARNING, "Impossibile caricare arena {0} location non valida", key);
                continue;
            }

            loadArena(key, lobbyLocation, spawnLocation, spectLocation, maxPlayers, minPlayers);
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
            if (arena.getTNTPlayers().contains(playerId)) {
                arena.getTNTPlayers().remove(playerId);
            }
            
            arena.sendMessage("§c" + player.getName() + "§c ha lasciato la partita!");
        }
    }

    public void removeBomb(Player player, Arena arena) {
        arena.getTNTPlayers().remove(player.getUniqueId());
        player.sendMessage(plugin.getConfig().getString("messages.tnt-given")
                .replaceAll("&", "§")
        );
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setItem(0, new ItemStack(Material.AIR, 1));
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.7F, 0.7F);
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
        arena.getTNTPlayers().clear();
    }

    public void unloadArenas() {
        for (Arena arena : arenaObjects) {
            unloadArena(arena);
        }
    }

    private void giveArenaItems(Player player) {
        ItemStack leaveItem = new ItemStack(Material.REDSTONE, 1);
        ItemMeta leaveItemMeta = leaveItem.getItemMeta();
        
        leaveItemMeta.setDisplayName(
            plugin.getConfig().getString("leave-item.name")
                .replaceAll("&", "§")
        );

        leaveItem.setItemMeta(leaveItemMeta);
        player.getInventory().setItem(8, leaveItem);
    }

    private void loadArena(String name, Location lobbyLoc, Location spawnLoc, Location spectLoc, int maxPlayers, int minPlayers) {        
        Arena arena = new Arena(name, lobbyLoc, spawnLoc, spectLoc, maxPlayers, minPlayers);

        arenaObjects.add(arena);
        setWorldOptions(arena.getStartLocation().getWorld());
        plugin.getLogger().log(Level.INFO, "Loaded arena {0}", name);
    }
    
    private void setWorldOptions(World world) {
        if (!plugin.getConfig().getBoolean("world-options.enabled")) return;
        
        double border = plugin.getConfig().getDouble("world-options.worldborder");
        boolean moballowed = plugin.getConfig().getBoolean("world-options.allow-creature-spawn");
        
        world.getWorldBorder().setCenter(0.0, 0.0);
        world.getWorldBorder().setSize(border);
        world.setSpawnFlags(moballowed, moballowed);
    }

    private void unloadArena(Arena arena) {
        if (arena.isInGame() || arena.isRunningCountdown()) {
            stopGame(arena);
        }
        
        arenaObjects.remove(arena);
        plugin.getLogger().log(Level.INFO, "Arena {0} unloaded", arena.getName());
    }
}
