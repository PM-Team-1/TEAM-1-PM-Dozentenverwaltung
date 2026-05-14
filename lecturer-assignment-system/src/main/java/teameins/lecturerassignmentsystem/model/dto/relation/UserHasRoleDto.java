package teameins.lecturerassignmentsystem.model.dto.relation;

import java.util.Objects;

public class UserHasRoleDto {
    private int id;
    private int userId;
    private int roleId;

    public UserHasRoleDto() {
    }

    public UserHasRoleDto(int id, int userId, int roleId) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, roleId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserHasRoleDto other)) {
            return false;
        }
        return this.id == other.id && this.userId == other.userId && this.roleId == other.roleId;
    }
}

