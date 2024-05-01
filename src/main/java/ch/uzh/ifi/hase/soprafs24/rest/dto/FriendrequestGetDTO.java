package ch.uzh.ifi.hase.soprafs24.rest.dto;
import java.util.List;
public class FriendrequestGetDTO {
    private List<String> friendrequests = null;

    public void SetFriendrequests(List<String> friendrequests){this.friendrequests = friendrequests;}

    public List<String> GetFriendrequests(){return this.friendrequests;}


}
