package me.xjqsh.botconnector.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData {
    public final String name;
    public final boolean online;
    public final UUID uuid;
    public final long lastLogin;
    public final long firstPlayed;
    private PlayerData(String name, boolean online, UUID uuid, long lastLogin, long firstPlayed){
        this.name = name;
        this.online = online;
        this.uuid = uuid;
        this.lastLogin = lastLogin;
        this.firstPlayed = firstPlayed;
    }
    @NotNull
    public static PlayerData get(String playerName){
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        return new PlayerData(p.getName(),p.isOnline(),p.getUniqueId(),p.getLastLogin(),p.getFirstPlayed());
    }

    @NotNull
    public static PlayerData get(@NotNull Player p){
        return new PlayerData(p.getName(),p.isOnline(),p.getUniqueId(),p.getLastLogin(),p.getFirstPlayed());
    }
}
