package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class UserPutDTO {

  private Long userId = null;
  private String token = null;
  private String password = null;
  private String username = null;
  private String status = "UNDEF";
  private String creationdate = null;

  private String email = null;

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String Email){
        this.email = Email;
    }

  public Long getUserId(){
    return userId;
  }

  public void setUserId(long UserId){
    this.userId = UserId;
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

  public String getStatus(){
    return status;
  }

  public void setUserStatus(String status){
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