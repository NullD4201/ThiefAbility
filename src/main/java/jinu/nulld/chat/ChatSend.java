package jinu.nulld.chat;

import jinu.nulld.ChatChannel;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSend implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatChannel channel = ChatChannel.getPlayerChannel(player);

        String _message = event.getMessage();
        String message;
        if (channel == null || channel.getChannelName().equalsIgnoreCase("없음")) message = "&f<"+player.getDisplayName()+" (&6"+JobAPI.getJob(player).getJobName()+"&f) > "+_message;
        else message = "&f[ &b"+channel.getChannelName()+" &f] <"+player.getDisplayName()+" (&6"+JobAPI.getJob(player).getJobName()+"&f) > "+_message;
        if (JobAPI.getPlayerByJob(Jobs.DETECTIVE) != null) JobAPI.getPlayerByJob(Jobs.DETECTIVE).sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        if (channel != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if ((ChatChannel.getPlayerChannel(p) != null && ChatChannel.getPlayerChannel(p).equals(channel) && !JobAPI.getJob(p).equals(Jobs.DETECTIVE)) || ChatChannel.getPlayerChannel(p).equals(ChatChannel.ADMIN)) p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!JobAPI.getJob(p).equals(Jobs.DETECTIVE)) p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        Player player;
        player = event.getPlayer();
        if (player == null) return;

        String chat_regionName = null;
        String regionName = event.getRegionName();
        if (regionName.equalsIgnoreCase("bankA")) chat_regionName = "§f은행 A";
        if (regionName.equalsIgnoreCase("bankB")) chat_regionName = "§f은행 B";
        if (regionName.equalsIgnoreCase("court")) chat_regionName = "§f법원";

        if (JobAPI.getPlayerByJob(Jobs.DETECTIVE) != null) JobAPI.getPlayerByJob(Jobs.DETECTIVE).sendMessage("§7[ §a입장 알림 §7] §e"+player.getDisplayName()+" §a> "+chat_regionName);

        if (regionName.equalsIgnoreCase("bankA") || regionName.equalsIgnoreCase("bankB")) {
            player.performCommand("chat join 은행");
            if (regionName.equalsIgnoreCase("bankA")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ft enterbank A "+player.getName());
            if (regionName.equalsIgnoreCase("bankB")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ft enterbank B "+player.getName());
        }
        if (regionName.equalsIgnoreCase("court")) player.performCommand("chat join 법원");
    }

    @EventHandler
    public void onRegionLeft(RegionLeftEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        String chat_regionName = null;
        String regionName = event.getRegionName();
        if (regionName.equalsIgnoreCase("bankA")) chat_regionName = "§f은행 A";
        if (regionName.equalsIgnoreCase("bankB")) chat_regionName = "§f은행 B";
        if (regionName.equalsIgnoreCase("court")) chat_regionName = "§f법원";

        if (JobAPI.getPlayerByJob(Jobs.DETECTIVE) != null) JobAPI.getPlayerByJob(Jobs.DETECTIVE).sendMessage("§7[ §c퇴장 알림 §7] "+chat_regionName+" §c> §e"+player.getDisplayName());

        if (regionName.equalsIgnoreCase("bankA") || regionName.equalsIgnoreCase("bankB") || regionName.equalsIgnoreCase("court")) player.performCommand("chat join 공원");
    }
}
