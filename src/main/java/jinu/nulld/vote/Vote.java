package jinu.nulld.vote;

import jinu.nulld.ThiefAB;
import jinu.nulld.flow.GameState;
import jinu.nulld.flow.GameStateChangeEvent;
import jinu.nulld.flow.VoteEndEvent;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Vote implements Listener {
    public static List<Player> playerList = new ArrayList<>();
    public static List<Player> voteList = new ArrayList<>();
    public static int total_voteCount = 0; // 전체 플레이어의 투표 수

    public static Map<UUID, Integer> vote_availableCount_map = new HashMap<>(); // 각 플레이어별 남은 투표 가능횟수
    @EventHandler
    public void openVote(GameStateChangeEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!JobAPI.getJob(player).equals(Jobs.NONE) && !player.getGameMode().equals(GameMode.SPECTATOR) && !player.getGameMode().equals(GameMode.CREATIVE)) playerList.add(player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!JobAPI.getJob(player).equals(Jobs.NONE)) voteList.add(player);
        }

        int availableCount_default;
        if (event.getNewState() != null && event.getNewState().equals(GameState.VOTING)) {
            for (Player player : playerList) {
                if (JobAPI.getJob(player).equals(Jobs.JUDGE)) {
                    availableCount_default = 3;
                } else availableCount_default = 1;
                vote_availableCount_map.put(player.getUniqueId(), availableCount_default);
                player.openInventory(GUI.voteGui(voteList));
                Bukkit.getConsoleSender().sendMessage(player.getName());
            }
        }
    }

    public static Map<UUID, Integer> vote_count_map = new HashMap<>();
    public static List<String> resultList = new ArrayList<>(); // 투표 결과 최상위 득표자 리스트
    @EventHandler
    public void onVoteClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("피고인 투표")) {
            ItemStack toVote = event.getCurrentItem();
            int slot = event.getSlot();

            if (event.getClickedInventory() != null && !event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                InventoryView inventory = event.getView();
                int where_are_you = GUI.playerHeadPos.get(voteList.indexOf((Player) event.getWhoClicked()));

                if (toVote != null && toVote.getType().equals(Material.PLAYER_HEAD) && inventory.getItem(where_are_you-1) != null && !inventory.getItem(where_are_you-1).getType().equals(Material.BARRIER) && !inventory.getItem(where_are_you-1).getType().equals(Material.LIME_DYE)) {
                    if (vote_availableCount_map.get(event.getWhoClicked().getUniqueId()) <= 0) return;

                    if (vote_availableCount_map.get(event.getWhoClicked().getUniqueId()) == 1) {
                        inventory.setItem(where_are_you - 1, new ItemStack(Material.LIME_DYE, 1));
                        total_voteCount += 1;
                    }

                    if (inventory.getItem(slot+9) == null || !inventory.getItem(slot+9).getType().equals(Material.PAPER)) inventory.setItem(slot+9, new ItemStack(Material.PAPER, 1));
                    else {
                        ItemStack current = inventory.getItem(slot+9);
                        current.setAmount(current.getAmount()+1);
                        inventory.setItem(slot+9, current);
                    }

                    vote_availableCount_map.put(event.getWhoClicked().getUniqueId(), vote_availableCount_map.get(event.getWhoClicked().getUniqueId())-1);
                }
            }

            if (total_voteCount >= playerList.size()) {
                List<Map.Entry<UUID, Integer>> entryList = new ArrayList<>(vote_count_map.entrySet());
                entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                Bukkit.getConsoleSender().sendMessage(entryList.toString());

                int first = entryList.get(0).getValue();
                for (Map.Entry<UUID, Integer> entry : entryList) {
                    if (entry.getValue() >= first) resultList.add(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).getDisplayName());
                }

                new BukkitRunnable(){
                    @Override
                    public void run(){
                        for (Player player : playerList) player.closeInventory();
                        VoteEndEvent endEvent = new VoteEndEvent();
                        endEvent.setResult(resultList);
                        Bukkit.getPluginManager().callEvent(endEvent);
                        GameState.setGameState(GameState.WAITING);
                    }
                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 60);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVoteClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("피고인 투표")) {
            new BukkitRunnable(){
                @Override
                public void run(){
                    if (GameState.getNowState().equals(GameState.VOTING)) event.getPlayer().openInventory(event.getView());
                    else {
                        for (Player player : playerList) player.closeInventory();
                    }

                    voteList = new ArrayList<>();
                    playerList = new ArrayList<>();
                }
            }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 10);
        }
    }
}
