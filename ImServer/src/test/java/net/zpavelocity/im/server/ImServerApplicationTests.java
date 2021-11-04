package net.zpavelocity.im.server;

import net.bytebuddy.utility.RandomString;
import net.zpavelocity.im.server.imUser.entity.ImUser;
import net.zpavelocity.im.server.imUser.repository.ImUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
class ImServerApplicationTests {

    @Autowired
    private ImUserRepository imUserRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void addImUsers() {
        for (int i = 0; i < 20; i++) {
            ImUser imUser = new ImUser();
            imUser.setUsername(RandomString.make(10));
            imUser.setPassword(UUID.randomUUID().toString());
            imUser.setBirthday(new Date());
            System.out.println("save: " + imUser.toString());

            imUserRepository.save(imUser);
        }
    }

    @Test
    public void getImUserById() {
        ImUser user = imUserRepository.getById(1);
        System.out.println(user);
    }

}
