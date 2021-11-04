package net.zpavelocity.im.server.session;

import java.util.Collections;
import java.util.Set;

public class Group {
    public static final Group EMPTY_GROUP = new Group("empty", Collections.emptySet());
    private String name;
    private Set<String> members;

    public Group(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }
}
