package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.service.EmailSenderService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    checkIfUserExists(newUser);
    checkPassword(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    String currentDate = java.time.LocalDate.now().toString();
    newUser.setCreationdate(currentDate);
    newUser.setStatus("OFFLINE");
    newUser.setVerified(false);
    newUser = userRepository.save(newUser);
    //important, token can only be set after ID was given!
    userRepository.flush();

    return newUser;
  }


  public void updateUser(User to_be_updated_user, User user_with_new_data){
    // TO DO: Implement Uniqueness check on the username, email?
    to_be_updated_user.update(user_with_new_data);
    userRepository.save(to_be_updated_user);
    userRepository.flush();
  }


  public User getUser(long user_Id){
    Optional<User> optionalUser = userRepository.findById(user_Id);
    return optionalUser.orElseThrow(() -> new RuntimeException("User not found with id: " + user_Id));
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
      // Ensure email is unique
      // Ensure username is unique
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByEmail = userRepository.findByUserEmail(userToBeCreated.getUserEmail());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username", "is"));
    }
    if (userByEmail != null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format(baseErrorMessage, "email","is"));
    }

  }

  
  public void checkPassword(User userToBeCreated){
    String pwd = userToBeCreated.getPassword();


    Pattern uppCase = Pattern.compile("[A-Z]");
    Matcher uppCaseMatch = uppCase.matcher(pwd);
    boolean HasUpperCase = uppCaseMatch.find();


    // Check for at least one numerical digit
    Pattern numericalPattern = Pattern.compile("[0-9]");
    Matcher numericalMatcher = numericalPattern.matcher(pwd);
    boolean hasNumerical = numericalMatcher.find();

    // Check for at least one special character
    Pattern specialCharPattern = Pattern.compile("[^A-Za-z0-9]");
    Matcher specialCharMatcher = specialCharPattern.matcher(pwd);
    boolean hasSpecialChar = specialCharMatcher.find();


    if (!HasUpperCase){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
      String.format("The password doesn't contain an Upper Case Letter [A-Z]"));
    }
    if (!hasNumerical){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
      String.format("The password doesn't contain a numerical character [0-9]"));
    }
    if (!hasSpecialChar){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
      String.format("The password doesn't contain special Character."));
    }



  }

  
  public User getUser(String username){
    return this.userRepository.findByUsername(username);
  }

  public User getUserByVerificationToken(String verificationCode) {
    return this.userRepository.findByVerificationCode(verificationCode);
  }

  public void deleteUser(User user){
    try {
      this.userRepository.delete(user);
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"User could not be deleted");
    }
  }


}
