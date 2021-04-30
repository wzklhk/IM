package net.zpavelocity.im.server.session;

import io.netty.channel.Channel;

import java.util.ArrayList;

public class Channels {
    private static ArrayList<Channel> channelArrayList = new ArrayList<Channel>();

    public static ArrayList<Channel> getChannelArrayList() {
        return channelArrayList;
    }
}
