package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.User;
import teameins.lecturerassignmentsystem.model.db.relation.UserHasRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    @Query("select r from UserHasRole r where r.user.id = :userId")
    List<UserHasRole> findRolesForUser(@Param("userId") int userId);
}
