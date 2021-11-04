package net.zpavelocity.springboot.test.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Books {
    @JsonIgnore
    protected Float price;
    private Integer id;
    private String name;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date publicationDate;


    public Books() {
    }

    public Books(Integer id, String name, String author, Float price, Date publicationDate) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationDate = publicationDate;
    }

    public Books(Integer id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public Books setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Books setName(String name) {
        this.name = name;
        return this;

    }

    public String getAuthor() {
        return author;
    }

    public Books setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Float getPrice() {
        return price;
    }

    public Books setPrice(Float price) {
        this.price = price;
        return this;

    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public Books setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
        return this;

    }
}
