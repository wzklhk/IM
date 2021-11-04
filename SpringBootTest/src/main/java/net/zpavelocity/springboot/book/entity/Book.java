package net.zpavelocity.springboot.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@TableName("book")
@Data
public class Book {

    protected Double price;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String author;
    private Date publicationDate;

}
