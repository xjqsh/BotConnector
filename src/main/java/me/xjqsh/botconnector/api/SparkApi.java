package me.xjqsh.botconnector.api;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
import me.xjqsh.botconnector.listener.ConsoleListener;

public class SparkApi {
    @OpenApi(
            summary = "try open the spark profiler",
            path = "/v1/ping",
            tags = {"Spark"},
            methods = HttpMethod.GET,
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "plain/text")),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(type = "plain/text"),
                            description = "Failed to open the spark profiler WebUI")
            }
    )
    public static void profiler(Context ctx) {
        ctx.async(30000,
                ()->{
                    ctx.status(400).result("Time out when trying to open the spark profiler WebUI");
                },()->{
                    while (true){
                        Thread.sleep(1000);
                        String url = ConsoleListener.getSparkReportUrl();
                        if(url!=null){
                            ctx.result(url);
                            break;
                        }
                    }
                });
    }
}
