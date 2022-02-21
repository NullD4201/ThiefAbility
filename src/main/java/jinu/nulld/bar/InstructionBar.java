package jinu.nulld.bar;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static jinu.nulld.ThiefAB.bartitle;

public class InstructionBar implements Listener {
    public static Map<UUID, BossBar> instructionBar = new HashMap<>();
    public static Map<UUID, Boolean> instructionEnabled = new HashMap<>();

    public static String unicodeString_byKey(String key) {
        return "§f" + StringEscapeUtils.unescapeJava(bartitle.getString(key));
    }
    public static String unicodeString_byString(String string) {
        return "§f" + StringEscapeUtils.unescapeJava(string);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        instructionBar.put(player.getUniqueId(), getInstructionBar());
    }

    public static BossBar getInstructionBar() {
        BossBar bar = Bukkit.createBossBar(StringEscapeUtils.unescapeJava(bartitle.getStringList("instruction").get(0)), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        bar.setProgress(1.0);
        bar.setVisible(false);
        return bar;
    }

//    게임설명 좌클릭, 우클릭으로 상호작용
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        if (instructionEnabled.get(player.getUniqueId()) != null && instructionEnabled.get(player.getUniqueId())) {
            String current = instructionBar.get(player.getUniqueId()).getTitle();
            int prev = 0, next = 0;

            for (int i = 0; i < bartitle.getStringList("instruction").size(); i++) {
                if (StringEscapeUtils.unescapeJava(bartitle.getStringList("instruction").get(i)).equalsIgnoreCase(current)) {
                    prev = i - 1;
                    next = i + 1;

                    if (prev < 0) prev = bartitle.getStringList("instruction").size() - 1;
                    if (next == bartitle.getStringList("instruction").size()) next = 0;
                    break;
                }
            }

            if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                BossBar bar = instructionBar.get(player.getUniqueId());
                bar.setTitle(StringEscapeUtils.unescapeJava(bartitle.getStringList("instruction").get(prev)));
                instructionBar.put(player.getUniqueId(), bar);
            }
            if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) && !Objects.requireNonNull(event.getHand()).equals(EquipmentSlot.OFF_HAND)) {
                BossBar bar = instructionBar.get(player.getUniqueId());
                bar.setTitle(StringEscapeUtils.unescapeJava(bartitle.getStringList("instruction").get(next)));
                instructionBar.put(player.getUniqueId(), bar);
            }
        }
    }
}
