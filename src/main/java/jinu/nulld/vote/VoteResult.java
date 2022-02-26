package jinu.nulld.vote;

import jinu.nulld.ABCommand;
import jinu.nulld.ThiefAB;
import jinu.nulld.flow.EventOfVoteResult;
import jinu.nulld.flow.ResultShowEndEvent;
import jinu.nulld.flow.VoteEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VoteResult implements Listener{
//    public static void barShow(boolean b) {
//        ResultBar.voteResult.setVisible(b);
//        ResultBar.bs1.setVisible(b);
//        ResultBar.bs2.setVisible(b);
//        ResultBar.bs3.setVisible(b);
//        ResultBar.bs4.setVisible(b);
//        ResultBar.bs5.setVisible(b);
//    }
//
//    private static String normalBarTitle(String original, String side, int count, String enable_upper, String enable_lower, String disable_upper, String disable_lower) {
//        String[] original_split = original.split("\uf829");
//        String left = original_split[0];
//        String right = original_split[1];
//        StringBuilder toReturn = new StringBuilder();
//
//        if (side.equalsIgnoreCase("left")) {
//            String[] toChange_set = left.split("\uf80B");
//
//            String toChange_upper = toChange_set[0];
//            String toChange_lower = toChange_set[1];
//            String[] toChange_upper_set = toChange_upper.split(disable_upper);
//            String[] toChange_lower_set = toChange_lower.split(disable_lower);
//
//            if (count <= 5) {
//                for (int i = 0; i < 5; i++) {
//                    if (i < count) toReturn.append(toChange_upper_set[i]).append(enable_upper);
//                    else toReturn.append(toChange_upper_set[i]).append(disable_upper);
//                }
//                toReturn.append("\uf80B").append(toChange_lower);
//            } else {
//                toReturn.append(toChange_upper.replaceAll(disable_upper, enable_upper));
//                toReturn.append("\uf80B");
//                for (int i = 5; i < 10; i++) {
//                    if (i < count) toReturn.append(toChange_lower_set[i-5]).append(enable_lower);
//                    else toReturn.append(toChange_lower_set[i-5]).append(disable_lower);
//                }
//            }
//
//            toReturn.append("\uf829").append(right);
//        } else if (side.equalsIgnoreCase("right")) {
//            String[] toChange_set = right.split("\uf80B");
//
//            String toChange_upper = toChange_set[0];
//            String toChange_lower = toChange_set[1];
//            String[] toChange_upper_set = toChange_upper.split(disable_upper);
//            String[] toChange_lower_set = toChange_lower.split(disable_lower);
//
//            if (count <= 5) {
//                for (int i = 0; i < 5; i++) {
//                    if (i < count) toReturn.append(toChange_upper_set[i]).append(enable_upper);
//                    else toReturn.append(toChange_upper_set[i]).append(disable_upper);
//                }
//                toReturn.append("\uf80B").append(toChange_lower);
//            } else {
//                toReturn.append(toChange_upper.replaceAll(disable_upper, enable_upper));
//                toReturn.append("\uf80B");
//                for (int i = 5; i < 10; i++) {
//                    if (i < count) toReturn.append(toChange_lower_set[i-5]).append(enable_lower);
//                    else toReturn.append(toChange_lower_set[i-5]).append(disable_lower);
//                }
//            }
//
//            StringBuilder leftBuilder = new StringBuilder(left);
//            leftBuilder.append("\uf829").append(toReturn);
//
//            toReturn = leftBuilder;
//        }
//
//        return toReturn.toString();
//    }
//    private static String skipBarTitle(String original, int count, String enable, String disable) {
//        String[] original_split = original.split(disable);
//        StringBuilder toReturn = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            if (i < count) toReturn.append(original_split[i]).append(enable);
//            else toReturn.append(original_split[i]).append(disable);
//        }
//
//        return toReturn.toString();
//    }
//    private static void normalSetTitle(BossBar bar, String side, Map<String, Integer> map, String face, int barNum) {
//        bar.setTitle(InstructionBar.unicodeString_byString(normalBarTitle(bar.getTitle(),
//                side,
//                map.getOrDefault(face, 0),
//                bartitle.getString("result.bar"+barNum+".enable.upper"),
//                bartitle.getString("result.bar"+barNum+".enable.lower"),
//                bartitle.getString("result.bar"+barNum+".disable.upper"),
//                bartitle.getString("result.bar"+barNum+".disable.lower")
//                )));
//    }
    public static Map<String, Integer> voteResult;
    @EventHandler
    public void onResult(EventOfVoteResult event) {
        Map<String, Integer> after_vote = event.getMap();
        voteResult = after_vote;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.performCommand("voteresult");
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                Bukkit.getPluginManager().callEvent(new ResultShowEndEvent(after_vote));
            }
        }.runTaskLater(ThiefAB.getPlugin(ThiefAB.class), 200);
    }

    @EventHandler
    public void onShowEnd(ResultShowEndEvent event) {
        Map<String, Integer> after_vote = event.getMap();
        List<String> resultList = new ArrayList<>(); // 투표 결과 최상위 득표자 리스트

        List<String> _result = new ArrayList<>(after_vote.keySet());
        _result.sort((o1, o2) -> after_vote.get(o2).compareTo(after_vote.get(o1)));

        int _temp = after_vote.get(_result.get(0));
        resultList.add(_result.get(0));

        for (int i = 1; i < _result.size(); i++) {
            if (after_vote.get(_result.get(i)) > _temp) {
                resultList = new ArrayList<>();
                resultList.add(_result.get(i));
                _temp = after_vote.get(_result.get(i));
            } else if (after_vote.get(_result.get(i)) == _temp) {
                resultList.add(_result.get(i));
            }
        }

        if (resultList.contains("skip")) resultList = Collections.singletonList("skip");
        else {
            List<String> list = new ArrayList<>();
            for (String string : resultList) {
                if (ABCommand.face_to_playerUUID.get(string) != null) list.add(ABCommand.face_to_playerUUID.get(string).toString());
            }
            resultList = list;
        }

        VoteEndEvent voteEndEvent = new VoteEndEvent();
        voteEndEvent.setResult(resultList);
        Bukkit.getPluginManager().callEvent(voteEndEvent);
        Bukkit.getConsoleSender().sendMessage(resultList.toString());

        Vote.playerList = new ArrayList<>();
        Vote.endVoteList = new ArrayList<>();
    }

//    private static void setTitle(BossBar bar, int num, Map<String, Integer> map){
//        bar.setTitle(InstructionBar.unicodeString_byString(count_object_and_change_from_disable_to_enable__LeftOrRight("face"+num, num, map.getOrDefault("face"+num, 0))));
//        bar.setTitle(InstructionBar.unicodeString_byString(count_object_and_change_from_disable_to_enable__LeftOrRight("face"+(num+4), num, map.getOrDefault("face"+(num+4), 0))));
//    }
//
//    private static String count_skip_and_change_from_disable_to_enable(String title, int count) {
//        for (int i = 0; i < count; i++) {
//            title = title.replaceFirst("\uf045", "\uf015");
//        }
//
//        return title;
//    }
//
//    private static String count_object_and_change_from_disable_to_enable__LeftOrRight(String face, int num, int count) {
//        String result;
//        Direction where = Direction.LEFT;
//        if (face.equals("face5") || face.equals("face6") || face.equals("face7") || face.equals("face8")) where = Direction.RIGHT;
//
//        String left_title = bartitle.getString("result.bar"+num+".title").substring(0, 150);
//        String right_title = bartitle.getString("result.bar"+num+".title").substring(156);
//        switch (where){
//            case LEFT:
//                left_title = getString(num, count, left_title);
//            case RIGHT:
//                right_title = getString(num, count, right_title);
//        }
//
//        result = left_title+"\uf829"+right_title;
//        return result;
//    }
//
//    private static String getString(int num, int count, String _title) {
//        if (count <= 5) {
//            for (int i = 0; i < count; i++) _title = _title.replaceFirst(bartitle.getString("result.bar"+num+".disable.upper"), bartitle.getString("result.bar"+num+".enable.upper"));
//        } else {
//            for (int i = 0; i < 5; i++) _title = _title.replaceFirst(bartitle.getString("result.bar"+num+".disable.upper"), bartitle.getString("result.bar"+num+".enable.upper"));
//            for (int i = 5; i < count; i++) _title = _title.replaceFirst(bartitle.getString("result.bar"+num+".disable.lower"), bartitle.getString("result.bar"+num+".enable.lower"));
//        }
//        return _title;
//    }

}

//enum Direction {
//    LEFT,
//    RIGHT
//}