package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.data.OnlinePlayers;
import me.xjqsh.botconnector.data.PlayerData;

public class Server {
    @OpenApi(
            summary = "pong!",
            path = "/v1/ping",
            tags = {"Server"},
            methods = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "plain/text"))
            }
    )
    public static void ping(Context ctx) {
        ctx.result("pong");
    }

    @OpenApi(
            summary = "pong!",
            path = "/v1/player_list",
            tags = {"Server"},
            methods = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void playerList(Context ctx) {
        ctx.json(OnlinePlayers.get());
    }
    @OpenApi(
            summary = "pong!",
            path = "/v1/player",
            tags = {"Server"},
            queryParams = {
                    @OpenApiParam(name = "name", description = "The name of the objective to get")
            },
            methods = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getPlayer(Context ctx) {
        ctx.json(PlayerData.get(ctx.queryParam("name")));
    }
}
