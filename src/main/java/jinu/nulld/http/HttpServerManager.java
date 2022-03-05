package jinu.nulld.http;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main Class
 */
public class HttpServerManager {
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int DEFAULT_PORT = 4546;
    private final int DEFAULT_BACKLOG = 0;
    private HttpServer server = null;

    /**
     * 생성자
     */
    public HttpServerManager() throws IOException {
        createServer(DEFAULT_HOSTNAME, DEFAULT_PORT);
    }
    public HttpServerManager(int port) throws IOException {
        createServer(DEFAULT_HOSTNAME, port);
    }
    public HttpServerManager(String host, int port) throws IOException {
        createServer(host, port);
    }

    /**
     * 서버 생성
     */
    private void createServer(String host, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(host, port), DEFAULT_BACKLOG);
        server.createContext("/vote", new VoteHandler());
        server.createContext("/voteresult", new VoteResultHandler());
        server.createContext("/judge", new JudgeHandler());
        server.createContext("/judgeresult", new JudgeHandler());
    }

    /**
     * 서버 실행
     */
    public void start() {
        server.start();
    }

    /**
     * 서버 중지
     */
    public void stop(int delay) {
        server.stop(delay);
    }

//    /**
//     * Sub Class
//     */
//    class RootHandler implements HttpHandler {
//
//
//    }
}