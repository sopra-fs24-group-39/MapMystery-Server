package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class UserGetDTO {

  private Long id;
  private String token;
  private String password;
  private String username;
  private String status = "UNDEF";
  private String creationdate;

  public Long getId() {
    return id;
  }

  public void setId(long UserId) {
    this.id = UserId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    assert "OFFLINE".equals(status) || "ONLINE".equals(status) || "UNDEF".equals(status);
    this.status = status;
  }

  public String getCreationdate(){
    return creationdate;
  }

  public void setCreationdate(String creationdate){
    this.creationdate = creationdate;
  }
}