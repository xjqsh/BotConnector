package me.xjqsh.botconnector.api.websocket;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import me.xjqsh.botconnector.BotConnector;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketHandler {
    private final static Map<String, WsContext> subscribers = new ConcurrentHashMap<>();
    public static void events(WsConfig ws) {
        ws.onConnect(ctx -> {
            subscribers.put(clientHash(ctx), ctx);
        });

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
            String msg = ctx.message();
            Bukkit.getScheduler().scheduleSyncDelayedTask(BotConnector.getInstance(),()->{
                Bukkit.broadcast(Component.text(msg));
            });
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

    public static void broadcastAsync(Object message){
        Bukkit.getScheduler().runTaskAsynchronously(BotConnector.getInstance(),
                ()->WebsocketHandler.broadcast(message)
        );
    }

    private static String clientHash(WsContext ctx) {
        return String.format("sub-%s-%s", ctx.host(), ctx.getSessionId());
    }
}
