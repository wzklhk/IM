package net.zpavelocity.springboot;

import net.bytebuddy.utility.RandomString;
import net.zpavelocity.springboot.user.entity.User;
import net.zpavelocity.springboot.user.reposityory.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootJpaApplicationTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void contextLoads() {
        System.out.println("table创建成功");
    }

    @Test
    public void addUser() {
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setUsername(RandomString.make(10));
            user.setPassword(UUID.randomUUID().toString());
            user.setBirthday(new Timestamp(new Date().getTime()));
            System.out.println("save: " + user.toString());

            userRepository.save(user);
        }
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(3);
        user.setUsername(RandomString.make(10));
        user.setPassword(UUID.randomUUID().toString());
        user.setBirthday(new Timestamp(0L));
        System.out.println("save: " + user.toString());

        userRepository.save(user);
    }

    @Test
    public void findByIdUser() {
        Optional<User> userDaoById = userRepository.findById(3);

        User user = userDaoById.get();
        System.out.println(user);
    }

    @Test
    public void findUsersSmaller() {
        List<User> users = userRepository.getUsersSmaller(10);

        for (User user : users) {
            System.out.println(user);
        }

    }

    @Test
    public void findByIdsUser() {
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(3);
        ids.add(5);
        ids.add(4);
        ids.add(9);

        List<User> users = userRepository.findByBatchIds(ids);

        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void findAllUser() {

        List<User> all = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        for (User user : all) {
            System.out.println(user);
        }
    }

    @Test
    public void findAllByPageUser() {
        Page<User> all = userRepository.findAll(PageRequest.of(0, 10, Sort.Direction.ASC, "username"));

        System.out.println("TotalElements: " + all.getTotalElements());
        System.out.println("TotalPages: " + all.getTotalPages());

        for (User user : all) {
            System.out.println(user);
        }
    }

    @Test
    public void deleteByIdUser() {
        userRepository.deleteById(3);
    }

    @Test
    public void findByNameUser() {
        List<User> users = userRepository.findByUsernameAndPassword("9AheSoUVLb", "73c07159-f806-414f-9f35-7b8fc23c4a71");

        for (User user : users) {
            System.out.println(user);
        }
    }
}
