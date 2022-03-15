package ch.uzh.ifi.sopra22.service;


import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setLogged_in(true);//automatically login into application
    newUser.setCreation_date(new Date());

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @see User
   */
  public void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    }
  }

  public User loginUser(User userToLogin){
      User userByUsername = userRepository.findByUsername(userToLogin.getUsername());

      if(userByUsername == null){
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password - error 1");
      }
      if(!Objects.equals(userByUsername.getPassword(), userToLogin.getPassword())){
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password - error 2");
      }

      userByUsername.setLogged_in(true);
      userRepository.saveAndFlush(userByUsername);
      return userByUsername;
  }

  public User returnUserByID(Long id){
      User returnUser  = userRepository.findById(id).orElse(null);
      if(returnUser == null)
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User wasn't found");

      return returnUser;
  }


  public User updateUser(Long id, User userIn){
      //System.out.println(userIn);
      if(userIn.getToken() == null){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access the method, TOKEN missing");
      }

      User userToUpdate = returnUserByID(id);
      if(!userIn.getToken().equals(userToUpdate.getToken())){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to update foreign user");
      }

      if(userIn.getUsername() != null){//it must be set
          if(!Objects.equals(userIn.getUsername(), userToUpdate.getUsername())){//don't update, if I send the same value
              this.checkIfUserExists(userIn);
              userToUpdate.setUsername(userIn.getUsername());
          }
      }


      userToUpdate.setLogged_in(userIn.isLogged_in());//we can set it to false or true, doesn't matter, if not provided it will be set to false
      userToUpdate.setBirthday(userIn.getBirthday());//can also be null
      userRepository.save(userToUpdate);

      return userToUpdate;
  }
}
