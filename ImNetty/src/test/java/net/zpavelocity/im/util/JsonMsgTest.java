package net.zpavelocity.im.util;

import net.zpavelocity.im.message.JsonMsg;

public class JsonMsgTest {


    public static void main(String[] args) {
        JsonMsg message = new JsonMsgTest().buildMsg();

        String json = message.convertToJson();
        System.out.println(json);

        JsonMsg jsonMsg = JsonMsg.parseFromJson(json);
        System.out.println(jsonMsg);
    }

    public JsonMsg buildMsg() {
        JsonMsg user = new JsonMsg();
        user.setId(1000);
        user.setContent("json msg test");

        return user;
    }
}
