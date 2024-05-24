package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.GuessResult;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CredPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FriendrequestGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GuessResultPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.SettingsPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-24T09:47:26+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class DTOMapperImpl implements DTOMapper {

    @Override
    public User convertUserPutDTOtoEntity(UserPutDTO user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setScore( user.getScore() );
        if ( user.getPassword() != null ) {
            user1.setPassword( user.getPassword() );
        }
        if ( user.getCreationdate() != null ) {
            user1.setCreationdate( user.getCreationdate() );
        }
        user1.setCurrentpoints( user.getCurrentpoints() );
        if ( user.getVerified() != null ) {
            user1.setVerified( user.getVerified() );
        }
        if ( user.getUserEmail() != null ) {
            user1.setUserEmail( user.getUserEmail() );
        }
        if ( user.getFeatured_in_rankings() != null ) {
            user1.setFeatured_in_rankings( user.getFeatured_in_rankings() );
        }
        if ( user.getId() != null ) {
            user1.setId( user.getId() );
        }
        List<String> list = user.getFriends();
        if ( list != null ) {
            user1.setFriends( new ArrayList<String>( list ) );
        }
        if ( user.getUsername() != null ) {
            user1.setUsername( user.getUsername() );
        }
        if ( user.getStatus() != null ) {
            user1.setStatus( user.getStatus() );
        }

        return user1;
    }

    @Override
    public User convertSettingsPutDTOtoEntity(SettingsPutDTO settingsPutDTO) {
        if ( settingsPutDTO == null ) {
            return null;
        }

        User user = new User();

        if ( settingsPutDTO.getPassword() != null ) {
            user.setPassword( settingsPutDTO.getPassword() );
        }
        if ( settingsPutDTO.getAccept_friendrequests() != null ) {
            user.setAccept_friendrequests( settingsPutDTO.getAccept_friendrequests() );
        }
        if ( settingsPutDTO.getProfilepicture() != null ) {
            user.setProfilepicture( settingsPutDTO.getProfilepicture() );
        }
        if ( settingsPutDTO.getUserEmail() != null ) {
            user.setUserEmail( settingsPutDTO.getUserEmail() );
        }
        if ( settingsPutDTO.getFeatured_in_rankings() != null ) {
            user.setFeatured_in_rankings( settingsPutDTO.getFeatured_in_rankings() );
        }
        if ( settingsPutDTO.getStatus() != null ) {
            user.setStatus( settingsPutDTO.getStatus() );
        }
        if ( settingsPutDTO.getUsername() != null ) {
            user.setUsername( settingsPutDTO.getUsername() );
        }

        return user;
    }

    @Override
    public UserGetDTO convertEntityToUserGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserGetDTO userGetDTO = new UserGetDTO();

        userGetDTO.setCreationdate( user.getCreationdate() );
        userGetDTO.setVerified( user.getVerified() );
        userGetDTO.setPointsthismonth( user.getPointsthismonth() );
        List<String> list = user.getFriends();
        if ( list != null ) {
            userGetDTO.setFriends( new ArrayList<String>( list ) );
        }
        userGetDTO.setScore( user.getScore() );
        userGetDTO.setPassword( user.getPassword() );
        userGetDTO.setCurrentpoints( user.getCurrentpoints() );
        userGetDTO.setUserEmail( user.getUserEmail() );
        userGetDTO.setFeatured_in_rankings( user.getFeatured_in_rankings() );
        userGetDTO.setId( user.getId() );
        userGetDTO.setUsername( user.getUsername() );
        userGetDTO.setStatus( user.getStatus() );
        userGetDTO.setAccept_friendrequests( user.getAccept_friendrequests() );
        userGetDTO.setProfilepicture( user.getProfilepicture() );
        List<String> list1 = user.getFriendrequests();
        if ( list1 != null ) {
            userGetDTO.setFriendrequests( new ArrayList<String>( list1 ) );
        }

        return userGetDTO;
    }

    @Override
    public FriendrequestGetDTO convertEntityToFriendrequestGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        FriendrequestGetDTO friendrequestGetDTO = new FriendrequestGetDTO();

        List<String> list = user.getFriendrequests();
        if ( list != null ) {
            friendrequestGetDTO.setFriendrequests( new ArrayList<String>( list ) );
        }

        return friendrequestGetDTO;
    }

    @Override
    public User convertCredPostDTOtoEntity(CredPostDTO credentials) {
        if ( credentials == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( credentials.getPassword() );
        user.setUserEmail( credentials.getUserEmail() );
        user.setUsername( credentials.getUsername() );

        return user;
    }

    @Override
    public GuessResult convertGuessResultDTOtoEntity(GuessResultPutDTO resuls) {
        if ( resuls == null ) {
            return null;
        }

        GuessResult guessResult = new GuessResult();

        guessResult.setDistance( resuls.getDistance() );
        guessResult.setTimeDelta( resuls.getTimeDelta() );
        guessResult.setPlayerId( resuls.getPlayerId() );

        return guessResult;
    }
}
