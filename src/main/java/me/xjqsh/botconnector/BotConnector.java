package me.xjqsh.botconnector;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import me.xjqsh.botconnector.api.Server;

import me.xjqsh.botconnector.api.websocket.WebsocketHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class BotConnector extends JavaPlugin {
    public static Logger logger;
    private static BotConnector instance;
    public static BotConnector getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        logger = this.getLogger();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration bukkitConfig = getConfig();

        var app = Javalin.create(config -> {
            OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
            openApiConfiguration.getInfo().setTitle("Minecraft API");
            openApiConfiguration.setDocumentationPath("/swagger-docs");

            config.plugins.register(new OpenApiPlugin(openApiConfiguration));

            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
            swaggerConfiguration.setDocumentationPath("/swagger-docs");
            config.plugins.register(new SwaggerPlugin(swaggerConfiguration));

            config.accessManager((handler, ctx, permittedRoles) -> {
                String path = ctx.req().getPathInfo();

                String authKey = bukkitConfig.getString("key", "change_me");
                if (ctx.header("key") != null && ctx.header("key").equals(authKey)) {
                    handler.handle(ctx);
                    return;
                }

                // Add some paths that will always bypass auth
                List<String> noAuthPathsList = new ArrayList<>();
                noAuthPathsList.add("/swagger");
                noAuthPathsList.add("/swagger-docs");
                noAuthPathsList.add("/webjars");
                noAuthPathsList.add("/v1/ws");

                // If the request path starts with any of the noAuthPathsList just allow it
                for (String noAuthPath : noAuthPathsList) {
                    if (path.startsWith(noAuthPath)) {
                        handler.handle(ctx);
                        return;
                    }
                }

                // fall through, failsafe
                ctx.status(401).result("Unauthorized key, reference the key existing in config.yml");
            });
        });

        app.routes(()->{
            path("v1",()->{
                get("/ping", Server::ping);
                get("/player_list",Server::playerList);
                get("/player",Server::getPlayer);

                ws("/ws", WebsocketHandler::events);
            });
        });

//        Bukkit.getScheduler().runTaskTimerAsynchronously(this,()->{
//            WebsocketHandler.broadcast("test");
//        },20,200);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);

        app.start(20248);


    }

    @Override
    public void onDisable() {

    }


}
