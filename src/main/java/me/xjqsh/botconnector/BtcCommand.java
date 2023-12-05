package me.xjqsh.botconnector;

import me.xjqsh.botconnector.database.SQLiteJDBC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BtcCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            UUID name = p.getUniqueId();
            String qnum = SQLiteJDBC.getByUUID(name);
            if(qnum==null){
                sender.sendMessage("你尚未绑定qq！");
            }else {
                sender.sendMessage("你当前绑定的qq是："+qnum);
            }
        }else {
            sender.sendMessage("this command should execute by player!");
        }
        return true;
    }
}
