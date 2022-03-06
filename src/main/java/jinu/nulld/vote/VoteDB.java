package jinu.nulld.vote;

import jinu.nulld.MySQL;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jinu.nulld.ThiefAB.LOGGER;

public class VoteDB {

    private MySQL db;

    public VoteDB() {
        this.db = new MySQL();
    }

    /**
     * 활성화된 투표의 리스트를 불러옵니다.
     * @return 투표의 ID가 key, 투표가 결과가 나왔는지 여부가 value인 Map을 반환합니다.
     */
    public Map<Integer, Integer> getVoteList() {
        Map<Integer, Integer> votes = new HashMap<>();

        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            Statement stmt = conn.createStatement();

            String sql = "select * from VoteSet;";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                Integer voteId = rs.getInt(1);
                Integer isResult = rs.getInt(2); // 0 or 1 > 1 is result
                votes.put(voteId, isResult);
            }

            rs.close();
            stmt.close();
            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return votes;
    }

    /**
     * VoteData 객체를 구해옵니다.
     * @param voteID    투표의 ID를 받습니다.
     * @return  VoteData 객체를 반환합니다.
     */
    public VoteData getVoteByID(Integer voteID) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteSet where voteid=?");

            stmt.setInt(1, voteID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer voteId = rs.getInt(1);
                boolean isResult = rs.getBoolean(2);
                Integer skipVotes = rs.getInt(3);
                PreparedStatement stmt2 = conn.prepareStatement("select * from VoteUsers where voteid=?");
                stmt2.setInt(1, voteID);
                ResultSet rs2 = stmt2.executeQuery();
                Map<Integer, VoteUser> users = new HashMap<>();
                while (rs2.next()) {
                    users.put(
                        rs2.getInt(1),
                        new VoteUser(
                            rs2.getInt(1),
                            rs2.getInt(2),
                            rs2.getString(3),
                            rs2.getString(4),
                            rs2.getString(5),
                            rs2.getBoolean(6),
                            rs2.getInt(7)
                        )
                    );
                }
                rs2.close();
                stmt2.close();
                rs.close();
                stmt.close();
                this.db.disconnect();
                return new VoteData(voteId, skipVotes, isResult, users);
            } else {
                LOGGER.warning("Vote is not found");
                rs.close();
                stmt.close();
                this.db.disconnect();
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * VoteUser 객체를 구해옵니다.
     * @param userID    User의 ID를 받습니다.
     * @return  VoteUser 객체를 반환합니다.
     */
    public VoteUser getUserByID(Integer userID) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteUsers where userid=?");

            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VoteUser user = new VoteUser(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getBoolean(6),
                        rs.getInt(7)
                );
                rs.close();
                stmt.close();
                this.db.disconnect();
                return user;
            } else {
                LOGGER.warning("User is not found");
                rs.close();
                stmt.close();
                this.db.disconnect();
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 투표를 생성합니다.
     * @param userList 등록할 유저의 리스트
     * @return 생성된 VoteData 객체
     */
    public VoteData createVote(List<VoteUser> userList) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into VoteSet(result, skipvotes) values(0, 0)");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("select * from VoteSet where result=0 order by voteid desc limit 1");
            ResultSet rs = stmt.executeQuery();
            Integer voteId = 0;
            Map<Integer, VoteUser> userMap = new HashMap<>();
            if (rs.next()) {
                voteId = rs.getInt(1);
                for (VoteUser user : userList) {
                    PreparedStatement stmt2 = conn.prepareStatement("insert into VoteUsers(voteid, displayName, faceid, job, isValid, voteResult) values(?, ?, ?, ?, ?, ?)");
                    stmt2.setInt(1, voteId);
                    stmt2.setString(2, user.getDisplayName());
                    stmt2.setString(3, user.getFaceId());
                    stmt2.setString(4, user.getJob());
                    stmt2.setBoolean(5, user.isValid());
                    stmt2.setInt(6, 0);
                    stmt2.executeUpdate();
                    stmt2.close();
                    userMap.put(user.getUserId(), user);
                }
            } else {
                rs.close();
                stmt.close();
                LOGGER.warning("Vote is not found");
                return null;
            }
            rs.close();
            stmt.close();
            // "INSERT INTO Reservation(ID, Name, ReserveDate, RoomNum) VALUES(5, '이순신', '2016-02-16', 1108);"

            this.db.disconnect();
            return new VoteData(voteId, 0, false, userMap);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 투표의 정보를 갱신합니다. 유저의 갱신은 불가능합니다.
     * @param voteId    갱신할 투표의 ID
     * @param result    투표의 결과 여부
     * @param skipVotes 스킵에 투표한 수
     */
    public void updateVote(Integer voteId, boolean result, Integer skipVotes) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update VoteSet set result=?, skipvotes=? where voteid=?");
            stmt.setBoolean(1, result);
            stmt.setInt(2, skipVotes);
            stmt.setInt(3, voteId);
            stmt.executeUpdate();
            stmt.close();
            this.db.disconnect();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 유저를 갱신합니다. 투표의 갱신은 불가능합니다.
     * @param userid    갱신할 유저의 ID
     * @param user  갱신할 유저 객체
     */
    public void updateUser(Integer userid, VoteUser user) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update VoteUsers set displayName=?, faceid=?, job=?, isValid=?, voteResult=? where userid=?");

            stmt.setString(1, user.getDisplayName());
            stmt.setString(2, user.getFaceId());
            stmt.setString(3, user.getJob());
            stmt.setBoolean(4, user.isValid());
            stmt.setInt(5, user.getVoteResult());
            stmt.setInt(6, userid);

            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 투표를 삭제합니다. 투표에 등록된 유저도 같이 삭제합니다.
     * @param voteId 삭제할 투표의 ID
     */
    public void deleteVote(Integer voteId) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteUsers where voteid=?");
            stmt.setInt(1, voteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PreparedStatement stmt2 = conn.prepareStatement("delete from VoteUsers where userid=?");
                stmt2.setInt(1, rs.getInt(1));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs.close();
            stmt.close();
            stmt = conn.prepareStatement("delete from VoteSet where voteid=?");
            stmt.setInt(1, voteId);
            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
