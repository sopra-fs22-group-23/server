package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.UserStatus;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        newUser.setStatus(UserStatus.ONLINE);

        if (newUser.getName() == null){
            newUser.setName(newUser.getUsername());
        }

        String hashedPassword = hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);
        checkIfUserExists(newUser);

        updateRepository(newUser);
        return newUser;
    }


    public User authenticateUser(User newUser) {
        User userByUsername = userRepository.findByUsername(newUser.getUsername());
        if (userByUsername == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Username not found"));
        }
        //System.out.println(userByUsername.getUsername());
        String hashedNewUserPassword = hashPassword(newUser.getPassword());
        if(!userByUsername.getPassword().equals(hashedNewUserPassword) ){
            System.out.println("From Database: " + userByUsername.getPassword());
            System.out.println("From Frontend: " + newUser.getPassword());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Password is incorrect"));
        }

        //updates the changes
        userByUsername.setToken(UUID.randomUUID().toString());
        userByUsername.setStatus(UserStatus.ONLINE);
        //does the flushing and saving
        updateRepository(userByUsername);
        return userByUsername;
    }


    public void logout(User user) {
        user.setStatus(UserStatus.OFFLINE);
        updateRepository(user);
    }

    public void compareUserByToken(String userToken, String headerToken) {
        System.out.println("UserbyID: " + userToken);
        System.out.println("UserbyHeader: " + headerToken);
        if (!("Bearer "+ userToken).equals(headerToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Unauthorized for the update"));
        }
    }


    public void checkTokenExists(String token) {
        if (token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,  String.format("Unauthorized for this request"));
        }
    }

    public User getUserByIDNum(Long userId) {
        Optional<User> userRepo = userRepository.findById(userId);
        User user;
        try{
            user = userRepo.orElse(null);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ID not found"));
            }
        }catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ID not found"));
        }
        return user;
    }


    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByName = userRepository.findByName(userToBeCreated.getName());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null && userByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username and the name", "are"));
        } else if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        } else if (userByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "name", "is"));
        }
    }

    private void updateRepository(User user) {
        userRepository.save(user);
        userRepository.flush();
    }
    private String hashPassword(String passwordToHash) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(passwordToHash.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i=0; i <bytes.length; i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }
}