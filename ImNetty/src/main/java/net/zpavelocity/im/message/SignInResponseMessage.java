package net.zpavelocity.im.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SignInResponseMessage extends Message {
    MessageTypeEnum messageTypeEnum = MessageTypeEnum.SIGN_IN_RESPONSE;

    private boolean isSignIn;
    private String reason;

    public SignInResponseMessage(boolean isSignIn, String reason) {
        this.isSignIn = isSignIn;
        this.reason = reason;
    }

    @Override
    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }
}
