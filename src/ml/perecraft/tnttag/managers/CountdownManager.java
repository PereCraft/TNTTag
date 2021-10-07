/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.managers;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author deka
 */
public class CountdownManager {
    
    private final TNTTag plugin;
    
    public CountdownManager(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    private final List<Integer> timesToBroadcast = TNTTag.getInstance().getConfig().getIntegerList("times-to-broadcast");
    
    public void startCountdown(Arena arena, int seconds) {
        if(!arena.isRunningCountdown() && arena.getAlivePlayers().size() >= arena.getMinPlayers()) {
            arena.setSeconds(seconds);
            arena.setRunningCountdown(Boolean.TRUE);
            arena.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> runCountdown(arena, false), 20L, 20L));
        }
    }
    
    public void forceStartCountdown(Arena arena) {
        arena.setSeconds(plugin.getConfig().getInt("countdown"));
        arena.setRunningCountdown(Boolean.TRUE);
        arena.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> runCountdown(arena, true), 20L, 20L));
        plugin.getLogger().log(Level.INFO, "Arena {0} forced start. Questo metodo può causare problemi. Usare solo per debug.", arena.getName());
    }
    
    public void runCountdown(Arena arena, Boolean forceStart) {
        int seconds = arena.getSeconds();
        
        if(timesToBroadcast.contains(seconds)) {
            if(seconds == 1) {
                arena.sendMessage(plugin.getConfig().getString("messages.second-left")
                .replaceAll("&", "§")
                );
            } else {
                arena.sendMessage(plugin.getConfig().getString("messages.seconds-left")
                .replaceFirst("%sec%", Integer.toString(seconds))
                .replaceAll("&", "§")
                );
            }
        }
        
        for(UUID loopPlayer : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            player.setLevel(seconds);
            arena.setBoard(player, seconds);
        }
        
        if(arena.getAlivePlayers().size() < arena.getMinPlayers()) {
            if(forceStart) {
                plugin.getLogger().info("Arena " + arena.getName() + " forced to start. Countdown: " + seconds);
            }else {
                Bukkit.getScheduler().cancelTask(arena.getTaskId());
                arena.setRunningCountdown(Boolean.FALSE);
                arena.sendMessage("§cNon ci sono abbastanza giocatori. Countdown interrotto!");
                return;
            }
        }
            
        if(seconds == 0) {
            Bukkit.getScheduler().cancelTask(arena.getTaskId());
            pickRandomTNT(arena);
            startGame(arena);
            arena.sendMessage(plugin.getConfig().getString("messages.tnt-released").replaceAll("&", "§"));
        }else {
            arena.setSeconds(seconds - 1);
        }
    }
    
    public void startGame(Arena arena) {
        arena.setInGame(Boolean.TRUE);
        arena.setSeconds((arena.getAlivePlayers().size() > 6) ? 50 : 30);
        
        Location startLocation = arena.getStartLocation();
        int speedLevel = plugin.getConfig().getInt("speed-level") - 1;
        int speedTime = (arena.getSeconds() + 1) * 20;
        PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, speedTime, speedLevel);
        
        for(UUID loopPlayer : arena.getAlivePlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            player.teleport(startLocation);
            player.addPotionEffect(speedEffect);
        }
        
        arena.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> runGame(arena), 20L, 20L));
    }
    
    public void runGame(Arena arena) {
        int seconds = arena.getSeconds();
        
        if(arena.getAlivePlayers().isEmpty()) {
            plugin.getArenaManager().stopGame(arena);
            return;
        }
        
        for(UUID loopPlayer : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            player.setLevel(arena.getSeconds());
            arena.setBoard(player, arena.getSeconds());
        }
        
        if(seconds == 0) {
            Bukkit.getScheduler().cancelTask(arena.getTaskId());
            
            if(arena.getAlivePlayers().size() == 1) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> endGame(arena), 20L);
                
            }else if(arena.getAlivePlayers().size() == 2 && !arena.getTntPlayers().isEmpty()) {
                blowUpTNTs(arena);
                Bukkit.getScheduler().runTaskLater(plugin, () -> endGame(arena), 20L);
                
            }else {
                blowUpTNTs(arena);
                startDelayed(arena);
            }
        }else if(seconds <= 5) {
            for(UUID loopPlayer : arena.getPlayers()) {
                plugin.getTitleSender().sendTitle(Bukkit.getPlayer(loopPlayer), "§6" + seconds, "§7all'esplosione", 1, 20, 1);
            }
            
            arena.setSeconds(seconds - 1);
        }else {
            arena.setSeconds(seconds - 1);
        }
    }
    
    public void endGame(Arena arena) {
        plugin.getLogger().log(Level.INFO, "Fine gioco sull''arena {0}", arena.getName());
        
        Player winner = Bukkit.getPlayer(arena.getAlivePlayers().get(0));
        
        if(winner != null) {
            Bukkit.broadcastMessage(plugin.getConfig().getString("messages.win-message")
                    .replace("%player%", winner.getDisplayName())
                    .replace("%arena%", arena.getName())
                    .replaceAll("&", "§")
            );
            
            plugin.getPlayerDataManager().addWins(winner, 1);
        }
                
        for(UUID loopPlayer : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            player.sendMessage("§6Il gioco è terminato.");
            arena.removeBoard(player);
            plugin.getArenaManager().restorePlayer(player);
        }
        
        arena.getPlayers().clear();
        arena.getAlivePlayers().clear();
        arena.getTntPlayers().clear();
        arena.setInGame(Boolean.FALSE);
        arena.setRunningCountdown(Boolean.FALSE);
        plugin.getLogger().log(Level.INFO, "Arena {0} terminata", arena.getName());
    }
    
    public void startDelayed(Arena arena) {
        arena.setSeconds(5);
        arena.sendMessage(plugin.getConfig().getString("messages.round-restart").replaceAll("&", "§"));
        arena.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> runDelayed(arena), 20L, 20L));
    }
    
    public void runDelayed(Arena arena) {
        int seconds = arena.getSeconds();
        
        if(seconds == 0) {
            String tntmsg = plugin.getConfig().getString("messages.tnt-released2")
                    .replaceAll("&", "§");
            
            Bukkit.getScheduler().cancelTask(arena.getTaskId());
            arena.sendMessage(tntmsg);
            pickRandomTNT(arena);
            startGame(arena);
            
        }else {
            arena.setSeconds(seconds - 1);
        }
    }
    
    public void pickRandomTNT(Arena arena) {
        int amount = (arena.getAlivePlayers().size() >= 6) ? (arena.getPlayers().size() / 2) : 1;
        
        while(amount !=0) {
            giveARandomTNT(arena);
            amount--;
        }
    }
    
    public void giveARandomTNT(Arena arena) {
        Random random = new Random();
        UUID[] players = new UUID[arena.getAlivePlayers().size()];
        int i = 0;
            
        for(UUID loopPlayer : arena.getAlivePlayers()) {
            players[i] = loopPlayer;
            i++;
        }
            
        int randomInt = random.nextInt(players.length);
        Player randomPlayer = Bukkit.getPlayer(players[randomInt]);
        
        plugin.getArenaManager().addTNTPlayer(randomPlayer, arena);
        randomPlayer.sendMessage("§cHai ricevuto una TNT. Buona fortuna!");
        
        players = null;
    }
    
    public void blowUpTNTs(Arena arena) {
        for(UUID loopPlayer : arena.getTntPlayers()) {
            Player player = Bukkit.getPlayer(loopPlayer);
            
            player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.7F, 0.7F);
            arena.sendMessage(plugin.getConfig().getString("messages.player-blowed-up")
                    .replace("%player%", player.getName())
                    .replaceAll("&", "§")
            );
            
            arena.getAlivePlayers().remove(loopPlayer);
            player.teleport(arena.getSpectatorLocation());
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§7Sei in modalità spettatore. Digita §e/leave §7per uscire.");
        }
        arena.getTntPlayers().clear();
    }
    
    public void cancelTask(Arena arena) {
        Bukkit.getScheduler().cancelTask(arena.getTaskId());
        arena.setInGame(Boolean.FALSE);
        arena.setRunningCountdown(Boolean.FALSE);
    }
}
