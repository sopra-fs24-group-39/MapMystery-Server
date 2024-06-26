package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.hibernate.internal.ExceptionConverterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
@EnableScheduling
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    // demo : Evey minute at 10 seconds into the minute @Scheduled(cron = "10 * * * * ?")
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updatepointsofthismonthforallusers() throws Exception {
        try {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                user.setPointsthismonth(0);
                userRepository.save(user);
                userRepository.flush();
                System.out.println("All pointsthismonth of users set to 0");
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "When getting all of the users from the respository went wrong");
        }
    }
    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() throws Exception{
        try{
          return this.userRepository.findAll();
        }
        catch (Exception e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()+"Could not return all users from Userrepository");
        }

    }

    public User createUser(User newUser) throws Exception{
      try{
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
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage()+"Could not create User");
      }
    }


    public void updateUser(User to_be_updated_user, User user_with_new_data) throws Exception {
      try{
        // TO DO: Implement Uniqueness check on the username, email?
        to_be_updated_user.update(user_with_new_data);
        userRepository.saveAndFlush(to_be_updated_user);

      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage()+"Could not update User");
      }
    }

    public void updateUserSettings(User to_be_updated_user, User user_with_new_data) throws Exception{
        if(user_with_new_data.getUsername() != null) {
            checkifUsernameexists(user_with_new_data);
        }
        if(user_with_new_data.getUserEmail() != null) {
            checkifEmailexists(user_with_new_data);
        }
        to_be_updated_user.update(user_with_new_data);
        userRepository.saveAndFlush(to_be_updated_user);
    }
    public void checkifUsernameexists(User user_with_new_data)throws Exception{
        try {
            User user = userRepository.findByUsername(user_with_new_data.getUsername());
            if(user != null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Username : "+ user_with_new_data.getUsername()+ " is already taken");
            }
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something unexpected happened when looking for the user in the userRespository");

        }
    }

    public void checkifEmailexists(User user_with_new_data)throws Exception{
        try{
            User user = userRepository.findByUserEmail(user_with_new_data.getUserEmail());
            if(user != null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Email : "+ user_with_new_data.getUserEmail()+ " is already taken");
            }
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something unexpected happened when looking for the user in the userRespository");
        }
    }


    public User getUser(long user_Id) throws Exception {
      try{
        User user = userRepository.findById(user_Id);
        return user;
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()+"Could not find User with given UserId");
      }
    }

    public void addfriendrequest(User user_recipient, User user_sender) throws Exception {
      try{
          List<String> friends = user_recipient.getFriends();
          if(friends.contains(user_sender.getUsername())){
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already friends with " + user_recipient.getUsername());
          }
        // Null check for the user to whom the friend request is sent
        if (user_recipient == null || user_sender == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either the user who receives the friendrequest or the user who sent the friend request is null");
        }
        List<String> friendRequests = user_recipient.getFriendrequests();

        if (friendRequests == null) {
            List<String> usernameList = new ArrayList<>(); // Create a new ArrayList to store usernames
            String secondUsername = user_sender.getUsername(); // Assuming getUsername() returns the username of the user
            usernameList.add(secondUsername); // Add the second user's username to the list
            user_recipient.setFriendrequests(usernameList);
        }
                // Check if the friend request doesn't already exist
        if (!friendRequests.contains(user_sender.getUsername())) {
            friendRequests.add(user_sender.getUsername());
            user_recipient.setFriendrequests(friendRequests);
        }
        userRepository.save(user_recipient);
        userRepository.flush();
    }
    catch(ResponseStatusException e){
          throw e;
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could not add Friends Request");
    }


    }
    public void acceptfriendrequest(User receiver, User sender) throws Exception{
      try{
        if(!receiver.getFriendrequests().contains(sender.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The friend to be added is not in the friendrequest list");
        }
        List<String> friendrequestslist = receiver.getFriendrequests();
        friendrequestslist.remove(sender.getUsername());
        receiver.setFriendrequests(friendrequestslist);

        List<String> friendslist = receiver.getFriends();
        friendslist.add(sender.getUsername());
        receiver.setFriends(friendslist);

        List<String> senderfriend_list = sender.getFriends();
        senderfriend_list.add(receiver.getUsername());
        sender.setFriends(senderfriend_list);

        userRepository.save(receiver);
        userRepository.save(sender);
        userRepository.flush();
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could not accept friendrequest");
      }
    }

    public void declinefriendrequest(User receiver, User sender) throws Exception{
      try{
        if(receiver.getFriendrequests().contains(sender.getUsername())){
            List<String> friendrequestslist = receiver.getFriendrequests();
            friendrequestslist.remove(sender.getUsername());
            receiver.setFriendrequests(friendrequestslist);

            userRepository.save(receiver);
            userRepository.flush();

        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username, whose friendrequest is to be declined isn't in the list of friendrequests of the receiver");
        }

      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could decline Friends reqeuest");
      }
    }

    public void removefriendship(User friend1, User friend2) throws Exception{
      try{
        if (friend1 == null || friend2 == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One of the 2 user inputs is equal to null");
        }
        List<String> friends_of_friend1 =  friend1.getFriends();
        if(!friends_of_friend1.contains(friend2.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user with the username "+ friend2.getUsername() + " was not found in the friends list of the user with username " + friend1.getUsername());
        }
        List<String> friends_of_friend2 = friend2.getFriends();
        if(!friends_of_friend2.contains(friend1.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user with the username "+ friend1.getUsername() + " was not found in the friends list of the user with username " + friend2.getUsername());
        }

        friends_of_friend1.remove(friend2.getUsername());
        friends_of_friend2.remove(friend1.getUsername());


        friend1.setFriends(friends_of_friend1);
        friend2.setFriends(friends_of_friend2);

        userRepository.save(friend1);
        userRepository.save(friend2);
        userRepository.flush();
 
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could not remove friends");
      }
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

    private void checkIfUserExists(User userToBeCreated) throws Exception{
      try{
        // Ensure email is unique
        // Ensure username is unique
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByEmail = userRepository.findByUserEmail(userToBeCreated.getUserEmail());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage, "username", "is"));
        }
        if (userByEmail != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage, "email", "is"));
 
        }
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could not check if user exists");

      }

    }


    /**
     * @param userToBeCreated the user whose password should be checked if it
     *                        satisfies all password requirements
     *                        throws errors should the password not satisfy some requirements
     */
    public void checkPassword(User userToBeCreated) throws Exception{
      try{
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


        if (!HasUpperCase) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The password doesn't contain an Upper Case Letter [A-Z]"));
        }
        if (!hasNumerical) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The password doesn't contain a numerical character [0-9]"));
        }
        if (!hasSpecialChar) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The password doesn't contain special Character."));
        }


      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()+"Could not check Password");
      }

    }


    public User getUser(String username) throws Exception {
      try{
        return this.userRepository.findByUsername(username);
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()+"Could not find Username with given username");
      }
    }

    public User getUserByVerificationToken(String verificationCode) throws Exception {
      try{
        return this.userRepository.findByVerificationCode(verificationCode);
      }
      catch (Exception e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()+"Could not find user with given verificationToken");
      }
    }

    public void deleteUser(User user) throws Exception{
        try {
            this.userRepository.delete(user);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User could not be deleted");
        }

    }


}
