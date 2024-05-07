package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class FriendrequestPutDTO {
    private String username;

    private Boolean accepted;

    public  void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){return this.username;}

    public void setAccepted(Boolean accepted){this.accepted = accepted;}

    public Boolean getAccepted(){return this.accepted;}
}
