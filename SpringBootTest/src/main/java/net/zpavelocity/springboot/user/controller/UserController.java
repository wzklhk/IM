package net.zpavelocity.springboot.user.controller;

import net.zpavelocity.springboot.common.Result;
import net.zpavelocity.springboot.user.entity.User;
import net.zpavelocity.springboot.user.reposityory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public Result<List<User>> findAllUser() {
        return Result.data(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
    }
}
