package jinu.nulld;

import jinu.nulld.chat.ChatSend;
import jinu.nulld.gui.GuiEvent;
import jinu.nulld.http.HttpServerManager;
import jinu.nulld.judge.Judge;
import jinu.nulld.vote.Vote;
import jinu.nulld.vote.VoteResult;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public final class ThiefAB extends JavaPlugin implements Listener {
    public static FileConfiguration bartitle;
    public static final Logger LOGGER = Bukkit.getLogger();
    private HttpServerManager httpServerManager = null;
    public MySQL SQL;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.httpMain();
        this.SQL = new MySQL();

        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.info("Database not connected.");
            e.printStackTrace();
        }
        if (SQL.isConnected()) {
            LOGGER.info("Database connected");
        }

        ChatChannel.register();

        Objects.requireNonNull(getCommand("능력")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("능력")).setTabCompleter(new ABCommand());
        Objects.requireNonNull(getCommand("chat")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("chat")).setTabCompleter(new ABCommand());
        Objects.requireNonNull(getCommand("메모")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("공지")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("thab")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("thab")).setTabCompleter(new ABCommand());
        Objects.requireNonNull(getCommand("dbtest")).setExecutor(new ABCommand());
        Objects.requireNonNull(getCommand("sendvotedataforhttp")).setExecutor(new Vote());
        Objects.requireNonNull(getCommand("sendjudgedataforhttp")).setExecutor(new Vote());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatSend(), this);
        getServer().getPluginManager().registerEvents(new Vote(), this);
        getServer().getPluginManager().registerEvents(new Judge(), this);
        getServer().getPluginManager().registerEvents(new VoteResult(), this);
        getServer().getPluginManager().registerEvents(new GuiEvent(), this);

        if (new File(getDataFolder(), "bartitle.yml").exists()) {
            bartitle = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "bartitle.yml"));
            if (bartitle.getDouble("config_version", 0.0) != 1.4) saveResource("bartitle.yml", true);
        } else saveResource("bartitle.yml", true);
        bartitle = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "bartitle.yml"));

        if (!new File(getDataFolder(), "dbconfig.yml").exists()) {
            saveResource("dbconfig.yml", true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        httpServerManager.stop(0);
        httpServerManager = null;

        SQL.disconnect();
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
