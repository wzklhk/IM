package net.zpavelocity.springboot.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zpavelocity.springboot.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
