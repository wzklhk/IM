package net.zpavelocity.im.user;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class User {
    private String username;
    private Channel userChannel;
    private boolean isOnline;

    public User(String username, Channel userChannel, boolean isOnline) {
        this.username = username;
        this.userChannel = userChannel;
        this.isOnline = isOnline;
    }
}
