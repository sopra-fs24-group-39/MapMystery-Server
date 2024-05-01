package ch.uzh.ifi.hase.soprafs24.rest.dto;
import java.util.List;
public class FriendrequestGetDTO {
    private List<String> friendrequests;

    public void setFriendrequests(List<String> friendrequests){this.friendrequests = friendrequests;}

    public List<String> getFriendrequests(){return this.friendrequests;}


}
