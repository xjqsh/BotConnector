package me.xjqsh.botconnector.listener;

import me.xjqsh.botconnector.BotConnector;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ConsoleListener implements Filter {

    private BotConnector plugin;

    public ConsoleListener(BotConnector plugin) {
        this.plugin = plugin;
    }
    private static final AtomicReference<Long> last_report = new AtomicReference<>(0L);
    private static final AtomicReference<String> result = new AtomicReference<>("");
    private static final AtomicReference<Boolean> running = new AtomicReference<>(false);

    public static String getSparkReportUrl(){
        if(System.currentTimeMillis() - last_report.get()>60*1000L){
            if(!running.get()){
                running.set(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(BotConnector.getInstance(),
                        ()-> {
                            try {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"spark profiler open");
                            } catch (Exception e) {
                                // Just warn about the issue
                                Bukkit.getLogger().warning("Failed to execute spark profiler command!");
                            }
                        });
            }
        }else {
            return result.get();
        }
        return null;
    }

    @Override
    public Result filter(LogEvent logEvent) {
        if(logEvent.getMessage().getFormattedMessage().contains("https://spark.lucko.me/")){
            result.set(logEvent.getMessage().getFormattedMessage());
            last_report.set(System.currentTimeMillis());
            running.set(false);
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return null;
    }

    @Override
    public Result getOnMatch() {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object... objects) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object o, Throwable throwable) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg) {
        return Filter.super.filter(logger, level, marker, msg);
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        return true;
    }
}
