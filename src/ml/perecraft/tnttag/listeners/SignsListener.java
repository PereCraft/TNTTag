/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.listeners;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author deka
 */
public class SignsListener implements Listener {
    
    private final TNTTag plugin;
    
    public SignsListener(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    private final String signTitle = "§6[TNTTAG]";
    private final String signJoin = "§aEntra";
    private final String signAutoJoin = "§aAutoJoin";
    private final String signLeave = "§cEsci";
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(event.getBlock() == null) return;
        
        if(event.getLine(0).toLowerCase().contains("[tnttag]")) {
            Player player = event.getPlayer();
            
            if(!player.hasPermission("tnttag.sign.create")) {
                player.sendMessage("§cNon hai il permesso di creare cartelli TNTTag");
                event.setCancelled(true);
                return;
            }
            
            if(event.getLine(1).equalsIgnoreCase("join")) {
                Arena arena = plugin.getArenaManager().getArena(event.getLine(2));
                
                if(arena == null) {
                    player.sendMessage("§cArena non trovata");
                    event.setCancelled(true);
                    return;
                }
                
                event.setLine(0, signTitle);
                event.setLine(1, signJoin);
                event.setLine(2, arena.getName());
                
            }else if(event.getLine(1).equalsIgnoreCase("autojoin")) {
                event.setLine(0, signTitle);
                event.setLine(1, signAutoJoin);
                
            }else if(event.getLine(1).equalsIgnoreCase("leave")) {
                event.setLine(0, signTitle);
                event.setLine(1, signLeave);
                
            }else {
                player.sendMessage("§cStringa non valida");
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock() == null) return;
            
            if(event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                
                if(sign.getLine(0).equals(signTitle)) {
                    Player player = event.getPlayer();
                    
                    if(!player.hasPermission("tnttag.sign.use")) {
                        player.sendMessage("§cNon hai il permesso di usare questo oggetto.");
                        return;
                    }
                    
                    switch (sign.getLine(1)) {
                        case signJoin:
                            plugin.getJoinTools().joinArena(player, sign.getLine(2));
                            break;
                        case signAutoJoin:
                            plugin.getJoinTools().autoJoin(player);
                            break;
                        case signLeave:
                            plugin.getArenaManager().removePlayer(player);
                            break;
                        default:
                            player.sendMessage("§cStringa non valida");
                            break;
                    }
                }
            }
        }
    }
    
}
