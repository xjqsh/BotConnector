package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.api.data.OnlinePlayers;
import me.xjqsh.botconnector.api.data.ServerHealth;
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
}
