package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.database.SQLiteJDBC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class QQBindApi {
    @OpenApi(
            summary = "request to bind the provide qq to player",
            path = "/v1/qq/bind",
            tags = {"QQ"},
            methods = HttpMethod.POST,
            headers = {
                    @OpenApiParam(name = "key")
            },
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = {
                            @OpenApiContent(
                                    mimeType = "application/x-www-form-urlencoded",
                                    properties = {
                                            @OpenApiContentProperty(name = "qq_num", type = "string"),
                                            @OpenApiContentProperty(name = "player", type = "string")
                                    }
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(type = "application/json"),
                            description = "Param missing"
                    ),
                    @OpenApiResponse(status = "403",
                            content = @OpenApiContent(type = "application/json"),
                            description = "Target player is not online"
                    )
            }
    )
    public static void bindQQNum(Context ctx) {
        String qq = ctx.formParam("qq_num");
        String playerName = ctx.formParam("player");

        if(qq==null || playerName==null){
            ctx.status(400).result("QQ number or player name is missing");
            return;
        }

        Player player = Bukkit.getPlayer(playerName);

        if(player == null){
            ctx.status(403).result("Target player is not online");
            return;
        }

        UUID uuid = SQLiteJDBC.getByQnum(qq);
        String x = SQLiteJDBC.getByUUID(player.getUniqueId());

        if(uuid!=null || x!=null){
            ctx.status(200).result("The qq or player is already bound");
            return;
        }

        AtomicBoolean timeout = new AtomicBoolean(false);
        ctx.async(30000, ()->{
            synchronized (timeout){
                ctx.result("Confirm time out.");
                timeout.set(true);
                timeout.notify();
            }
            player.sendMessage(Component.text("确认超时"));
        }, ()->{
            final Component component = Component.text()
                    .content("QQ("+qq+")正在尝试绑定至该账号，你有30秒的时间确认该请求")
                    .color(NamedTextColor.GOLD)

                    .append(Component.text("[✓]")
                            .color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("点击确认")
                            ))
                            .clickEvent(ClickEvent.callback((f)->{
                                synchronized (timeout){
                                    if(timeout.get()){
                                        f.sendMessage(Component.text("该请求已经过期，请重新发起请求"));
                                    }else {
                                        ctx.result("success");
                                        f.sendMessage(Component.text("确认成功"));
                                        if(SQLiteJDBC.bind(player.getUniqueId(),qq)){
                                            f.sendMessage(Component.text("绑定成功"));
                                        }else {
                                            f.sendMessage(Component.text("绑定失败"));
                                        }
                                    }
                                    timeout.notify();
                                }
                            }))
                    )
                    .appendNewline()
                    .append(Component.text("如果该操作并非你本人所为，请将其忽略"))
                    .build();

            player.sendMessage(component);

            synchronized (timeout){
                timeout.wait();
            }
        });
    }
}