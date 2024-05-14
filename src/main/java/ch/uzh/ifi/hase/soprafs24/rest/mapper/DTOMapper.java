package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GuessResult;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "id", target = "id",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "username", target = "username",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "password", target = "password",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "creationdate", target = "creationdate",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "status", target = "status",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "userEmail",target = "userEmail",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "score", target = "score",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "friends", target = "friends",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "currentpoints",target = "currentpoints",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "featured_in_rankings",target = "featured_in_rankings",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "verified",target = "verified",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User convertUserPutDTOtoEntity(UserPutDTO user);

@Mapping(source = "id", target = "id",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "username", target = "username",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "password", target = "password",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "creationdate", target = "creationdate",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "status", target = "status",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "userEmail",target = "userEmail",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "score", target = "score",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "friends", target = "friends",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "currentpoints",target = "currentpoints",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "featured_in_rankings",target = "featured_in_rankings",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(source = "verified",target = "verified",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
User convertSettingsPutDTOtoEntity(SettingsPutDTO settingsPutDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "creationdate", target = "creationdate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "userEmail",target = "userEmail")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "friends", target = "friends")
  @Mapping(source = "currentpoints",target = "currentpoints")
  @Mapping(source = "featured_in_rankings",target = "featured_in_rankings")
  @Mapping(source = "verified",target = "verified")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "friendrequests", target = "friendrequests")
  FriendrequestGetDTO convertEntityToFriendrequestGetDTO(User user);


  @Mapping(source="username",target="username")
  @Mapping(source="password",target="password")
  @Mapping(source="userEmail",target="userEmail")
  User convertCredPostDTOtoEntity(CredPostDTO credentials);

  @Mapping(source="distance",target="distance")
  @Mapping(source="timeDelta",target="timeDelta")
  @Mapping(source="playerId",target="playerId")
  GuessResult convertGuessResultDTOtoEntity(GuessResultPutDTO resuls);
}
