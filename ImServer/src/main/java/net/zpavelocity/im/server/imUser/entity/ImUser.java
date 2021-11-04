package net.zpavelocity.im.server.imUser.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "im_user")
@Table
@Data
public class ImUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private Date birthday;

    public ImUser() {
    }

    public ImUser(Integer id, String username, String password, Date birthday) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.birthday = birthday;
    }
}


