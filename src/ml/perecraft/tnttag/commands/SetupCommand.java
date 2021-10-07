/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.commands;

import ml.perecraft.tnttag.TNTTag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author deka
 */
public class SetupCommand implements CommandExecutor {
    
    private final TNTTag plugin;
    
    public SetupCommand(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command name, String label, String[] args) {
        if(cs instanceof Player) {
            if(!cs.hasPermission("tnttag.setup")) {
                cs.sendMessage("§cNon hai accesso a quel comando");
                return false;
            }
            Player player = (Player) cs;
            
            if(args.length == 0) {
                sendHelpMessages(player);
                return true;
            }
            
            if(args[0].equalsIgnoreCase("create")) {
                if(args.length < 2) {
                    cs.sendMessage("§cUso: /ttsetup create [arena]");
                    return false;
                }
                
                plugin.getArenaSetupTools().createArena(player, args[1]);
                return true;
                
            }else if(args[0].equalsIgnoreCase("cancel")) {
                plugin.getArenaSetupTools().cancelArenaCreation(player);
                return true;
                
            }else if(args[0].equalsIgnoreCase("setlobby")) {
                plugin.getArenaSetupTools().setLobbyLocation(player, player.getLocation());
                return true;
                
            }else if(args[0].equalsIgnoreCase("setspawn")) {
                plugin.getArenaSetupTools().setStartLocation(player, player.getLocation());
                return true;
                
            }else if(args[0].equalsIgnoreCase("setspect")) {
                plugin.getArenaSetupTools().setSpectLocation(player, player.getLocation());
                return true;
                
            }else if(args[0].equalsIgnoreCase("savearena")) {
                plugin.getArenaSetupTools().saveArena(player);
                return true;
                
            }else if(args[0].equalsIgnoreCase("delete")) {
                if(args.length < 2) {
                    cs.sendMessage("§cUso: /ttsetup delete [arena]");
                    return false;
                }
                
                //Delete arena
                return true;
            }else {
                cs.sendMessage("§cComando sconosciuto. Per aiuto: /ttsetup");
                return false;
            }
            
        }else if(cs instanceof ConsoleCommandSender) {
            cs.sendMessage("§cComando eseguibile solo in gioco.");
            return false;
        }
        
        return false;
    }
    
    public void sendHelpMessages(Player player) {
        player.sendMessage("§6--- TNTTAG SETUP HELP ---");
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup create [arena]")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup create "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Inizia la creazione di una nuova arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup cancel")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup cancel"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Annulla la creazione di una arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup setlobby")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup setlobby"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Imposta lobby dell'arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup setspawn")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup setspawn"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Imposta spawn dell'arena")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup setspect")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup setspect"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Imposta spawn spectator")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup savearena")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup savearena"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Salva l'arena in fase di creazione")))
                .create()
        );
        player.spigot().sendMessage(new ComponentBuilder("/ttsetup delete [arena]")
                .color(net.md_5.bungee.api.ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ttsetup delete "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Elimina una arena già salvata")))
                .create()
        );
    }
    
}
