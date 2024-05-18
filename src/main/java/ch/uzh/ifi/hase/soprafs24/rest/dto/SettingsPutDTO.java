package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class SettingsPutDTO {
    private Long id = null;
    private String password = null;
    private String username = null;
    private String status = "UNDEF";
    private String creationdate = null;
    private String userEmail = null;
    private float score;
    private boolean verified;
    private List<String> friends;
    private float currentpoints;
    private String token = null;
    private  Boolean featured_in_rankings = null;
    private String authKey = null;
    private Long lobbyID;

    public Long getId(){
        return this.id;
    }

    public void setId(Long Id){
        this.id = Id;
    }

    public void setCreationdate(String creationdate){
        this.creationdate = creationdate;
    }

    public String getCreationdate(){
        return creationdate;
    }

    public float getScore(){
        return this.score;
    }

    public void setScore(float score){
        this.score = score;
    }

    public Boolean getVerified(){
        return this.verified;
    }

    public void setVerified(Boolean state){
        this.verified = state;
    }

    public List<String> getFriends(){
        return this.friends;
    }

    public void setFreinds(List<String> friends){
        this.friends = friends;
    }

    public float getCurrentpoints(){
        return this.currentpoints;
    }

    public void setCurrentpoints(int currentpoints){
        this.currentpoints = currentpoints;
    }

    public String getUserEmail(){
        return this.userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public Boolean getFeatured_in_rankings(){
        return this.featured_in_rankings;
    }
    public void setFeatured_in_rankings(Boolean featured_in_rankings){
        this.featured_in_rankings = featured_in_rankings;
    }
    public String getToken() {
        return this.token;
    }

    // Setter method for token
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
    public void setAuthKey(String authKey){this.authKey = authKey;}
    public String getAuthKey(){return authKey;}

    public Long getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(Long lobbyID) {
        this.lobbyID = lobbyID;
    }

}
