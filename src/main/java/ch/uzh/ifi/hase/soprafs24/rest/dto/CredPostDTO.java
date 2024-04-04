package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class CredPostDTO {
    private String password;
    private String username;
    private String userEmail;
    

    public String getPassword(){
        return password;
    }

    public void setPassword(String pw){
        this.password = pw;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setUserEmail(String email){
      this.userEmail = email;
    }

    public String getUserEmail(){
      return userEmail;
    }
}