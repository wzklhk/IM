package net.zpavelocity.springboot;

import net.bytebuddy.utility.RandomString;
import net.zpavelocity.springboot.book.entity.Book;
import net.zpavelocity.springboot.book.mapper.BookMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.sql.Date;


public class SpringBootMyBatisPlusApplicationTest {

    @Resource
    @Autowired
    BookMapper bookMapper;

    @Test
    public void addBook() {
        for (int i = 0; i < 20; i++) {
            Book book = new Book();
            book.setName(RandomString.make(10));
            book.setAuthor(RandomString.make(10));
            book.setPrice(Math.random());
            book.setPublicationDate(new Date(new java.util.Date().getTime()));
            System.out.println("save: " + book.toString());

            bookMapper.insert(book);
        }
    }
}
