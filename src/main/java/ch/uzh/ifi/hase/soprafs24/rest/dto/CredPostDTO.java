package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class CredPostDTO {
    private String password;
    private String username;
    private String email;
    

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

    public void setUserEmail(String email){this.email = email;}
    public String getEmail(){return email;}
}