package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
//  User findByName(String name);
  User findByUsername(String username);

}
