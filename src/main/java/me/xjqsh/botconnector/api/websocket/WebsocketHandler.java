package me.xjqsh.botconnector.api.websocket;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketHandler {

    private final static Map<String, WsContext> subscribers = new ConcurrentHashMap<>();
    public static void events(WsConfig ws) {
        // Unsubscribe clients that disconnect
        ws.onClose(ctx -> {
            subscribers.remove(clientHash(ctx));
        });

        // Unsubscribe any subscribers that error out
        ws.onError(ctx -> {
            subscribers.remove(clientHash(ctx));
        });

        // Allow sending of commands
        ws.onMessage(ctx -> {

        });
    }

    /**
     * Sends the specified message (as JSON) to all subscribed clients.
     *
     * @param message Object can be any Jackson/JSON serializable object
     */
    public static void broadcast(Object message) {
        subscribers.values().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
            session.send(message);
        });
    }

    /**
     * Generate a unique hash for this subscriber using its connection properties
     *
     * @param ctx
     * @return String the hash
     */
    private static String clientHash(WsContext ctx) {
        return String.format("sub-%s-%s", ctx.host(), ctx.getSessionId());
    }
}
