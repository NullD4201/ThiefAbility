package jinu.nulld;

import jinu.nulld.chat.ChatSend;
import jinu.nulld.gui.GUIEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ThiefAB extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ChatChannel.register();

        getCommand("능력").setExecutor(new ABCommand());
        getCommand("능력").setTabCompleter(new ABCommand());
        getCommand("chat").setExecutor(new ABCommand());
        getCommand("chat").setTabCompleter(new ABCommand());
        getCommand("메모").setExecutor(new ABCommand());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatSend(), this);
        getServer().getPluginManager().registerEvents(new GUIEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Map<UUID, ChatChannel> channelMap = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ChatChannel.addPlayerToChannel(player, channelMap.getOrDefault(player.getUniqueId(), ChatChannel.PARK));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ChatChannel.getPlayerChannel(player) != null) {
            channelMap.put(player.getUniqueId(), ChatChannel.getPlayerChannel(player));
            ChatChannel.removePlayerFromChannel(player, ChatChannel.getPlayerChannel(player));
        }
    }
}
