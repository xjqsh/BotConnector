package me.xjqsh.botconnector.listener;

import com.google.gson.Gson;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.xjqsh.botconnector.api.QQBindApi;
import me.xjqsh.botconnector.api.websocket.WebsocketHandler;
import me.xjqsh.botconnector.api.websocket.SPlayerEventMsg;
import me.xjqsh.botconnector.database.SQLiteDB;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private static final Gson gson = new Gson();
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        if(event.message() instanceof TextComponent){
            TextComponent component = (TextComponent) event.message();
            if(component.content().startsWith("#")){
                WebsocketHandler.broadcastAsync(gson.toJson(SPlayerEventMsg.build(event)));
            }
        }

    }

    @EventHandler
    public void onPlayerChat(PlayerJoinEvent event){
        Player p = event.getPlayer();
        String qnum = SQLiteDB.getByUUID(p.getUniqueId());
//        if(qnum!=null){
//            QQBindApi.getInstance().cacheQBindInfo(qnum,p.getUniqueId());
//        }

        WebsocketHandler.broadcastAsync(gson.toJson(SPlayerEventMsg.build(event)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player p = event.getPlayer();
//        QQBindApi.getInstance().unCacheQBindInfo(p.getUniqueId());

        WebsocketHandler.broadcastAsync(gson.toJson(SPlayerEventMsg.build(event)));
    }
}
