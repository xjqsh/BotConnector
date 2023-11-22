package me.xjqsh.botconnector;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import me.lucko.spark.api.Spark;
import me.xjqsh.botconnector.api.PlayerApi;
import me.xjqsh.botconnector.api.ServerApi;

import me.xjqsh.botconnector.api.SparkApi;
import me.xjqsh.botconnector.api.websocket.WebsocketHandler;
import me.xjqsh.botconnector.database.SQLiteJDBC;
import me.xjqsh.botconnector.listener.ConsoleListener;
import me.xjqsh.botconnector.listener.PlayerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


import static io.javalin.apibuilder.ApiBuilder.*;

public final class BotConnector extends JavaPlugin {
    private static BotConnector instance;
    public static BotConnector getInstance() {
        return instance;
    }
    Logger rootLogger = (Logger) LogManager.getRootLogger();
    public static Spark spark;

    @Override
    public void onLoad() {
        instance = this;
    }

    public static Javalin app;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration bukkitConfig = getConfig();
        // init sqlite
        SQLiteJDBC.init();
        // init javalin
        app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
            openApiConfiguration.getInfo().setTitle("Server API");
            openApiConfiguration.setDocumentationPath("/swagger-docs");

            config.plugins.register(new OpenApiPlugin(openApiConfiguration));

            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
            swaggerConfiguration.setDocumentationPath("/swagger-docs");
            config.plugins.register(new SwaggerPlugin(swaggerConfiguration));

            config.accessManager((handler, ctx, permittedRoles) -> {
                String path = ctx.req().getPathInfo();

                // make sure there is a header called "key"
                String authKey = bukkitConfig.getString("key", "change_me");
                if (ctx.header("key") != null && ctx.header("key").equals(authKey)) {
                    handler.handle(ctx);
                    return;
                }

                // If the request is still not handled, check for a cookie (websockets use cookies for auth)
                if (ctx.cookie("auth-key") != null && ctx.cookie("auth-key").equals(authKey)) {
                    handler.handle(ctx);
                    return;
                }

                // Add some paths that will always bypass auth
                List<String> noAuthPathsList = new ArrayList<>();
                noAuthPathsList.add("/swagger");
                noAuthPathsList.add("/swagger-docs");
                noAuthPathsList.add("/webjars");

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
                //player
                get("/player", PlayerApi::getPlayer);
                get("/player/uuid", PlayerApi::getPlayerByUUID);
                //server
                get("/ping", ServerApi::ping);
                get("/server/players", ServerApi::playerList);
                get("/server/health",ServerApi::health);
                //spark
                get("/spark", SparkApi::profiler);
                ws("/ws", WebsocketHandler::events);
            });
        });

        app.start(20248);
        //init logger listener
        rootLogger.addFilter(new ConsoleListener(this));

        // init spark
        RegisteredServiceProvider<Spark> provider = Bukkit.getServicesManager().getRegistration(Spark.class);
        if (provider != null) {
            spark = provider.getProvider();
        }


        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);


    }

    @Override
    public void onDisable() {
        if(app!=null){
            app.stop();
        }
    }


}
