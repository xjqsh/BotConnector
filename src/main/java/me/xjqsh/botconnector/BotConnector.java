package me.xjqsh.botconnector;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import me.xjqsh.botconnector.api.Server;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

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

        var app = Javalin.create(config -> {
            OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
            openApiConfiguration.getInfo().setTitle("Minecraft API");

            config.plugins.register(new OpenApiPlugin(openApiConfiguration));
            config.plugins.register(new SwaggerPlugin(new SwaggerConfiguration()));
        });

        app.routes(()->{
            path("v1",()->{
                get("/ping", Server::ping);
                get("/player_list",Server::playerList);
                get("/player",Server::getPlayer);
            });
        });

        app.ws("v1/ws", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("Connected");
            });

        });



        app.start(20248);

//        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
    }

    @Override
    public void onDisable() {

    }


}
