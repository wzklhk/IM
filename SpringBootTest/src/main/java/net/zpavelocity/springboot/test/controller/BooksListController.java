package net.zpavelocity.springboot.test.controller;

import net.zpavelocity.springboot.test.bean.Books;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BooksListController {
    @GetMapping("/bookslist")
    public ModelAndView bookList() {
        List<Books> booksList = new ArrayList<>();
        booksList.add(new Books(1, "三国演义", "罗贯中"));
        booksList.add(new Books(2, "红楼梦", "红楼梦"));
        booksList.add(new Books(3, "西游记", "吴承恩"));
        booksList.add(new Books(4, "水浒传", "施耐庵"));

        ModelAndView mv = new ModelAndView();
        mv.addObject("bookslist", booksList);
        mv.setViewName("bookslist");

        return mv;
    }
}
