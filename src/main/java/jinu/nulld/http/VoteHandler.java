package jinu.nulld.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jinu.nulld.ABCommand;
import jinu.nulld.jobs.Jobs;
import jinu.nulld.vote.VoteResult;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static jinu.nulld.vote.Vote.playerList;

public class VoteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> queryMap = new HashMap<>();
            if (exchange.getRequestURI().getQuery() != null) {
                for (String string : exchange.getRequestURI().getQuery().split("&")) {
                    queryMap.put(string.split("=")[0], string.split("=")[1]);
                }
            }

            Map<UUID, Jobs> jobMap = Jobs.jobMap;
            Map<String, Integer> after_vote = VoteResult.voteResult;

            String content = "";

            boolean result = queryMap.containsKey("step") && queryMap.get("step").equals("result");

            List<String> joArr = new ArrayList<>();
            for (UUID uuid : jobMap.keySet()) {
                joArr.add("{\"displayName\":\""+Bukkit.getPlayer(uuid).getDisplayName()+"\","
                        + "\"faceID\":\""+ABCommand.playerUUID_to_face(uuid)+"\","
                        + "\"job\":\""+jobMap.get(uuid).getJobName()+"\","
                        + "\"isValid\":"+playerList.contains(uuid)+""
                        + (result ? ",\"voteResult\":"+after_vote.getOrDefault(uuid.toString(), 0).toString() : "")
                        + "}");
            }
            content = "{\"users\":"+joArr+""
                    + (result ? ",\"skipVotes\":"+after_vote.getOrDefault("skip", 0) : "")
                    + "}";

            // Encoding to UTF-8
            ByteBuffer bb = StandardCharsets.UTF_8.encode(content);
            int contentLength = bb.limit();
            byte[] contentL = new byte[contentLength];
            bb.get(contentL, 0, contentLength);

            // Set Response Headers
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Cross-Origin", "*");
            headers.add("Content-Type", "application/json;charset=UTF-8");
//            headers.add("Content-Length", String.valueOf(contentLength));

            // Send Response Headers
            exchange.sendResponseHeaders(200, contentLength);

            OutputStream respBody = exchange.getResponseBody();

            respBody.write(content.getBytes(StandardCharsets.UTF_8));

            // Close Stream
            // 반드시, Response Header를 보낸 후에 닫아야함
            respBody.close();

        } catch ( IOException e ) {
            e.printStackTrace();

            if( exchange.getResponseBody() != null ) {
                exchange.getResponseBody().close();
            }
        } finally {
            exchange.close();
        }
    }
}
