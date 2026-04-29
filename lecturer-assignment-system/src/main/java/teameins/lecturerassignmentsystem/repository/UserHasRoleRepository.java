package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.repository.CrudRepository;
import teameins.lecturerassignmentsystem.model.db.relation.UserHasRole;

import java.util.List;

public interface UserHasRoleRepository extends CrudRepository<UserHasRole, Integer> {
    List<UserHasRole> findByUserId(int userId);
    List<UserHasRole> findByRoleId(int roleId);

    boolean existsUserHasRoleByUserIdAndRoleId(int userId, int roleId);
}
