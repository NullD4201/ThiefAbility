package jinu.nulld.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jinu.nulld.ABCommand;
import jinu.nulld.jobs.Jobs;
import jinu.nulld.judge.Judge;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static jinu.nulld.vote.Vote.playerList;

public class JudgeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String content = "{\"agree\":"+ Judge.agreeInt +","
                        + "\"disagree\":"+Judge.disagreeInt
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
