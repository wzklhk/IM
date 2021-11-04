package net.zpavelocity.im.message;

public class EnumTest {
    public static void main(String[] args) {
        MessageTypeEnum msg = MessageTypeEnum.BROADCAST;
        System.out.println(MessageTypeEnum.getCode(msg));
        System.out.println((MessageTypeEnum.getType(MessageTypeEnum.getCode(msg))));

        BroadcastMessage broadcastMessage = new BroadcastMessage("test", "username");
        System.out.println("getMessageType: " + broadcastMessage.getMessageTypeEnum());

    }
}
