package ml.perecraft.tnttag.util;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;

import ml.perecraft.tnttag.TNTTag;

public class Utils {

    private final TNTTag plugin;

    public Utils(TNTTag plugin) {
        this.plugin = plugin;
    }

    public boolean isOutsideOfBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    public void sendNoPermissionMessage(CommandSender cs) {
        String msg = plugin.getConfig().getString("messages.no-permission", "§cNon hai accesso a quel comando").replaceAll("§", "&");
        cs.sendMessage(msg);
    }
}
