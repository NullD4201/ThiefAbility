package jinu.nulld.gui;

import jinu.nulld.ThiefAB;
import jinu.nulld.Voice;
import jinu.nulld.ability.AbilityEndUseEvent;
import jinu.nulld.jobs.IsThief;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GUIEvent implements Listener {
    public static List<Player> counselSelect = new ArrayList<>();
    public static Player judgeSelect = null;
    public static Player gangSelect = null;
    public static Player hackerSelect = null;
    public static Player agentSelect = null;
    private static Map<Player, Boolean> _tempGood = new HashMap<>();

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().contains("의 인벤토리")) {
            event.setCancelled(true);
        }
        if (event.getClickedInventory() != null && event.getView().getTitle().equals("능력을 사용할 대상 선택")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            ItemStack cannot = new ItemStack(Material.BARRIER, 1);
            ItemMeta cannotMeta = cannot.getItemMeta();
            cannotMeta.setDisplayName("§c2명 이상 선택할 수 없습니다.");
            cannotMeta.setLore(Collections.singletonList("§6다른 사람을 선택취소 후 다시 선택하세요."));
            cannot.setItemMeta(cannotMeta);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE)) return;
            ItemStack item = event.getCurrentItem();
            String name = item.getItemMeta().getDisplayName().replaceAll("§f", "").split(" <")[0];
            List<String> selected = Collections.singletonList("§7( 선택됨 )");
            List<String> notSelected = new ArrayList<>();

            if (JobAPI.getJob(player).equals(Jobs.HACKER)) {
                for (ItemStack itemStack : event.getClickedInventory().getContents()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLore(notSelected);
                    itemStack.setItemMeta(itemMeta);
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (gangSelect == null) {
                    itemMeta.setLore(selected);
                    gangSelect = Bukkit.getPlayer(name.replaceAll(" ", ""));
                } else {
                    itemMeta.setLore(notSelected);
                    gangSelect = null;
                }
                item.setItemMeta(itemMeta);
            }
            if (JobAPI.getJob(player).equals(Jobs.GANG)) {
                for (ItemStack itemStack : event.getClickedInventory().getContents()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLore(notSelected);
                    itemStack.setItemMeta(itemMeta);
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (gangSelect == null) {
                    itemMeta.setLore(selected);
                    gangSelect = Bukkit.getPlayer(name.replaceAll(" ", ""));
                } else {
                    itemMeta.setLore(notSelected);
                    gangSelect = null;
                }
                item.setItemMeta(itemMeta);
            }
            if (JobAPI.getJob(player).equals(Jobs.AGENT)) {
                for (ItemStack itemStack : event.getClickedInventory().getContents()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLore(notSelected);
                    itemStack.setItemMeta(itemMeta);
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (agentSelect == null) {
                    itemMeta.setLore(selected);
                    agentSelect = Bukkit.getPlayer(name.replaceAll(" ", ""));
                } else {
                    itemMeta.setLore(notSelected);
                    agentSelect = null;
                }
                item.setItemMeta(itemMeta);
            }
            if (JobAPI.getJob(player).equals(Jobs.COUNSEL)) {
                ItemMeta itemMeta = item.getItemMeta();
                Player toGet = Bukkit.getPlayer(name);

                if (counselSelect.contains(toGet)) {
                    itemMeta.setLore(notSelected);
                    counselSelect.remove(toGet);
                } else {
                    if (counselSelect.size() < 2) {
                        itemMeta.setLore(selected);
                        counselSelect.add(toGet);
                    } else {
                        event.setCurrentItem(cannot);
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                event.setCurrentItem(item);
                            }
                        }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 30);
                    }
                }
                item.setItemMeta(itemMeta);
            }
        }
    }
//
//    private void please_select_person(Player eventPlayer, InventoryView toReOpen) {
//        eventPlayer.sendTitle("", "§c능력을 사용할 대상을 선택하세요", 5, 30, 5);
//        new BukkitRunnable(){
//            @Override
//            public void run(){
//                eventPlayer.openInventory(toReOpen);
//            }
//        }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);
//    }
    private void cancelUse(Player eventPlayer) {
        eventPlayer.sendTitle("", "§c능력 사용을 취소합니다.", 5, 30, 5);

        Bukkit.getPluginManager().callEvent(new AbilityEndUseEvent(JobAPI.getJob(eventPlayer)));
    }
    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("능력을 사용할 대상 선택")) {
            InventoryView view = event.getView();
            Player player = (Player) event.getPlayer();

            if (JobAPI.getJob(player).equals(Jobs.COUNSEL)) {
                if (counselSelect.size() != 2) {
//                    player.sendTitle("", "§c2명을 선택해야 합니다.", 5, 30, 5);
//                    new BukkitRunnable(){
//                        @Override
//                        public void run(){
//                            player.openInventory(view);
//                        }
//                    }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);
                    cancelUse(player);
                    return;
                } else {
//                    boolean exist = (IsThief.booleanThief(counselSelect.get(0)) || IsThief.booleanThief(counselSelect.get(1)));
//                    if (exist) player.sendTitle("§e"+counselSelect.get(0).getName()+"   "+counselSelect.get(1).getName(), "§a2명 중에 괴도가 §l있습니다", 5, 30, 5);
//                    else player.sendTitle("§e"+counselSelect.get(0).getName()+"   "+counselSelect.get(1).getName(), "§c2명 중에 괴도가 §l없습니다", 5, 30, 5);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ft usecounsel " + counselSelect.get(0).getDisplayName() + " " + counselSelect.get(1).getDisplayName());

                            judgeSelect = null;
                        }
                    }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 2);
                }
            }
            if (JobAPI.getJob(player).equals(Jobs.HACKER)) {
                if (hackerSelect == null) {
                    cancelUse(player);
                    return;
                }

                player.sendTitle("§2해킹중...", "", 5, 30, 5);
                double pp = Math.random()*100.0;
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if (pp < 50) {
                            if (IsThief.booleanThief(hackerSelect)) player.sendTitle("§a해킹에 성공했습니다.", "§e" + hackerSelect.getDisplayName() + "§f님은 괴도가 맞습니다.", 5, 30, 5);
                            else player.sendTitle("§a해킹에 성공했습니다.", "§e" + hackerSelect.getDisplayName() + "§f님은 괴도가 아닙니다.", 5, 30, 5);
                        } else player.sendTitle("§c해킹에 실패했습니다.", "", 5, 30, 5);
                    }
                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);

                hackerSelect = null;
            }
            if (JobAPI.getJob(player).equals(Jobs.GANG)) {
                if (gangSelect == null) {
                    cancelUse(player);
                    return;
                }

                Voice.mute(gangSelect);
                player.sendTitle("§6능력을 사용했습니다", "§e"+gangSelect.getDisplayName()+"§6님은 이번 회차동안 음소거됩니다.", 5, 30, 5);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.equals(player)) p.sendTitle("§6깡패가 능력을 사용했습니다", "§e"+gangSelect.getDisplayName()+"§6님은 이번 회차동안 음소거됩니다.", 5, 30, 5);
                }

                gangSelect = null;
            }
            if (JobAPI.getJob(player).equals(Jobs.AGENT)) {
                if (agentSelect == null) {
                    cancelUse(player);
                    return;
                }

                player.sendTitle("", "§e"+agentSelect.getDisplayName()+"의 인벤토리를 확인합니다.", 5, 30, 5);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        player.openInventory(GUI.playerInv(agentSelect));

                        agentSelect = null;
                    }
                }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 40);
            }
        }
    }
}
