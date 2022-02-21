package jinu.nulld;

import jinu.nulld.bar.InstructionBar;
import jinu.nulld.bar.ShiftToggle;
import jinu.nulld.chat.ChatSend;
import jinu.nulld.flow.GameState;
import jinu.nulld.gui.GUIEvent;
import jinu.nulld.http.HttpServerManager;
import jinu.nulld.vote.Vote;
import jinu.nulld.vote.VoteResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class ThiefAB extends JavaPlugin implements Listener {
    public static FileConfiguration bartitle;
    private HttpServerManager httpServerManager = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.httpMain();

        ChatChannel.register();

        Objects.requireNonNull(getCommand("능력")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("능력")).setTabCompleter(new ABCommand());
        Objects.requireNonNull(getCommand("chat")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("chat")).setTabCompleter(new ABCommand());
        Objects.requireNonNull(getCommand("메모")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("공지")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("thab")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("thab")).setTabCompleter(new ABCommand());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatSend(), this);
        getServer().getPluginManager().registerEvents(new GUIEvent(), this);
        getServer().getPluginManager().registerEvents(new Vote(), this);
        getServer().getPluginManager().registerEvents(new VoteResult(), this);
        getServer().getPluginManager().registerEvents(new ShiftToggle(), this);
        getServer().getPluginManager().registerEvents(new InstructionBar(), this);

        if (new File(getDataFolder(), "bartitle.yml").exists()) {
            bartitle = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "bartitle.yml"));
            if (bartitle.getDouble("config_version", 0.0) != 1.4) saveResource("bartitle.yml", true);
        } else saveResource("bartitle.yml", true);
        bartitle = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "bartitle.yml"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Map<UUID, Boolean> isVoteEnded_forPlayer = new HashMap<>();
    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isVoteEnded_forPlayer.getOrDefault(player.getUniqueId(), false)) {
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if (GameState.getNowState().equals(GameState.VOTING)) player.sendTitle("§a투표 진행중...", "§f다른 사람들의 투표가 끝날 때까지 기다려 주세요.", 20, 40, 20);
                        else cancel();
                    }
                }.runTaskTimer(this, 0, 80);
            }
        }
    }

    public Map<UUID, ChatChannel> channelMap = new HashMap<>();

    private void httpMain() {
        try {
            // 시작 로그
            System.out.println(
                    String.format(
                            "[%s][HTTP SERVER][START]",
                            new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
                    )
            );

            // 서버 생성
            httpServerManager = new HttpServerManager();
            httpServerManager.start();
            // Shutdown Hook
//            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    // 종료 로그
//                    System.out.println(
//                            String.format(
//                                    "[%s][HTTP SERVER][STOP]",
//                                    new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
//                            )
//                    );
//                }
//            }));

        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
    }

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
