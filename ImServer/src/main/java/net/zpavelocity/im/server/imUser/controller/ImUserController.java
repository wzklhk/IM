package net.zpavelocity.im.server.imUser.controller;

import net.zpavelocity.im.server.imUser.entity.ImUser;
import net.zpavelocity.im.server.imUser.repository.ImUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/imUser")
public class ImUserController {

    @Autowired
    private ImUserRepository imUserRepository;

    @GetMapping
    public String home() {
        return "imUser";
    }

    @GetMapping("/get")
    public ImUser get() {
        Optional<ImUser> byId = imUserRepository.findById(1);
        ImUser user = byId.get();
        return user;
    }
}
