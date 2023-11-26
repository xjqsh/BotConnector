package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.BotConnector;
import me.xjqsh.botconnector.api.data.PlayerData;
import me.xjqsh.botconnector.database.SQLiteJDBC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
                            description = "Target player is not online or provided qq/player is already bound"
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
            ctx.status(403).result("The qq or player is already bound");
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
                                        f.sendMessage(Component.text("确认成功"));
                                        if(SQLiteJDBC.bind(player.getUniqueId(),qq)){
                                            f.sendMessage(Component.text("绑定成功"));
                                            ctx.result("success");
                                        }else {
                                            f.sendMessage(Component.text("绑定失败"));
                                            ctx.result("failed");
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

    @OpenApi(
            summary = "request to bind the provide qq to player",
            path = "/v1/qq/unbind",
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
                                            @OpenApiContentProperty(name = "qq_num", type = "string")
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
                            description = "Provided qq number haven't bound to any player."
                    ),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(type = "application/json"),
                            description = "Operation time out."
                    )
            }
    )
    public static void unbindQQNum(Context ctx) {
        String qq = ctx.formParam("qq_num");

        if(qq==null){
            ctx.status(400).result("QQ number name is missing");
            return;
        }

        ctx.async(30000, ()->{
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Operation time out.");
        }, ()->{
            UUID uuid = SQLiteJDBC.getByQnum(qq);

            if(uuid==null){
                ctx.status(403).result("The qq haven't bound to any player");
                return;
            }

            if(SQLiteJDBC.unbind(qq)){
                ctx.result("success");
            }else {
                ctx.result("failed");
            }
        });
    }


    @OpenApi(
            summary = "request to bind the provide qq to player",
            path = "/v1/qq/check",
            tags = {"QQ"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            queryParams = {
                    @OpenApiParam(name = "qq_num")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(type = "application/json"),
                            description = "Param error"
                    ),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(type = "application/json"),
                            description = "Operation time out."
                    )
            }
    )
    public static void getBound(Context ctx) {
        String qq = ctx.queryParam("qq_num");

        if(qq==null){
            ctx.status(400).result("QQ number name is missing");
            return;
        }

        ctx.async(30000, ()->{
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Operation time out.");
        }, ()->{
            UUID uuid = SQLiteJDBC.getByQnum(qq);

            if(uuid==null){
                ctx.status(403).result("The qq haven't bound to any player");
                return;
            }

            ctx.json(PlayerData.get(uuid));
        });
    }
}
