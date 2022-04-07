package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.UserStatus;
import ch.uzh.ifi.sopra22.entity.EventUser;
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

    public void linkEventUsertoUser(User user, EventUser admin) {
        user.addEventUsers(admin);
        updateRepository(user);
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

    public User editUser(User updatedUser) {
        Optional<User> userRepo = userRepository.findById(updatedUser.getId());
        User user = userRepo.orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID not found");
        }
        if (updatedUser.getUsername() != null) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(hashPassword(updatedUser.getPassword()));
        }
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getBirthday() != null) {
            user.setBirthday(updatedUser.getBirthday());
        }
        if (updatedUser.getBiography() != null){
            user.setBiography(updatedUser.getBiography());
        }

        updateRepository(user);

        return user;
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized for the update");
        }
    }

    public void checkTokenExists(String token) {
        if (token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,  String.format("Unauthorized for this request"));
        }
    }

    public User getUserByToken(String token) {
        checkTokenExists(token);
        User user = userRepository.findByToken(token);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No such user exists with this token");
        }

        return user;
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

    public void updateRepository(User user) {
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

    public User validateUser(Long id, String token) {
        User user = getUserByToken(token);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
        }
        if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unauthorized access (token invalid)");
        }

        return user;
    }

    public String parseBearerToken(String token) {
        String[] parseToken = token.split(" ");
        return parseToken[1];
    }

    public void validateToken(String token) {
        User user = getUserByToken(parseBearerToken(token));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access (token invalid)");
        }
    }
}
