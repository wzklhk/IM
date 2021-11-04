package net.zpavelocity.springboot.test.controller;

import net.zpavelocity.springboot.test.bean.Books;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class BooksController {
    @GetMapping("/book")
    public Books book() {
        Books books = new Books();
        books.setId(10)
                .setName("流浪地球")
                .setAuthor("刘慈欣")
                .setPrice(20.5f)
                .setPublicationDate(new Date());
        return books;
    }
}
