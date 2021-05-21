package net.zpavelocity.im.message;

public enum MessageTypeEnum {
    SIGN_IN_REQUEST,
    SIGN_IN_RESPONSE,
    BROADCAST;

    public static MessageTypeEnum getType(int code) {
        return MessageTypeEnum.values()[code];
    }

    public static int getCode(MessageTypeEnum messageTypeEnum) {
        return messageTypeEnum.ordinal();
    }

    public static Class<? extends Message> getClass(MessageTypeEnum messageTypeEnum) {
        if (messageTypeEnum == SIGN_IN_REQUEST)
            return SignInRequestMessage.class;
        else if (messageTypeEnum == SIGN_IN_RESPONSE)
            return SignInResponseMessage.class;
        else if (messageTypeEnum == BROADCAST)
            return BroadcastMessage.class;
        else
            return null;
    }
}




