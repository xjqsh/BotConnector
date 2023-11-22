package me.xjqsh.botconnector.api.data;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class OnlinePlayers {
    public final List<PlayerData> onlinePlayers;
    private OnlinePlayers(List<PlayerData> onlinePlayers){
        this.onlinePlayers = onlinePlayers;
    }
    @NotNull
    public static OnlinePlayers get(){
        return new OnlinePlayers(Bukkit.getOnlinePlayers().stream()
                .map(PlayerData::get).collect(Collectors.toList()));
    }
}
