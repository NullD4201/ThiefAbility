//package jinu.nulld.bar;
//
//import org.bukkit.Bukkit;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarFlag;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
//import org.bukkit.entity.Player;
//
//public class ResultBar {
//    public static BossBar voteResult = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("voteResult"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//    public static BossBar bs1 = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("result.bar1.title"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//    public static BossBar bs2 = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("result.bar2.title"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//    public static BossBar bs3 = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("result.bar3.title"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//    public static BossBar bs4 = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("result.bar4.title"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//    public static BossBar bs5 = Bukkit.createBossBar(InstructionBar.unicodeString_byKey("result.bar5.title"), BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
//
//    public static void register() {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            voteResult.addPlayer(player);
//            bs1.addPlayer(player);
//            bs2.addPlayer(player);
//            bs3.addPlayer(player);
//            bs4.addPlayer(player);
//            bs5.addPlayer(player);
//        }
//    }
//}
