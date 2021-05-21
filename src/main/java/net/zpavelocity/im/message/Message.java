package net.zpavelocity.im.message;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Message implements Serializable {
    private MessageTypeEnum messageTypeEnum;

    public abstract MessageTypeEnum getMessageTypeEnum();

    public static Class<? extends Message> getMessageClass(MessageTypeEnum messageTypeEnum) {
        if (messageTypeEnum == MessageTypeEnum.SIGN_IN_REQUEST)
            return SignInRequestMessage.class;
        else if (messageTypeEnum == MessageTypeEnum.SIGN_IN_RESPONSE)
            return SignInResponseMessage.class;
        else if (messageTypeEnum == MessageTypeEnum.BROADCAST)
            return BroadcastMessage.class;
        else
            return null;
    }
}
