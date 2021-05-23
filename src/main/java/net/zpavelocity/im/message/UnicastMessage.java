package net.zpavelocity.im.message;

import lombok.Data;

@Data
public class UnicastMessage extends Message {
    MessageTypeEnum messageTypeEnum = MessageTypeEnum.BROADCAST;

    private java.lang.String message;
    private String fromUser;
    private String toUser;

    public UnicastMessage(java.lang.String message, String fromUser, String toUser) {
        this.message = message;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    @Override
    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }
}
