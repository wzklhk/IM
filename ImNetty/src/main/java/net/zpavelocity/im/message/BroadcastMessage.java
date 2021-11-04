package net.zpavelocity.im.message;

import lombok.Data;

@Data
public class BroadcastMessage extends Message {
    MessageTypeEnum messageTypeEnum = MessageTypeEnum.BROADCAST;

    private String message;
    private String fromUser;

    public BroadcastMessage(String message, String fromUser) {
        this.message = message;
        this.fromUser = fromUser;
    }

    @Override
    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }
}
