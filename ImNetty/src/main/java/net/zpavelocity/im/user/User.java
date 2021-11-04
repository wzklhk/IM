package net.zpavelocity.im.user;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class User {
    private String username;
    private Channel userChannel;

    public User(String username, Channel userChannel) {
        this.username = username;
        this.userChannel = userChannel;
    }
}
