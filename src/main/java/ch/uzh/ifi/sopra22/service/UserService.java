package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.UserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
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

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    // Search constants
    private final int containsWholeFactor = 3;
    private final int usernameWeight = 3;
    private final int nameWeight = 3;
    private final int biographyWeight = 1;
    private final int emailWeight = 3;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void linkEventUsertoUser(User user, EventUser admin) {
        user.addEventUsers(admin);
        updateRepository(user);
    }

    public List<String> getWordsFromString(String text) {
        List<String> words = new ArrayList<>();
        int ref = 0;
        for (int i=0; i < text.length(); i++) {
            if (text.charAt(i) == ' ' || text.charAt(i) == '_' || text.charAt(i) == '+' || text.charAt(i) == '-') {
                words.add(text.substring(ref, i));
                ref = i + 1;
            }
        }
        words.add(text.substring(ref));
        return words;
    }

    public String parseString(String text) {
        String parsedText = text.replace('+', ' ');
        parsedText = parsedText.replace('-', ' ');
        parsedText = parsedText.replace('_', ' ');
        return parsedText.toLowerCase();
    }

    public List<User> sortUsersBySearch(List<User> users, String search) {
        if (search == null || search.equals("")) {
            return users;
        }
        // parse string to have spaces
        search = parseString(search);

        List<Integer> scores = new ArrayList<>();
        List<Integer> sortedScores = new ArrayList<>();
        List<User> sortedUsers = new ArrayList<>();

        // Assign scores to events
        for (User user : users) {
            int score = 0;
            try {
                // Contains check
                try {
                    if (user.getUsername().toLowerCase().contains(search)) {score += containsWholeFactor * usernameWeight;}
                    if (user.getName().toLowerCase().contains(search)) {score += containsWholeFactor * nameWeight;}
                    if (user.getEmail().toLowerCase().contains(search)) {score += containsWholeFactor * emailWeight;}
                    if (user.getBiography().toLowerCase().contains(search)) {score += containsWholeFactor * biographyWeight;}
                } catch (Exception ignore) {;}
                //Check words of query (space = ' ', '_', '-', '+')
                List<String> words = new ArrayList<>();
                words = getWordsFromString(search);
                for (String word : words) {
                    try {
                        if (user.getUsername().toLowerCase().contains(word)) {score += usernameWeight;}
                        if (user.getName().toLowerCase().contains(word)) {score += nameWeight;}
                        if (user.getEmail().toLowerCase().contains(word)) {score += emailWeight;}
                        if (user.getBiography().toLowerCase().contains(word)) {score += biographyWeight;}
                    } catch (Exception ignore) {;}
                }
            } catch (Exception ignore) {;}
            scores.add(score);
            sortedScores.add(score);
        }

        // Sort events based on scores
        Collections.sort(sortedScores); // ascending
        Collections.reverse(sortedScores); // descending

        for (int score : sortedScores) {
            /* no searchfilter but rather searchSort for /users
            if (score <= 0) {
                break;
            }*/
            sortedUsers.add(users.get(scores.indexOf(score)));
            scores.set(scores.indexOf(score), -1);
        }
        return sortedUsers;
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
        if (newUser.getEmail() != null){
            checkEmail(newUser.getEmail());
        }

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
        if (updatedUser.getEmail() != null){
            checkEmail(updatedUser.getEmail());
            user.setEmail(updatedUser.getEmail());
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized for this request");
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

    public User getUserByPartialUser(User partialUser) {
        return getUserByIDNum(partialUser.getId());
    } //may needs to be extended for other user information

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

    private void checkEmail(String email){
        if (!email.contains("@")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address");
        }
    }

    public void updateRepository(User user) {
        User createdUser = userRepository.save(user);
        userRepository.flush();
    }

    private String hashPassword(String passwordToHash) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

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

    public void linkImageToUser(User updatedUser, String image) {
        updatedUser.setImageFile(image);
        updateRepository(updatedUser);
    }
}
