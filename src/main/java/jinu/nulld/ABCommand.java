package jinu.nulld;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.xdevapi.DatabaseObject;
import jinu.nulld.ability.AbilityStartUseEvent;
import jinu.nulld.flow.GameState;
import jinu.nulld.jobs.Jobs;
import jinu.nulld.vote.VoteDB;
import jinu.nulld.vote.VoteData;
import jinu.nulld.vote.VoteUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.sql.*;
import java.util.*;

import static jinu.nulld.ThiefAB.LOGGER;

public class ABCommand implements TabExecutor {
    public static Map<UUID, Jobs> jobMap = Jobs.jobMap;

    public static boolean isNumeric(String string) {
        if (string == null) return false;
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static Map<String, UUID> face_to_playerUUID = new HashMap<>();
    public static String playerUUID_to_face(UUID uuid) {
        String toReturn = null;
        for (String string : face_to_playerUUID.keySet()) {
            if (face_to_playerUUID.get(string).equals(uuid)) {
                toReturn = string;
                break;
            }
        }
        return toReturn;
    }
    public static String playerUUID_to_face(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return playerUUID_to_face(uuid);
    }
    FileConfiguration config = YamlConfiguration.loadConfiguration(new File(ThiefAB.getPlugin(ThiefAB.class).getDataFolder(), "dbconfig.yml"));
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("dbtest")) {

            // ?????? ??????
            List<VoteUser> users = new ArrayList<>();
            users.add(new VoteUser(0, 0, "??????", "??????ID", "??????", true, 0));
            VoteDB voteDB = new VoteDB();
            VoteData myVote = voteDB.createVote(users);

            // ?????? ????????? ????????????
            Map<Integer, Integer> voteList = voteDB.getVoteList();

            // ?????? ?????? (?????? ??????)
            voteDB.updateVote(myVote.getVoteId(), true, myVote.getSkipVotes());

            // ?????? ?????? (?????? ??????)
            voteDB.updateVote(myVote.getVoteId(), myVote.isResult(), myVote.getSkipVotes() + 1);

            // ?????? ??????
            List<VoteUser> myUser = new ArrayList<>(voteDB.getVoteByID(myVote.getVoteId()).getVoteUsers().values());

            // ?????? ?????? (??????) << ???????????? ?????? ??? ?????? ??????????????????
            voteDB.updateUser(myUser.get(0).getUserId(), new VoteUser(
                    myUser.get(0).getUserId(),
                    myUser.get(0).getVoteId(),
                    myUser.get(0).getDisplayName(),
                    myUser.get(0).getFaceId(),
                    myUser.get(0).getJob(),
                    myUser.get(0).isValid(),
                    myUser.get(0).getVoteResult()+1
            ));

            // ?????? ??????
            voteDB.deleteVote(myVote.getVoteId());

            // dbtest ?????????
        }
        if (label.equalsIgnoreCase("??????") && player.isOp()) {
            StringBuilder string = new StringBuilder();
            for (String s : args) string.append(" ").append(s);

            for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage("??7[??b??????????????7]??d" + string);
        }
        if (label.equalsIgnoreCase("thab") && player.isOp()) {
            if (args[0].equalsIgnoreCase("setbar")) {
                ThiefAB.bartitle = YamlConfiguration.loadConfiguration(new File(ThiefAB.getPlugin(ThiefAB.class).getDataFolder(), "bartitle.yml"));
            } else if (args[0].equalsIgnoreCase("setface") && args.length == 3) {
                String playerName = args[1];
                String num = args[2];
                if (!isNumeric(num)) return false;

                Player toSet = Bukkit.getPlayer(playerName);
                if (toSet == null) return false;

                int number = Integer.parseInt(num);
                if (number < 1 || number > 8) return false;
                String face = "face"+num;

                face_to_playerUUID.put(face, toSet.getUniqueId());
                player.sendMessage("Set player "+toSet.getName()+" to "+face);
            }
        }
        if (label.equalsIgnoreCase("??????")) {
            if (args[0].equalsIgnoreCase("debug")) {
                GameState.setGameState(GameState.VOTING);
            }
            if (args[0].equalsIgnoreCase("??????")) {
                if (jobMap.get(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l?????? : &e&l"+jobMap.get(player.getUniqueId()).getJobName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l?????? : &e&l"+jobMap.get(player.getUniqueId()).getJobDescription()));
                }
            } else if (args[0].equalsIgnoreCase("??????")) {
                if (GameState.getNowState() == GameState.DISCUSS) {
                    if (jobMap.get(player.getUniqueId()) != null) {
                        if (jobMap.get(player.getUniqueId()).equals(Jobs.COUNSEL)) { // ??????
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.COUNSEL);
                            Bukkit.getPluginManager().callEvent(event);
                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.GANG)) { // ??????
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.GANG);
                            Bukkit.getPluginManager().callEvent(event);
                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.AGENT)) { // ????????????
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.AGENT);
                            Bukkit.getPluginManager().callEvent(event);
                        } else if (jobMap.get(player.getUniqueId()).equals(Jobs.HACKER)) { // ??????
                            AbilityStartUseEvent event = new AbilityStartUseEvent(Jobs.HACKER);
                            Bukkit.getPluginManager().callEvent(event);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c???????????? ????????? ??? ?????? ????????? ????????????."));
                            return false;
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c????????? ????????? ????????????."));
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c??????????????? ????????????."));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("??????")) {
                player.performCommand("ft ablist");
            }
        }
        if (label.equalsIgnoreCase("chat")) {
            List<String> _list = Arrays.asList("LAW", "PARK", "BANK", "??????", "??????", "??????", "ADMIN", "?????????", "NONE", "??????");
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
        if (label.equalsIgnoreCase("??????")) {
            ItemStack book = new ItemStack(Material.WRITABLE_BOOK, 1);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setDisplayName("?????????");
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
            else if (!args[1].equalsIgnoreCase("LAW") && !args[1].equalsIgnoreCase("PARK") && !args[1].equalsIgnoreCase("BANK") && !args[1].equalsIgnoreCase("??????") && !args[1].equalsIgnoreCase("??????") && !args[1].equalsIgnoreCase("??????"))
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
