/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.commands;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author deka
 */
public class AdminCommand implements CommandExecutor {
    
    private final TNTTag plugin;
    
    public AdminCommand(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command name, String label, String[] args) {
        
        if (cs instanceof Player || cs instanceof ConsoleCommandSender) {
            if (!cs.hasPermission("tnttag.admin")) {
                plugin.getUtils().sendNoPermissionMessage(cs);
                return false;
            }
            
            if (args.length == 0) {
                cs.sendMessage("§cArgs: reload, send [player] [arena], forcestart [arena], stopgame [arena], cleardata [game/player]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("reload")) {
                cs.sendMessage("§aPlugin reloaded!");
                plugin.reloadPlugin();
                return true;
                
            } else if (args[0].equalsIgnoreCase("send")) {
                if (args.length < 3) {
                    cs.sendMessage("§cUso: /ttadmin send [player] [arena]");
                    return false;
                }
                
                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    cs.sendMessage("§cErrore: Player non trovato.");
                    return false;
                }
                
                if (player.isInsideVehicle() || player.isSleeping()) {
                    cs.sendMessage("§cErrore: Questo player non può entrare in arena adesso.");
                    return false;
                }
                
                Arena arena = plugin.getArenaManager().getArena(args[2]);

                if (arena == null) {
                    cs.sendMessage("§cErrore: Impossibile trovare arena con quel nome.");
                    return false;
                }
                
                if (!plugin.getArenaManager().isArenaAvailable(arena)) {
                    cs.sendMessage("§cErrore: Arena non disponibile.");
                    return false;
                }
                
                plugin.getArenaManager().addPlayer(player, arena);
                return true;
                
            } else if (args[0].equalsIgnoreCase("forcestart")) {
                if (args.length < 2) {
                    cs.sendMessage("§cUso: /ttadmin forcestart [arena]");
                    return false;
                }
                
                Arena arena = plugin.getArenaManager().getArena(args[1]);

                if (arena == null) {
                    cs.sendMessage("§cErrore: Arena non trovata.");
                    return false;
                }
                
                plugin.getCountdownManager().forceStartCountdown(arena);
                return true;
                
            } else if (args[0].equalsIgnoreCase("stopgame")) {
                if (args.length < 2) {
                    cs.sendMessage("§cUso: /ttadmin endarena [arena]");
                    return false;
                }
                
                Arena arena = plugin.getArenaManager().getArena(args[1]);

                if (arena == null) {
                    cs.sendMessage("§cErrore: Arena non trovata.");
                    return false;
                }
                
                cs.sendMessage("§cStopping arena " + args[1]);
                plugin.getArenaManager().stopGame(arena);
                return true;
               
            } else if (args[0].equalsIgnoreCase("cleardata")) {
                if (args.length < 2) {
                    cs.sendMessage("§cUso: /ttadmin cleardata [game/player]");
                    return false;
                }
                
                if (args[1].equalsIgnoreCase("game")) {
                    cs.sendMessage("§cDisattivazione e unload delle arene...");
                    
                    for (Arena arena : plugin.getArenaManager().getArenas()) {
                        plugin.getArenaManager().unloadArena(arena);
                    }
                    
                    cs.sendMessage("§cCancellazione GameData...");
                    plugin.getGameData().clearData();
                    cs.sendMessage("§cTutti i dati relativi alle arene sono stati cancellati.");
                    return true;
                    
                } else if (args[1].equalsIgnoreCase("player")) {
                    cs.sendMessage("§cCancellazione PlayerData...");
                    plugin.getPlayerData().clearData();
                    cs.sendMessage("§cTutti i dati relativi ai player sono stati cancellati.");
                    return true;
                }
                
            } else {
                cs.sendMessage("§cComando sconosciuto. Usa /ttadmin per aiuto.");
                return false;
            }
        }
        
        return false;
    }
    
}
