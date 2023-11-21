package me.xjqsh.botconnector;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

//public class PlayerListener implements Listener {
//    @EventHandler
//    public void onPlayerChat(AsyncChatEvent event){
//        Player p = event.getPlayer();
//        if(event.message() instanceof TextComponent){
//            TextComponent component = (TextComponent) event.message();
//            if(component.content().startsWith("#")){
//                Bukkit.getScheduler().runTaskAsynchronously(BotConnector.getInstance(),
//                        ()->BotConnector.getWsServer()
//                                .broadcast(new Message(p.getName(),component.content().substring(1)).toJson())
//                        );
//            }
//        }
//
//    }
//}
