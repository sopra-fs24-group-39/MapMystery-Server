package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);

  User findById(long user_Id);

  User findByUserEmail(String userEmail);
  User findByVerificationCode(String verificationCode);
}

