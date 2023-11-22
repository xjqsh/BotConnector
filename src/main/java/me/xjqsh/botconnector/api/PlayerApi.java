package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.api.data.PlayerData;

import java.util.UUID;

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
            ctx.status(400);
            return;
        }
        UUID uuid = UUID.fromString(ctx.queryParam("uuid"));

        ctx.json(PlayerData.get(uuid));
    }
}