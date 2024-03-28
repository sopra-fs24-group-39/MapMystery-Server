package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

// This handles the updates of a profile, current version will support the update of
// the email-address, password, username & boolean whether the user wants his profile to be displayed in rankings

// This currently can also handle the update of the status, though it might be useful to create a seperate DTI instance, since the two things are called seperately

public class UserPutDTO {

  private Long id = null;
  private String token = null;
  private String password = null;
  private String username = null;
  private String status = "UNDEF";
  private String email = null;
  private String creationdate = null;
  private String score;
  private List<UserPutDTO> friends;
  private int currentpoints;
  private  Boolean featured_in_rankings = null;

  public Long getId(){
    return this.id;
  }

  public void setId(Long Id){
    this.id = Id;
  }

  public String getToken(){
    return this.token;
  }

  public void setToken(String token){
    this.token = token;
  }

  public String getScore(){
    return this.score;
  }

  public void setScore(String score){
    this.score = score;
  }

  public List<UserPutDTO> getFriends(){
    return this.friends;
  }

  public void setFreinds(List<UserPutDTO> friends){
    this.friends = friends;
  }

  public int getCurrentpoints(){
    return this.currentpoints;
  }

  public void setCurrentpoints(int currentpoints){
    this.currentpoints = currentpoints;
  }

  public String getCreationdate(){
    return creationdate;
  }

  public void setCreationdate(String creationdate){
    this.creationdate = creationdate;
  }

  public String getEmail(){
      return this.email;
  }
  public void setEmail(String Email){
      this.email = Email;
  }

  public Boolean getFeatured_in_rankings(){
      return this.featured_in_rankings;
  }
  public void setFeatured_in_rankings(Boolean featured_in_rankings){
      this.featured_in_rankings = featured_in_rankings;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getStatus(){
    return status;
  }

  public void setUserStatus(String status){
    assert "OFFLINE".equals(status) || "ONLINE".equals(status) || "UNDEF".equals(status);
    this.status = status;
  }

}