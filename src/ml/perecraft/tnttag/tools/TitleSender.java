/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.tools;

import ml.perecraft.tnttag.TNTTag;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author deka
 */
public class TitleSender {
 
    private final TNTTag plugin;
    
    public TitleSender(TNTTag plugin) {
		this.plugin = plugin;
    }
    
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\",color:" + ChatColor.GREEN.name().toLowerCase() + "}");
        IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subTitle + "\",color:" + ChatColor.GREEN.name().toLowerCase() + "}");
        
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle packetLenght = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        
        CraftPlayer craftPlayer = (CraftPlayer) player;
        
        craftPlayer.getHandle().playerConnection.sendPacket(packetTitle);
        craftPlayer.getHandle().playerConnection.sendPacket(packetSubTitle);
        craftPlayer.getHandle().playerConnection.sendPacket(packetLenght);
    }
    
}
