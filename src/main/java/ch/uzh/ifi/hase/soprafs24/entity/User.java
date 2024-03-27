package ch.uzh.ifi.hase.soprafs24.entity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String useremail;

    @Column(nullable = false)
    private String password;

    @Column
    private String password;

    // TODO: needs to be changed to type Date according to class diagram
    @Column(nullable = false)
    private String creationdate;

    // TODO: needs to be changed to type tuple according to class diagram
    @Column
    private String score;

    @Transient // Marking this as transient to avoid persistence issues, adjust according to your JPA implementation details
    private List<User> friends;

    @Column
    private int currentpoints;

    @Column
    private Boolean featured_in_rankings = true;

    private String generateToken() {
      return Jwts.builder()
        .setSubject(this.username)
        .signWith(SignatureAlgorithm.HS512, "secrete_key")
        .compact();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.useremail;
    }

    public void setEmail(String email) {
        this.useremail = email;
    }

    public Boolean getFeatured_in_rankings() {
        return this.featured_in_rankings;
    }

    public void setFeatured_in_rankings(Boolean featured_in_rankings) {
        this.featured_in_rankings = featured_in_rankings;
    }

    public String getToken() {
        return token;
    }

    public void setToken() {
        this.token = this.generateToken();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
      this.password = encoder.encode(password);
    }
  
    public boolean checkPassword(String pw) {
        return encoder.matches(pw, this.password);
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public int getCurrentpoints() {
        return currentpoints;
    }

    public void setCurrentpoints(int currentpoints) {
        this.currentpoints = currentpoints;
    }

    public void update(User user_with_new_data){
        if (user_with_new_data.getPassword() != null && !user_with_new_data.getPassword().isEmpty()) {
            this.setPassword(user_with_new_data.getPassword());
        }

        if (user_with_new_data.getUsername() != null && !user_with_new_data.getUsername().isEmpty()) {
            this.setUsername(user_with_new_data.getUsername());
        }

        if (user_with_new_data.getStatus() != "UNDEF" && !user_with_new_data.getStatus().isEmpty()) {
            this.setStatus(user_with_new_data.getStatus());
        }

        if (user_with_new_data.getEmail() != null && !user_with_new_data.getEmail().isEmpty()) {
            this.setEmail(user_with_new_data.getEmail());
        }

        if (user_with_new_data.getFeatured_in_rankings() != null && user_with_new_data.getFeatured_in_rankings() != null) {
            this.setFeatured_in_rankings(user_with_new_data.getFeatured_in_rankings());
        }


        }

    }
}

