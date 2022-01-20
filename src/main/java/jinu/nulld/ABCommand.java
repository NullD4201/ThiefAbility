package jinu.nulld;

import jinu.nulld.ability.AbilityStartUseEvent;
import jinu.nulld.flow.GameState;
import jinu.nulld.gui.GUI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ABCommand implements TabExecutor {
    public static Map<UUID, Jobs> jobMap = Jobs.jobMap;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("능력")) {
            if (args[0].equalsIgnoreCase("확인")) {
                if (jobMap.get(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l직업 : &e&l"+jobMap.get(player.getUniqueId()).getJobName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l설명 : &e&l"+jobMap.get(player.getUniqueId()).getJobDescription()));
                }
            } else if (args[0].equalsIgnoreCase("사용")) {
                if (GameState.getNowState() == GameState.DISCUSS) {
                    if (jobMap.get(player.getUniqueId()) != null) {
                        if (jobMap.get(player.getUniqueId()).equals(Jobs.COUNSEL)) { // 검사
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.COUNSEL);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) player.openInventory(GUI.playerList(null));

                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.GANG)) { // 깡패
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.GANG);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) player.openInventory(GUI.playerList(player));

                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.AGENT)) { // 보안요원
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.AGENT);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) player.openInventory(GUI.playerList(player));

                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.HACKER)) { // 해커
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.HACKER);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) player.openInventory(GUI.playerList(player));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c명령어를 입력할 수 있는 권한이 없습니다."));
                            return false;
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c정해진 직업이 없습니다."));
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c토론시간이 아닙니다."));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("목록")) {
                player.performCommand("ft ablist");
            }
        }
        if (label.equalsIgnoreCase("chat")) {
            List<String> _list = Arrays.asList("LAW", "PARK", "BANK", "법원", "공원", "은행", "ADMIN", "운영자", "NONE", "없음");
            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("join")) {
                    if (_list.contains(args[1])) {
                        if (args.length == 3 && Bukkit.getPlayer(args[2]) != null) player = Bukkit.getPlayer(args[2]);
                        ChatChannel.removePlayerFromChannel(player, ChatChannel.LAW);
                        ChatChannel.removePlayerFromChannel(player, ChatChannel.BANK);
                        ChatChannel.removePlayerFromChannel(player, ChatChannel.PARK);
                        ChatChannel.addPlayerToChannel(player, args[1]);
                    } else help(sender, args);
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (_list.contains(args[1])) {
                        if (args.length == 3 && Bukkit.getPlayer(args[2]) != null) player = Bukkit.getPlayer(args[2]);
                        ChatChannel.removePlayerFromChannel(player, args[1]);
                    } else help(sender, args);
                } else help(sender, args);
            } else {
                help(sender, args);
            }
        }
        if (label.equalsIgnoreCase("메모")) {
            ItemStack book = new ItemStack(Material.WRITABLE_BOOK, 1);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setDisplayName("메모장");
            book.setItemMeta(bookMeta);
            player.getInventory().addItem(book);
        }
        return false;
    }

    private void help(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("join") && !args[0].equalsIgnoreCase("leave")) sender.sendMessage("Invalid parameter. (Input : "+args[0]+")");
            else sender.sendMessage("Invalid channel. (Input : "+args[1]+")");
        } else if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("join") && !args[0].equalsIgnoreCase("leave")) sender.sendMessage("Invalid parameter. (Input : "+args[0]+")");
            else if (!args[1].equalsIgnoreCase("LAW") && !args[1].equalsIgnoreCase("PARK") && !args[1].equalsIgnoreCase("BANK") && !args[1].equalsIgnoreCase("법원") && !args[1].equalsIgnoreCase("공원") && !args[1].equalsIgnoreCase("은행"))
                sender.sendMessage("Invalid channel. (Input : "+args[1]+")");
            else sender.sendMessage("Invalid player name. (Input : "+args[2]+")");
        } else  {
            sender.sendMessage("/chat join <channel name> [player name]");
            sender.sendMessage("/chat leave <channel name> [player name]");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
