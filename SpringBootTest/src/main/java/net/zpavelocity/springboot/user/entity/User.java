package net.zpavelocity.springboot.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column // 标识属性是一个普通的列
    private String username;

    @Column // 标识属性是一个普通的列
    private String password;

    @Column // 标识属性是一个普通的列
    private Timestamp birthday;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
