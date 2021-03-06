package jinu.nulld.vote;

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

import java.util.*;

import static jinu.nulld.ABCommand.face_to_playerUUID;

public class Vote implements CommandExecutor, Listener {
    public static List<UUID> endVoteList = new ArrayList<>();
    public static Map<String, Integer> after_vote = new HashMap<>();
    int resultCount = 0;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("sendvotedataforhttp")) { // /SENDVOTEDATAFORHTTP uuid/skip
            if (args[0].equals("vote")) {
                String object = "skip";
                int currentCount_uuid_or_skip = 0;
                if (!args[1].equals("skip")) {
                    object = args[1];
                    currentCount_uuid_or_skip = after_vote.getOrDefault(face_to_playerUUID.get(object).toString(), 0);
                }

                if (JobAPI.getJob(player).equals(Jobs.JUDGE)) after_vote.put(face_to_playerUUID.get(object).toString(), currentCount_uuid_or_skip + 3);
                else after_vote.put(face_to_playerUUID.get(object).toString(), currentCount_uuid_or_skip + 1);

                endVoteList.add(player.getUniqueId());
            } else if (args[0].equals("result")) {
                if (resultCount < playerList.size()) {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            for (UUID uuid : playerList) {
                                Bukkit.getPlayer(uuid).sendTitle("??a?????? ?????????...", "??f????????? ???????????? ?????? ????????? ????????????.", 20, 40, 20);
                            }
                        }
                    }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
                } else {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            Bukkit.getPluginManager().callEvent(new ResultShowEndEvent(after_vote));
                        }
                    }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 60);
                }
                resultCount ++;
            }
        }
        return false;
    }

    public static List<UUID> playerList = new ArrayList<>();
    @EventHandler
    public void onStateChange(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.VOTING)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!JobAPI.getJob(player).equals(Jobs.NONE) && !player.getGameMode().equals(GameMode.SPECTATOR) && !player.getGameMode().equals(GameMode.CREATIVE)) {
                    playerList.add(player.getUniqueId());
                }
            }

            if (endVoteList != null) {
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if (endVoteList.size() < playerList.size()) {
                            if (GameState.getNowState().equals(GameState.VOTING)) {
                                for (UUID uuid : endVoteList) {
                                    Bukkit.getPlayer(uuid).sendTitle("??a?????? ?????????...", "??f?????? ???????????? ????????? ?????? ????????? ????????? ?????????.", 20, 40, 20); // ??????
                                }
                            } else cancel();
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
            }
        }
    }
}

//public class Vote implements Listener {
//    public static Map<UUID, Boolean> isVoteEnded_forPlayer;
//    private static BukkitTask waitTitle;
//    @EventHandler
//    public void onStateChange(GameStateChangeEvent event) {
//        if (event.getNewState().equals(GameState.VOTING)) {
//            isVoteEnded_forPlayer = new HashMap<>();
//
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                if (!JobAPI.getJob(player).equals(Jobs.NONE) && !player.getGameMode().equals(GameMode.SPECTATOR) && !player.getGameMode().equals(GameMode.CREATIVE)) playerList.add(player.getUniqueId());
//            }
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                if (!JobAPI.getJob(player).equals(Jobs.NONE)) voteList.add(player.getUniqueId());
//            }
//            for (UUID uuid : playerList) {
//                Bukkit.getPlayer(uuid).openInventory(GUI.voteGui(voteList));
//            }
//
//            waitTitle = new BukkitRunnable(){
//                @Override
//                public void run(){
//                    for (UUID uuid : isVoteEnded_forPlayer.keySet()) {
//                        if (isVoteEnded_forPlayer.getOrDefault(uuid, false)) {
//                            if (GameState.getNowState().equals(GameState.VOTING)) Bukkit.getPlayer(uuid).sendTitle("??a?????? ?????????...", "??f?????? ???????????? ????????? ?????? ????????? ????????? ?????????.", 20, 40, 20);
//                            else cancel();
//                        }
//                    }
//                }
//            }.runTaskTimer(ThiefAB.getPlugin(ThiefAB.class), 0, 80);
//        }
//    }
//    public static List<UUID> playerList = new ArrayList<>(); // ???????????? ?????? ????????????
//    public static List<UUID> voteList = new ArrayList<>(); // ?????? ????????? ???????????? ????????????
//
//    public static Map<UUID, Integer> vote_availableCount_map = new HashMap<>(); // ??? ??????????????? ?????? ?????? ????????????
//
//    public static Map<String, Integer> after_vote = new HashMap<>(); // ??? ????????? ????????? ????????? ????????? ?????????
//    public static int total_voteCount = 0; // ?????? ?????????
//
//    @EventHandler
//    public void onVoteClick(InventoryClickEvent event) {
//        if (event.getView().getTitle().equalsIgnoreCase(InstructionBar.unicodeString_byKey("voteTitle"))) {
//
//            event.setCancelled(true);
//            Player player = (Player) event.getWhoClicked();
//            Player target = null;
//            int clickedSlot = event.getSlot();
//// TODO ?????? ?????? ???????????? ??????????????? ?????? ???????????? ???????????? ????????? ??????
//            if (event.getClickedInventory() == null) return;
//
//            if (event.getClickedInventory().equals(player.getInventory())) return;
//
//            if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
//                ItemStack _current = event.getCurrentItem();
//                SkullMeta _current_meta = (SkullMeta) _current.getItemMeta();
//                target = _current_meta.getOwningPlayer().getPlayer();
//            }
//
//            int target_restVote = vote_availableCount_map.getOrDefault(player.getUniqueId(), 1); // map?????? ????????? ?????? ?????????
//            if (target_restVote == 0) {
//                player.sendMessage("??c?????? ????????? ??????????????????.");
//                return;
//            }
//
//            String object;
//            if (clickedSlot == 48 || clickedSlot == 49 || clickedSlot == 50) {
//                player.sendMessage("?????? ????????? ???????????????.");
//                object = "skip";
//            } else if (GUI.playerHeadPos.contains(clickedSlot)) {
//                if (target == null) return;
//                else {
//                    player.sendMessage(target.getDisplayName() + "(???)??? ?????????????????????.");
//                    object = ABCommand.playerUUID_to_face(target.getUniqueId());
//                }
//            } else return;
//
//            int _value = after_vote.getOrDefault(object, 0);
//            if (JobAPI.getJob(player).equals(Jobs.JUDGE)) {
//                after_vote.put(object, _value+3);
//                vote_availableCount_map.put(player.getUniqueId(), 0);
//                total_voteCount += 3;
//            } else {
//                after_vote.put(object, _value+1);
//                vote_availableCount_map.put(player.getUniqueId(), 0);
//                total_voteCount += 1;
//            }
//
//            if (object != null) {
//                isVoteEnded_forPlayer.put(player.getUniqueId(), true);
//
//                if (!isVoteEnded_forPlayer.values().contains(false) && isVoteEnded_forPlayer.keySet().size() == playerList.size()) {
//                    waitTitle.cancel();
//                    new BukkitRunnable(){
//                        @Override
//                        public void run(){
//                            Bukkit.getPluginManager().callEvent(new EventOfVoteResult(after_vote));
//                        }
//                    }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 60);
//                }
//
//                player.closeInventory();
//            } else {
//                player.sendTitle("", "??c?????? ????????? ???????????? ???????????????.", 5, 30, 5);
//                new BukkitRunnable(){
//                    @Override
//                    public void run(){
//                        player.openInventory(event.getView());
//                    }
//                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);
//            }
//        }
//    }
//}