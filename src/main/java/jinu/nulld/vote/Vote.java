package jinu.nulld.vote;

import jinu.nulld.ABCommand;
import jinu.nulld.ThiefAB;
import jinu.nulld.bar.InstructionBar;
import jinu.nulld.flow.EventOfVoteResult;
import jinu.nulld.flow.GameState;
import jinu.nulld.flow.GameStateChangeEvent;
import jinu.nulld.gui.GUI;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Vote implements Listener {
    public static Map<UUID, Boolean> isVoteEnded_forPlayer;
    private static BukkitTask waitTitle;
    @EventHandler
    public void onStateChange(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.VOTING)) {
            isVoteEnded_forPlayer = new HashMap<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!JobAPI.getJob(player).equals(Jobs.NONE) && !player.getGameMode().equals(GameMode.SPECTATOR) && !player.getGameMode().equals(GameMode.CREATIVE)) playerList.add(player.getUniqueId());
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!JobAPI.getJob(player).equals(Jobs.NONE)) voteList.add(player.getUniqueId());
            }
            for (UUID uuid : playerList) {
                Bukkit.getPlayer(uuid).openInventory(GUI.voteGui(voteList));
            }

            waitTitle = new BukkitRunnable(){
                @Override
                public void run(){
                    for (UUID uuid : isVoteEnded_forPlayer.keySet()) {
                        if (isVoteEnded_forPlayer.getOrDefault(uuid, false)) {
                            if (GameState.getNowState().equals(GameState.VOTING)) Bukkit.getPlayer(uuid).sendTitle("§a투표 진행중...", "§f다른 사람들의 투표가 끝날 때까지 기다려 주세요.", 20, 40, 20);
                            else cancel();
                        }
                    }
                }
            }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
        }
    }
    public static List<UUID> playerList = new ArrayList<>(); // 투표창이 열릴 플레이어
    public static List<UUID> voteList = new ArrayList<>(); // 투표 목록에 해당하는 플레이어

    public static Map<UUID, Integer> vote_availableCount_map = new HashMap<>(); // 각 플레이어별 남은 투표 가능횟수

    public static Map<String, Integer> after_vote = new HashMap<>(); // 각 사람이 투표한 이후에 대상의 투표수
    public static int total_voteCount = 0; // 전체 투표수

    @EventHandler
    public void onVoteClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(InstructionBar.unicodeString_byKey("voteTitle"))) {

            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Player target = null;
            int clickedSlot = event.getSlot();

            if (event.getClickedInventory() == null) return;

            if (event.getClickedInventory().equals(player.getInventory())) return;

            if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                ItemStack _current = event.getCurrentItem();
                SkullMeta _current_meta = (SkullMeta) _current.getItemMeta();
                target = _current_meta.getOwningPlayer().getPlayer();
            }

            int target_restVote = vote_availableCount_map.getOrDefault(player.getUniqueId(), 1); // map에서 불러온 남은 투표수
            if (target_restVote == 0) {
                player.sendMessage("§c이미 투표를 완료했습니다.");
                return;
            }

            String object;
            if (clickedSlot == 48 || clickedSlot == 49 || clickedSlot == 50) {
                player.sendMessage("이번 투표를 건너뜁니다.");
                object = "skip";
            } else if (GUI.playerHeadPos.contains(clickedSlot)) {
                if (target == null) return;
                else {
                    player.sendMessage(target.getDisplayName() + "(을)를 선택하셨습니다.");
                    object = ABCommand.playerUUID_to_face(target.getUniqueId());
                }
            } else return;

            int _value = after_vote.getOrDefault(object, 0);
            if (JobAPI.getJob(player).equals(Jobs.JUDGE)) {
                after_vote.put(object, _value+3);
                vote_availableCount_map.put(player.getUniqueId(), 0);
                total_voteCount += 3;
            } else {
                after_vote.put(object, _value+1);
                vote_availableCount_map.put(player.getUniqueId(), 0);
                total_voteCount += 1;
            }

            if (object != null) {
                isVoteEnded_forPlayer.put(player.getUniqueId(), true);

                if (!isVoteEnded_forPlayer.values().contains(false) && isVoteEnded_forPlayer.keySet().size() == playerList.size()) {
                    waitTitle.cancel();
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            Bukkit.getPluginManager().callEvent(new EventOfVoteResult(after_vote));
                        }
                    }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 60);
                }

                player.closeInventory();
            } else {
                player.sendTitle("", "§c아직 투표를 완료하지 않았습니다.", 5, 30, 5);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        player.openInventory(event.getView());
                    }
                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);
            }
        }
    }
}