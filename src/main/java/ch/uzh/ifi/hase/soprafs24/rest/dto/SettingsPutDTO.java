package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class SettingsPutDTO {
    private String password = null;
    private String username = null;
    private String userEmail = null;
    private  Boolean featured_in_rankings = null;
    private Boolean accept_friendrequests = null;
    private Integer profilepicture = null;

    private String status = "UNDEF";

    public String getStatus(){return this.status;}

    public void setStatus(String status){this.status = status;}

    public Integer getProfilepicture(){return this.profilepicture;}

    public void setProfilepicture(Integer profilepicture){this.profilepicture = profilepicture;}

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
    public Boolean getAccept_friendrequests(){return this.accept_friendrequests;}

    public void setAccept_friendrequests(Boolean accept_friendrequests){this.accept_friendrequests = accept_friendrequests;}

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

}
