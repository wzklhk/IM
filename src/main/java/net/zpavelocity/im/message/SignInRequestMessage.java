package net.zpavelocity.im.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SignInRequestMessage extends Message {
    private MessageTypeEnum messageTypeEnum = MessageTypeEnum.SIGN_IN_REQUEST;

    private String username;
    public SignInRequestMessage(String username) {
        this.username = username;
    }


    @Override
    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }
}
