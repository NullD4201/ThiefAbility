package jinu.nulld.gui;

import com.sun.istack.internal.Nullable;
import jinu.nulld.jobs.IsThief;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GUI {
    public static ItemStack head(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        Objects.requireNonNull(headMeta).setOwningPlayer(player);
        headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f"+player.getDisplayName()+" &f< &6"+JobAPI.getJob(player).getJobName()+" &f>"));
        head.setItemMeta(headMeta);

        return head;
    }

    public static Inventory playerInv(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, player.getDisplayName()+"의 인벤토리");
        ItemStack separator = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta sMeta = separator.getItemMeta();
        Objects.requireNonNull(sMeta).setDisplayName(" ");
        separator.setItemMeta(sMeta);
        List<Integer> sepList = Arrays.asList(0, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        for (int i : sepList) inventory.setItem(i, separator);

        setItem(inventory, 1, player.getInventory().getHelmet());
        setItem(inventory, 2, player.getInventory().getChestplate());
        setItem(inventory, 3, player.getInventory().getLeggings());
        setItem(inventory, 4, player.getInventory().getBoots());
        setItem(inventory, 7, player.getInventory().getItemInOffHand());
        for (int i = 0; i < 36; i++) {
            setItem(inventory, i + 18, player.getInventory().getItem(i));
        }

        return inventory;
    }
    static void setItem(Inventory inventory, int slot, ItemStack itemStack) {
        if (itemStack != null) inventory.setItem(slot, itemStack);
    }

    public static Inventory playerList(@Nullable Player except) {
        Inventory inventory = Bukkit.createInventory(null, 9, "능력을 사용할 대상 선택");
        ItemStack separator = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta sMeta = separator.getItemMeta();
        Objects.requireNonNull(sMeta).setDisplayName(" ");
        separator.setItemMeta(sMeta);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, separator);
        }

        List<Player> nonAdmin = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!JobAPI.getJob(player).equals(Jobs.NONE) && !IsThief.isRevealed(player)) nonAdmin.add(player);
        }
        if (except != null) {
            nonAdmin.remove(except);
            for (int i = 0; i < nonAdmin.size(); i++) {
                inventory.setItem(i+1, head(nonAdmin.get(i)));
            }
        } else {
            for (int i = 0; i < nonAdmin.size(); i++) {
                inventory.setItem(i, head(nonAdmin.get(i)));
            }
        }

        return inventory;
    }
}