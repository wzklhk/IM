package net.zpavelocity.springboot.test.controller;

import net.zpavelocity.springboot.test.bean.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${value}")
    private String name;

    @Autowired
    private Environment env;

    @Autowired
    private Person person;

    @GetMapping("/hello")
    public String hello() {
        return "Hello spring boot";
    }

    @GetMapping("/hello2")
    public String hello2() {
        System.out.println("name: " + name);
        System.out.println("env: " + env.getProperty("person.name"));

        return "hello2";
    }

    @GetMapping("/hello3")
    public String hello3() {
        System.out.println("person: " + person.toString());

        return "hello3";
    }
}
