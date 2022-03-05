package jinu.nulld.judge;

import jinu.nulld.ThiefAB;
import jinu.nulld.flow.EventOfVoteResult;
import jinu.nulld.flow.GameState;
import jinu.nulld.flow.GameStateChangeEvent;
import jinu.nulld.flow.ResultShowEndEvent;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static jinu.nulld.ABCommand.face_to_playerUUID;
import static jinu.nulld.vote.Vote.playerList;

public class Judge implements CommandExecutor, Listener {
    public static int agreeInt = 0;
    public static int disagreeInt = 0;
    private static List<UUID> endJudgeList = new ArrayList<>();
    int judge_resultCount = 0;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("sendjudgedataforhttp")) { // /SENDJUDGEDATAFORHTTP agree/disagree
            if (args[0].equals("agree")) {
                if (endJudgeList.contains(player.getUniqueId())) {
                    player.sendMessage("§c이미 재판투표를 진행하셨습니다.");
                    return false;
                }

                agreeInt++;
                endJudgeList.add(player.getUniqueId());
            } else if (args[0].equals("disagree")) {
                if (endJudgeList.contains(player.getUniqueId())) {
                    player.sendMessage("§c이미 재판투표를 진행하셨습니다.");
                    return false;
                }

                disagreeInt++;
                endJudgeList.add(player.getUniqueId());
            }
        } else if (args[0].equals("result")) {
            if (judge_resultCount < playerList.size()) {
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        for (UUID uuid : playerList) {
                            Bukkit.getPlayer(uuid).sendTitle("§a결과 확인중...", "§f결과를 확인하지 않은 사람이 있습니다.", 20, 40, 20);
                        }
                    }
                }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
            } else {
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        // TODO 결과로 이벤트 받아서 출력
                    }
                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 60);
            }
            judge_resultCount++;
        }
        return false;
    }

    @EventHandler
    public void onStateChange(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.JUDGE)) {
            new BukkitRunnable(){
                @Override
                public void run(){
                    if (endJudgeList.size() < playerList.size()) {
                        if (GameState.getNowState().equals(GameState.JUDGE)) {
                            for (UUID uuid : playerList) {
                                Bukkit.getPlayer(uuid).sendTitle("§b재판투표 진행중...", "§f다른 사람들의 재판투표가 끝날 때까지 기다려 주세요.", 20, 40, 20);
                            }
                        }
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
        }
    }
}
