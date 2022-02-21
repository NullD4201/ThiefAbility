package jinu.nulld.bar;

import jinu.nulld.ThiefAB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShiftToggle implements Listener {
    public static Map<UUID, Integer> playerSneakCount = new HashMap<>();

    @EventHandler
    public void onShiftToggle(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (playerSneakCount.getOrDefault(player.getUniqueId(), 0) > 3) playerSneakCount.put(player.getUniqueId(), 0);
        playerSneakCount.put(player.getUniqueId(), playerSneakCount.getOrDefault(player.getUniqueId(), 0)+1);
        new BukkitRunnable(){
            @Override
            public void run(){
                if (playerSneakCount.getOrDefault(player.getUniqueId(), 0) == 4) {
                    if (!InstructionBar.instructionEnabled.getOrDefault(player.getUniqueId(), true)) {
                        InstructionBar.instructionEnabled.put(player.getUniqueId(), true);
                        InstructionBar.instructionBar.get(player.getUniqueId()).setVisible(true);
                        InstructionBar.instructionBar.get(player.getUniqueId()).addPlayer(player);
                    } else {
                        InstructionBar.instructionEnabled.put(player.getUniqueId(), false);
                        InstructionBar.instructionBar.get(player.getUniqueId()).setVisible(false);
                        InstructionBar.instructionBar.get(player.getUniqueId()).removePlayer(player);
                    }
                }
                cancel();
            }
        }.runTaskTimerAsynchronously(ThiefAB.getPlugin(ThiefAB.class), 0, 2);
        new BukkitRunnable(){
            @Override
            public void run(){
                if (playerSneakCount.getOrDefault(player.getUniqueId(), 0) != 4) playerSneakCount.put(player.getUniqueId(), 0);
                cancel();
            }
        }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 10);
    }
}
