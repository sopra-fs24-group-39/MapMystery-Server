/**
 * Author: David Sanchez
 */


package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CredPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import ch.uzh.ifi.hase.soprafs24.service.AccountService;

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
  private UtilityService utilityService;

  UserController(UserService userService, AccountService accountService) {
    this.userService = userService;
    this.accountService = accountService;
  }

  /**
   * Unsecure, would need also some kind of authentication to prevent outside entities of accesssing this data
   * @return returns a list of Users with all of their properties
   */
  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> allUsersGet() {

    try{
      // fetch all users in the internal representation
      List<User> users = userService.getUsers();
      List<UserGetDTO> userGetDTOs = new ArrayList<>();

      // convert each user to the API representation
      for (User user : users) {
        userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }
      return userGetDTOs;
    }
    catch(Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error occured "+e.getMessage());
    }
    
  }

  /**
   * 
   * @param userId pathvariable of request, the unique Id of user
   * @param token of the user for authentication
   * @return returns a User object with all of its properties
   */
  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO userGet(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) {

    try{
      User user = userService.getUser(userId);
      assert user.getToken().equals(token);

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    } 
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid! "+e.getMessage());
    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with %d not found "+e.getMessage(), userId));
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage() );
    }
  
  }

  /**
   * 
   * @param userId pathvariable of request, the unique Id of user
   * @param token of the user for authentication
   */
  @DeleteMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void userDeletion(@PathVariable long userId, @RequestHeader(value = "Authorization") String token) {

    try{
      User user = userService.getUser(userId);
      assert user.getToken().equals(token);

      userService.deleteUser(user);
    } 
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid! "+e.getMessage());
    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with %d not found "+e.getMessage(), userId));
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage());
    }
  
  }

  /**
   * 
   * @param credentials Username and password and emailfor the user to be created
   * @return returns the user with all of its properties, in particular the token which will be needed for
   * future requests with an id for example Users/userId where userId is the pathvariable
   */
  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO userCeration(@RequestBody CredPostDTO credentials) {
    try{

      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertCredPostDTOtoEntity(credentials);

      // create user, throws Excpetion if username or email already exists
      User createdUser = userService.createUser(userInput);

      try {
        accountService.sendVerificationEmail(createdUser);
      }
      catch (Exception e){
        throw new RuntimeException("E-mail verification failed "+e.getMessage());
      }

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "add user failed because username already exists or E-Mail does not exist "+e.getMessage());
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage());
    }
  }


  /**
   * Updates the user object with the attributes in the NewUserData param,
   * if an attribute is unspecified (not present in the param) nothing is changed
   * @param userId the Id of the user Object to be updated
   * @param NewUserData the Data as an User object which shoudl replace the old attributes
   * @param token user token for authnetication
   */
  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void userUpdate(@PathVariable long userId, @RequestBody UserPutDTO NewUserData,@RequestHeader(value = "Authorization") String token) {
    try{
      User oldData = userService.getUser(userId);
      assert oldData.getToken().equals(token);


      User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(NewUserData);

      userService.updateUser(oldData, userInput);

    }
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid!");
    }
    catch(RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with %d not found", userId));
    }
    catch(Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error occured");
    }
    
  }

  /**
   * endpoint which changes the user state and returns the user Object with all of its properties
   * in particular the token which will be used in requests using id as a pathvarible example : get Usres/Id
   * @param credentials username and password
   * @return the user object with all of its properties
   */
  @PutMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, Object> userLogin(@RequestBody CredPostDTO credentials){
    try{
      User user = userService.getUser(credentials.getUsername());

      if(!user.checkPassword(credentials.getPassword())){
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"password is wrong");
      }

      // TODO : verification somehow not working
      // if (!user.getVerified()) {
      //   throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Account is not verified");
      // }
    

      User newStatus = new User();

      newStatus.setStatus("ONLINE");

      userService.updateUser(user,newStatus);

      
      // the token needed for subsequent requests is in the authUser object
      UserGetDTO authUser = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

      String token = user.getToken();
      
      //prepearing response
      Map<String, Object> response = new HashMap<>();
      response.put("user", authUser);
      response.put("token", token);

      return response;

    }
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Wrong password for login or Account is not verified");
    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found with given userid");
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error occured");
    }

  }

  @GetMapping("/verify-account")
  public ResponseEntity<String> verifyAccount(@RequestParam("token") String token){
    User user = userService.getUserByVerificationToken(token);

    if(user == null){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token.");
    }
    // Mark the user as verified
    user.setVerified(true);
    userService.updateUser(user,user);

    return ResponseEntity.ok("Account verified successfully.");
  }
}
