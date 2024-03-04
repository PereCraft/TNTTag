/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.commands;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
public class TagCommand implements CommandExecutor {
    
    private final TNTTag plugin;
    
    public TagCommand(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command name, String label, String args[]) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            
            if (args.length == 0) {
                player.sendMessage("§aTNTTag §7v. §e"
                        + plugin.getDescription().getVersion()
                        + "§7 by §aPereCraft \n"
                        + "§7Per aiuto usa §6/tnttag help"
                );
                return true;
            }
            
            if (args[0].equalsIgnoreCase("join")) {
                if (!player.hasPermission("tnttag.join.arena")) {
                    plugin.getUtils().sendNoPermissionMessage(player);
                    return false;
                }
                
                if (args.length < 2) {
                    player.sendMessage("§cUso: /tnttag join [arena]");
                    return false;
                }
                
                plugin.getJoinTools().joinArena(player, args[1]);
                return true;
                
            } else if (args[0].equalsIgnoreCase("autojoin")) {
                if (!player.hasPermission("tnttag.join.auto")) {
                    plugin.getUtils().sendNoPermissionMessage(player);
                    return false;
                }
                
                plugin.getJoinTools().autoJoin(player);
                return true;
                
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (!player.hasPermission("tnttag.leave")) {
                    plugin.getUtils().sendNoPermissionMessage(player);
                    return false;
                }

                Arena arena = plugin.getArenaManager().getArenaFromPlayer(player);
                
                if (arena == null) {
                    player.sendMessage("§cNon sei in nessuna arena.");
                    return false;
                }
                
                plugin.getArenaManager().removePlayer(player);
                return true;
                
            } else if (args[0].equals("spectate")) {
                if (!player.hasPermission("tnttag.spectate")) {
                    plugin.getUtils().sendNoPermissionMessage(player);
                    return false;
                }
                
                if (args.length < 2) {
                    player.sendMessage("§cUso: /tnttag spectate [player/arena]");
                    return false;
                }
                
                if (plugin.getArenaManager().getArenaFromPlayer(player) != null) {
                    player.sendMessage("§cDevi uscire dall'arena prima di spectare");
                    return false;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                
                if (target != null) {
                    Arena arena = plugin.getArenaManager().getArenaFromPlayer(target);
                    
                    if (arena == null) {
                        player.sendMessage("§e" + player.getName() + "§c non è in gioco.");
                        return false;
                    }
                    
                    plugin.getArenaManager().addSpectator(player, arena);
                    player.sendMessage("§aStai spectando §e" + player.getName());
                } else {
                    Arena arena = plugin.getArenaManager().getArena(args[1]);
                    
                    if (arena == null) {
                        player.sendMessage("§cArena non trovata.");
                        return false;
                    }
                    
                    plugin.getArenaManager().addSpectator(player, arena);
                    player.sendMessage("§aStai spectando §e" + args[1]);
                }
                return true;
                
            } else if (args[0].equalsIgnoreCase("stats")) {
                if (!player.hasPermission("tnttag.stats")) {
                    plugin.getUtils().sendNoPermissionMessage(player);
                    return false;
                }
                
                if (args.length == 1) {
                    player.sendMessage("§6Statistiche TNTTAG:");
                    player.sendMessage("Vittorie: §a" + plugin.getPlayerDataManager().getPlayerWins(player.getName()));
                    player.sendMessage("Partite giocate: §a" + plugin.getPlayerDataManager().getGamesPlayed(player.getName()));
                    
                } else {
                    if (!player.hasPermission("tnttag.stats.others")) {
                        plugin.getUtils().sendNoPermissionMessage(player);
                        return false;
                    }
                    
                    player.sendMessage("§6Statistiche TNTTAG di " + args[1] + "§6:");
                    player.sendMessage("Vittorie: §a" + plugin.getPlayerDataManager().getPlayerWins(args[1]));
                    player.sendMessage("Partite giocate: §a" + plugin.getPlayerDataManager().getGamesPlayed(args[1]));
                }
                return true;
                
            } else if (args[0].equalsIgnoreCase("help")) {
                sendHelpMessages(player);
                return true;

            } else {
                player.sendMessage("§cComando sconosciuto. Usa §6/tnttag help §cper aiuto");
                return false;
            }
            
        } else if (cs instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                cs.sendMessage("§aTNTTag §7v. §e" + plugin.getDescription().getVersion());
                return true;
            }
            
            cs.sendMessage("§cComando eseguibile solo in game!");
            return false;
        }
        
        return false;
    }
    
    private void sendHelpMessages(Player player) {
        player.sendMessage("§6--- TNTTAG HELP ---");
        player.spigot().sendMessage(new ComponentBuilder("/tnttag join [arena]")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttag join "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Entra in una arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttag autojoin")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttag autojoin"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Cerca una arena automaticamente")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttag leave")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttag leave"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Esci dall'arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttag spectate [player/arena]")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttag spectate "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Specta un giocatore o una arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttag stats ([player])")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttag stats"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Vedi le tue statistiche o quelle di altri giocatori")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttagadmin")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttagadmin"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Comandi di amministrazione")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/tnttagsetup")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tnttagsetup"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Comandi per il setup delle arene")))
                .create()
        );
    }
    
}
