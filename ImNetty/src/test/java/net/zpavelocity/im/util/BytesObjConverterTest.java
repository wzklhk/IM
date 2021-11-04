package net.zpavelocity.im.util;

import net.zpavelocity.im.message.SignInRequestMessage;

public class BytesObjConverterTest {
    public static void main(String[] args) {
        SignInRequestMessage signInRequestMessage = new SignInRequestMessage("username");
        System.out.println(signInRequestMessage);
        byte[] username = BytesObjConverter.o2b(signInRequestMessage);
        System.out.println(username);
        SignInRequestMessage x = (SignInRequestMessage) BytesObjConverter.b2o(username);
        System.out.println(x);
    }
}
