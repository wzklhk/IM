package net.zpavelocity.springboot.user.reposityory;

import net.zpavelocity.springboot.common.repository.CommonRepository;
import net.zpavelocity.springboot.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CommonRepository<User, Integer> {

    List<User> findByUsernameAndPassword(String username, String password);

    @Query(value = "select t from user t where t.id<?1")
    List<User> getUsersSmaller(Integer id);

    @Query("select t from user t where t.id in (?1) ")
    List<User> findByBatchIds(List<Integer> ids);
}