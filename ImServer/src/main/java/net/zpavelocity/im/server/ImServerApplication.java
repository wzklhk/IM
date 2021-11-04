package net.zpavelocity.im.server;

import net.zpavelocity.im.server.imUser.entity.ImUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@SpringBootApplication
public class ImServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImServerApplication.class, args);
    }

    @GetMapping("/")
    public ImUser test() {
        return new ImUser(1, "test", "test", new Date());
    }
}
