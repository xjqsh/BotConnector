package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.BotConnector;
import me.xjqsh.botconnector.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerApi {
    @OpenApi(
            summary = "get player info by name",
            path = "/v1/player",
            tags = {"Player"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            queryParams = {
                    @OpenApiParam(name = "name", description = "The name of the objective to get")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getPlayer(Context ctx) {
        ctx.json(PlayerData.get(ctx.queryParam("name")));
    }

    @OpenApi(
            summary = "get player info by uuid",
            path = "/v1/player/uuid",
            tags = {"Player"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            queryParams = {
                    @OpenApiParam(name = "uuid", description = "The uuid of the objective to get")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getPlayerByUUID(Context ctx) {
        if(ctx.queryParam("uuid")==null){
            ctx.status(400).result("missing params");
            return;
        }
        UUID uuid = UUID.fromString(ctx.queryParam("uuid"));

        ctx.json(PlayerData.get(uuid));
    }

    @OpenApi(
            summary = "give white list to the player",
            path = "/v1/player/whitelist",
            tags = {"Player"},
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
                                            @OpenApiContentProperty(name = "name", type = "string")
                                    }
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void addWhiteList(Context ctx) {
        if(ctx.formParam("name")==null){
            ctx.status(400).result("missing params");
            return;
        }
        String name = ctx.formParam("name");

        OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(name);
        AtomicBoolean flag = new AtomicBoolean(false);
        if(p!=null){
            String cmd = BotConnector.bukkitConfig.getString("wl_command","wladd {player}")
                    .replace("{player}",name);

            runAsyncCommand(ctx, flag, cmd);
        }

    }

    @OpenApi(
            summary = "remove white list from the player",
            path = "/v1/player/whitelist_remove",
            tags = {"Player"},
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
                                            @OpenApiContentProperty(name = "name", type = "string")
                                    }
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void rmWhiteList(Context ctx) {
        if(ctx.formParam("name")==null){
            ctx.status(400).result("missing params");
            return;
        }
        String name = ctx.formParam("name");

        OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(name);
        AtomicBoolean flag = new AtomicBoolean(false);
        if(p!=null){
            String cmd = BotConnector.bukkitConfig.getString("wldel_command","wladd {player}")
                    .replace("{player}",name);

            runAsyncCommand(ctx, flag, cmd);
        }

    }

    private static void runAsyncCommand(Context ctx, AtomicBoolean flag, String cmd) {
        ctx.async(10000, ()->{
            synchronized (flag){
                ctx.status(500).result("Command execute time out");
                flag.notify();
            }
        },()->{
            Bukkit.getScheduler().scheduleSyncDelayedTask(BotConnector.getInstance(),
                    ()-> {
                        try {
                            synchronized (flag){
                                flag.set(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
                                if(flag.get()){
                                    ctx.status(200).result("success");
                                }else {
                                    ctx.status(200).result("failed");
                                }
                                flag.notify();
                            }
                        } catch (Exception e) {
                            // Just warn about the issue
                            Bukkit.getLogger().warning("Failed to execute whitelist command!");
                        }
                    });
            synchronized (flag){
                flag.wait();
            }
        });
    }
}
