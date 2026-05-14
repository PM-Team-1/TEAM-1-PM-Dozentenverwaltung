package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.Role;
import teameins.lecturerassignmentsystem.model.db.relation.UserHasRole;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("select r from UserHasRole r where r.role.id = :roleId")
    List<UserHasRole> findUsersWithRole(@Param("roleId") int roleId);

    Optional<Role> findByName(String admin);
}
