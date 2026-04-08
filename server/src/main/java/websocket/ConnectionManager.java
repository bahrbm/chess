package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        System.out.println(msg);
        List<Session> sessions = connections.get(gameID);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
