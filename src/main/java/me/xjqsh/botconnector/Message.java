package me.xjqsh.botconnector;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private long timestamp;
    private String content;
    private String player;

    public Message(String player, String content) {
        this.timestamp = System.currentTimeMillis();
        this.content = content;
        this.player = player;
    }

    public String toJson(){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("time",timestamp);
        objectMap.put("text",content);
        objectMap.put("player",player);
        return JSONObject.toJSONString(objectMap);
    }
}
