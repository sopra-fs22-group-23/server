package ch.uzh.ifi.sopra22.payroll;

import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDB {

    @Bean
    CommandLineRunner initDatabase(UserService service) {

        return args -> {
            User user1 = new User("test", "test");
            service.createUser(user1);
            user1.setLogged_in(false);
            service.updateUser(user1.getId(), user1);//otherwise it will be login and he will stay logged in

            User user2 = new User("user", "user");
            service.createUser(user2);
            user2.setLogged_in(false);
            service.updateUser(user2.getId(), user2);//otherwise it will be login and he will stay logged in


        };
    }
}
