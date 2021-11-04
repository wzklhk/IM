package net.zpavelocity.springboot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


// T：具体操作的实现类
// ID：主键字段的类型
@NoRepositoryBean
public interface CommonRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
