package jinu.nulld.vote;

import jinu.nulld.MySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class VoteDB {

    private MySQL db;

    VoteDB() {
        this.db = new MySQL();
    }

    public void getVoteList() {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            Statement stmt = conn.createStatement();

            String sql = "select * from VoteSet;";

            ResultSet rs = stmt.executeQuery(sql);

            Map<Integer, Integer> votes = new HashMap<>();

            while(rs.next()) {
                Integer voteId = rs.getInt(1);
                Integer status = rs.getInt(2);
            }

            stmt.close();
            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
