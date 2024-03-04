package ml.perecraft.tnttag.managers;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import ml.perecraft.tnttag.TNTTag;
import ml.perecraft.tnttag.util.Arena;

public class SignsManager {
    
    private final TNTTag plugin;

    private String signTitle;
    private String signJoin;
    private String signAutoJoin;
    private String signLeave;

    public SignsManager(TNTTag plugin) {
        this.plugin = plugin;
    }

    public void createSign(SignChangeEvent event) {
        Player player = event.getPlayer();
            
        if (!player.hasPermission("tnttag.sign.create")) {
            player.sendMessage("§cNon hai il permesso di creare cartelli TNTTag");
            event.setCancelled(true);
            return;
        }

        switch (event.getLine(1).toLowerCase()) {
            case "join":
                Arena arena = plugin.getArenaManager().getArena(event.getLine(2));
                
                if (arena == null) {
                    player.sendMessage("§cArena non trovata");
                    event.setCancelled(true);
                    return;
                }
                
                event.setLine(0, signTitle);
                event.setLine(1, signJoin);
                event.setLine(2, arena.getName());
                break;
        
            case "autojoin":
                event.setLine(0, signTitle);
                event.setLine(1, signAutoJoin);
                break;
            
            case "leave":
                event.setLine(0, signTitle);
                event.setLine(1, signLeave);
                break;

            default:
                player.sendMessage("§cStringa non valida");
                event.setCancelled(true);
                break;
        }
    }

    public void handleClick(Player player, Sign sign) {
        if(sign.getLine(0).equals(signTitle)) {

            if(!player.hasPermission("tnttag.sign.use")) {
                player.sendMessage("§cNon hai il permesso di usare questo oggetto.");
                return;
            }

            String signAction = sign.getLine(1);

            if (signAction.equals(signJoin)) {
                plugin.getJoinTools().joinArena(player, sign.getLine(2));
            } else if (signAction.equals(signAutoJoin)) {
                plugin.getJoinTools().autoJoin(player);
            } else if (signAction.equals(signLeave)) {
                plugin.getArenaManager().removePlayer(player);
            }
        }
    }

    public void loadConfig() {
        signTitle = plugin.getConfig().getString("signs.title");
        signJoin = plugin.getConfig().getString("signs.join");
        signAutoJoin = plugin.getConfig().getString("signs.autojoin");
        signLeave = plugin.getConfig().getString("signs.leave");
    }
}
