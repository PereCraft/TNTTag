package ml.perecraft.tnttag.util;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ml.perecraft.tnttag.TNTTag;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

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

    public Location parseLocation(Object rawLoc) {
        try {
            return (Location) rawLoc;
        } catch (ClassCastException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to parse location: " + rawLoc.toString());
            e.printStackTrace();
        }
        return null;
    }

    public void sendNoPermissionMessage(CommandSender cs) {
        String msg = plugin.getConfig().getString("messages.no-permission", "No permission").replaceAll("§", "&");
        cs.sendMessage(msg);
    }

    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        String colorName = ChatColor.GREEN.name().toLowerCase();
        
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\",color:" + colorName + "}");
        IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subTitle + "\",color:" + colorName + "}");
        
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle packetLenght = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        
        CraftPlayer craftPlayer = (CraftPlayer) player;
        
        craftPlayer.getHandle().playerConnection.sendPacket(packetTitle);
        craftPlayer.getHandle().playerConnection.sendPacket(packetSubTitle);
        craftPlayer.getHandle().playerConnection.sendPacket(packetLenght);
    }
}
