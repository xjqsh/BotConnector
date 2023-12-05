package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.BotConnector;
import me.xjqsh.botconnector.api.data.OnlinePlayers;
import me.xjqsh.botconnector.api.data.ServerHealth;
import me.xjqsh.botconnector.listener.ConsoleListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;

public class ServerApi {
    @OpenApi(
            summary = "pong!",
            path = "/v1/ping",
            tags = {"Server"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "plain/text"))
            }
    )
    public static void ping(Context ctx) {
        ctx.result("pong!");
    }

    @OpenApi(
            summary = "get online players",
            path = "/v1/server/players",
            tags = {"Server"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void playerList(Context ctx) {
        ctx.json(OnlinePlayers.get());
    }

    @OpenApi(
            summary = "get server health info",
            path = "/v1/server/health",
            tags = {"Server"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void health(Context ctx) {
        ServerHealth health = new ServerHealth();

        // Logical CPU count
        int cpus = Runtime.getRuntime().availableProcessors();
        health.setCpus(cpus);

        // Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        health.setUptime(uptime);

        // Memory stats from the runtime
        long memMax = Runtime.getRuntime().maxMemory();
        long memTotal = Runtime.getRuntime().totalMemory();
        long memFree = Runtime.getRuntime().freeMemory();
        health.setMaxMemory(memMax);
        health.setTotalMemory(memTotal);
        health.setFreeMemory(memFree);

        health.setTps(Bukkit.getTPS());

        ctx.json(health);
    }

    @OpenApi(
            summary = "try open the spark profiler",
            path = "/v1/server/broadcast",
            tags = {"Spark"},
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
                                            @OpenApiContentProperty(name = "qq_num", type = "int"),
                                            @OpenApiContentProperty(name = "msg", type = "string")
                                    }
                            )
                    }
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "plain/text")),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(type = "plain/text"),
                            description = "Failed to open the spark profiler WebUI")
            }
    )
    public static void broadcast(Context ctx) {
        String msg = ctx.formParam("msg");
        String qq_num = ctx.formParam("qq_num");

        if(msg==null || qq_num==null){
            ctx.status(400).result("missing parma");
        }

        ctx.async(30000,
                ()->{
                    ctx.status(400).result("Time out when trying to open the spark profiler WebUI");
                },()->{
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BotConnector.getInstance(),()->{
                        Bukkit.broadcast(Component.text(qq_num+":"+msg));
                    });
                });
    }
}
