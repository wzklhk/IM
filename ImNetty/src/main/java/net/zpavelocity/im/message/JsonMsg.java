package net.zpavelocity.im.message;

import lombok.Data;
import net.zpavelocity.im.util.JsonUtil;

@Data
public class JsonMsg {
    private int id;
    private String content;

    public JsonMsg() {
    }

    public JsonMsg(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public static JsonMsg parseFromJson(String json) {
        return JsonUtil.json2pojo(json, JsonMsg.class);
    }

    public String convertToJson() {
        return JsonUtil.pojo2json(this);
    }
}
