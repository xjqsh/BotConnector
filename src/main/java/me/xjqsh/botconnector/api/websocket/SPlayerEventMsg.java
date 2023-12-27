package me.xjqsh.botconnector.api.websocket;

import com.google.gson.annotations.Expose;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.xjqsh.botconnector.database.SQLiteDB;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SPlayerEventMsg {
    public enum Type{
        PLAYER_JOIN("player_join"),
        PLAYER_LEAVE("player_leave"),
        CHAT("chat");
        final String idf;
        Type(String idf) {
            this.idf = idf;
        }
    }
    @Expose
    private String player = "";
    @Expose
    private String qnum = "-1";
    @Expose
    private Type type;
    @Expose
    private String msg = "";
    private SPlayerEventMsg(Type type){
        this.type = type;
    }

    public static SPlayerEventMsg build(AsyncChatEvent chatEvent){
        SPlayerEventMsg wsMsg = new SPlayerEventMsg(Type.CHAT);
        wsMsg.player = chatEvent.getPlayer().getName();

        String cache = SQLiteDB.getByUUID(chatEvent.getPlayer().getUniqueId());
        if(cache!=null){
            wsMsg.qnum = cache;
        }

        if(chatEvent.message() instanceof TextComponent){
            wsMsg.msg = ((TextComponent) chatEvent.message()).content();
        }

        return wsMsg;
    }

    public static SPlayerEventMsg build(PlayerJoinEvent chatEvent){
        SPlayerEventMsg wsMsg = new SPlayerEventMsg(Type.PLAYER_JOIN);
        wsMsg.player = chatEvent.getPlayer().getName();

        String cache = SQLiteDB.getByUUID(chatEvent.getPlayer().getUniqueId());
        if(cache!=null){
            wsMsg.qnum = cache;
        }
        return wsMsg;
    }

    public static SPlayerEventMsg build(PlayerQuitEvent chatEvent){
        SPlayerEventMsg wsMsg = new SPlayerEventMsg(Type.PLAYER_LEAVE);
        wsMsg.player = chatEvent.getPlayer().getName();
        String cache = SQLiteDB.getByUUID(chatEvent.getPlayer().getUniqueId());
        if(cache!=null){
            wsMsg.qnum = cache;
        }
        return wsMsg;
    }
}
