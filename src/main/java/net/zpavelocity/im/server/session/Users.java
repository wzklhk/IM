package net.zpavelocity.im.server.session;

import io.netty.channel.Channel;
import net.zpavelocity.im.user.User;

import java.util.ArrayList;

public class Users {
    private static ArrayList<User> userArrayList = new ArrayList<User>();

    public static ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public static boolean isExist(String username) {
        for (User user : userArrayList) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static User getUser(String username) {
        for (User user : userArrayList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static User getUser(Channel userChannel) {
        for (User user : userArrayList) {
            if (user.getUserChannel().equals(userChannel)) {
                return user;
            }
        }
        return null;
    }
}
