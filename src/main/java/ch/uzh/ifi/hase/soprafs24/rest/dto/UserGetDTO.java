package ch.uzh.ifi.hase.soprafs24.rest.dto;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;


// This handles the updates of a profile, current version will support the update of
// the email-address, password, username & boolean whether the user wants his profile to be displayed in rankings

// This currently can also handle the update of the status, though it might be useful to create a seperate DTI instance, since the two things are called seperately

public class UserGetDTO {

    private Long id = null;
    private String password = null;
    private String username = null;
    private String status = "UNDEF";
    private String creationdate = null;
    private String userEmail = null;
    private float score;
    private List<String> friends;
    private List<String> friendrequests;
    private float currentpoints;
    private float pointsthismonth;
    private Boolean verified;
    private Boolean featured_in_rankings = null;

    private Boolean accept_friendrequests = null;

    private Integer profilepicture = null;

    public Long getId() {
        return this.id;
    }

    public void setId(Long Id) {
        this.id = Id;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public void setVerified(Boolean state) {
        this.verified = state;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getFriends() {
        return this.friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public float getCurrentpoints() {
        return this.currentpoints;
    }

    public void setCurrentpoints(float currentpoints) {
        this.currentpoints = currentpoints;
    }

    public void setPointsthismonth(float pointsthismonth) {
        this.pointsthismonth = pointsthismonth;
    }

    public float getPointsthismonth() {
        return pointsthismonth;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getFeatured_in_rankings() {
        return this.featured_in_rankings;
    }

    public void setFeatured_in_rankings(Boolean featured_in_rankings) {
        this.featured_in_rankings = featured_in_rankings;
    }
    public Boolean getAccept_friendrequests(){return this.accept_friendrequests;}

    public void setAccept_friendrequests(Boolean accept_friendrequests){this.accept_friendrequests = accept_friendrequests;}

    public Integer getProfilepicture(){return this.profilepicture;}

    public void setProfilepicture(Integer profilepicture){this.profilepicture = profilepicture;}
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

    public void setFriendrequests(List<String> friendrequests) {
        this.friendrequests = friendrequests;
    }

    public List<String> getFriendrequests() {
        return this.friendrequests;
    }


}