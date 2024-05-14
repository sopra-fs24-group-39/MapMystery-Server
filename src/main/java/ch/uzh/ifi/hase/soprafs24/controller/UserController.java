/**
 * Author: David Sanchez
 */


package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import ch.uzh.ifi.hase.soprafs24.service.AccountService;

import org.hibernate.internal.ExceptionConverterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {


    private final UserService userService;
    private AccountService accountService;

    @Autowired
    private UtilityService util;

    UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    /**
     * @return returns a list of Users with all of their properties, where the passwords are hashed
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> allUsersGet() throws Exception{

      // fetch all users in the internal representation
      List<User> users = userService.getUsers();
      List<UserGetDTO> userGetDTOs = new ArrayList<>();

      // convert each user to the API representation
      for (User user : users) {
          userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }
      return userGetDTOs;

    }

    /**
     * @param userId pathvariable of request, the unique Id of user
     * @param token  of the user for authentication
     * @return returns a User object with all of its properties
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO userGet(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) throws Exception {
      User user = userService.getUser(userId);
      util.Assert(user.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    /**
     * @param userId pathvariable of request, the unique Id of user
     * @param token  of the user for authentication
     */
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void userDeletion(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) throws Exception{

      User user = userService.getUser(userId);
      util.Assert(user.getToken() == token, "the provided token did not match the token expected in the Usercontroller");

      userService.deleteUser(user);

    }

    /**
     * @param credentials Username and password and emailfor the user to be created
     * @return returns the user with all of its properties, in particular the token which will be needed for
     * future requests with an id for example Users/userId where userId is the pathvariable
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO userCeration(@RequestBody CredPostDTO credentials) throws Exception {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertCredPostDTOtoEntity(credentials);

        // create user, throws Excpetion if username or email already exists
        User createdUser = userService.createUser(userInput);

        // try {
        //     accountService.sendVerificationEmail(createdUser);
        // }
        // catch (Exception e) {
        //     throw new RuntimeException(e.getMessage());
        // }

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }


    /**
     * Updates the user object with the attributes in the NewUserData param,
     * if an attribute is unspecified (not present in the param) nothing is changed
     *
     * @param userId      the Id of the user Object to be updated
     * @param NewUserData the Data as an User object which shoudl replace the old attributes
     * @param token       user token for authnetication
     */
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userUpdate(@PathVariable long userId, @RequestBody UserPutDTO NewUserData, @RequestHeader(value = "Authorization") String token) throws Exception{
      User oldData = userService.getUser(userId);
      util.Assert(oldData.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");


      User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(NewUserData);

      userService.updateUser(oldData, userInput);

    }

    /**
     * endpoint which changes the user state and returns the user Object with all of its properties
     * in particular the token which will be used in requests using id as a pathvarible example : get Usres/Id
     *
     * @param credentials username and password
     * @return the user object with all of its properties
     */
    // To do throw a 401 when the password is incorrect
    @PutMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, Object> userLogin(@RequestBody CredPostDTO credentials) throws Exception {
      User user = userService.getUser(credentials.getUsername());

      if (!user.checkPassword(credentials.getPassword())) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "password is wrong");
      }

      User newStatus = new User();

      newStatus.setStatus("ONLINE");

      userService.updateUser(user, newStatus);


      // the token needed for subsequent requests is in the authUser object
      UserGetDTO authUser = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

      String token = user.getToken();

      //prepearing response
      Map<String, Object> response = new HashMap<>();
      response.put("user", authUser);
      response.put("token", token);

      return response;

    }

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) throws Exception{
        User user = userService.getUserByVerificationToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token.");
        }
        // Mark the user as verified
        user.setVerified(true);
        userService.updateUser(user, user);

        return ResponseEntity.ok("Account verified successfully.");
    }
// ##################################### Start Friends Section #################################################################

    /**
     * This section deals with:
     * Creating Friend Requests,
     * Loading all friendrequests of a user,
     * Declining / accepting a friend request,
     * Removing Friends,
     * Loading all friends
     */
    @PutMapping("/friends/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void sendfriendrequest(@PathVariable long userId, @RequestBody UserPutDTO FromUserData, @RequestHeader(value = "Authorization") String token) {
        try {
            User user_who_sent_friend_request = userService.getUser(userId);
            util.Assert(user_who_sent_friend_request.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");

            User user_to_which_friend_request_is_sent = userService.getUser(DTOMapper.INSTANCE.convertUserPutDTOtoEntity(FromUserData).getUsername());

            userService.addfriendrequest(user_to_which_friend_request_is_sent, user_who_sent_friend_request);

        }
        catch (AssertionError e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(e.getMessage(), userId));
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/friends/friendrequests/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FriendrequestGetDTO getfriendrequests(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) throws Exception{
        User user = userService.getUser(userId);
        util.Assert(user.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
        return DTOMapper.INSTANCE.convertEntityToFriendrequestGetDTO(user);
    }

    @PutMapping("/friends/friendrequests/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void accept_or_decline_friend_request(@PathVariable long userId, @RequestBody FriendrequestPutDTO friendrequestPutDTO, @RequestHeader(value = "Authorization") String token) throws Exception{

        User receiver = userService.getUser(userId);
        User sender = userService.getUser(friendrequestPutDTO.getUsername());

        util.Assert(receiver.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");

        if(friendrequestPutDTO.getAccepted()){
            userService.acceptfriendrequest(receiver, sender);
        }
        else{
            userService.declinefriendrequest(receiver, sender);
        }



    }


    @GetMapping("/friends/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> returnfriendsofuser(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) throws Exception{
        User user = userService.getUser(userId);
        util.Assert(user.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
        List<UserGetDTO> userGetDTOS = new ArrayList<>();
        List<String> friends = user.getFriends();

        // convert each user to the API representation
        for (String username : friends ) {
            User tmpuser = userService.getUser(username);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(tmpuser));
        }
        return userGetDTOS;

    }

    @DeleteMapping("/friends/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removefriendship(@PathVariable long userId, @RequestBody FriendrequestPutDTO friendrequestPutDTO, @RequestHeader(value = "Authorization") String token) throws Exception{
        User friend1 = userService.getUser(userId);
        util.Assert(friend1.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
        User friend2 = userService.getUser(friendrequestPutDTO.getUsername());
        userService.removefriendship(friend1, friend2);

    }

// ##################################### End Friends Section #################################################################



// ##################################### Start Settings Section #################################################################

    @PutMapping("/settings/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateuser(@PathVariable long userId, @RequestBody SettingsPutDTO settingsPutDTO, @RequestHeader(value = "Authorization") String token) throws Exception{

            User user = userService.getUser(userId);
            User updated_user = DTOMapper.INSTANCE.convertSettingsPutDTOtoEntity(settingsPutDTO);


    }
}
