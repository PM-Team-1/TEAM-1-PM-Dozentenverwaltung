package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;

import java.util.List;
import java.util.Objects;

public class RoleDto {

    private int id;
    private String name;
    private String grantedAuthority;
    private List<UserHasRoleDto> usersWithRole;

    public RoleDto() {

    }

    public RoleDto(int id, String name, String grantedAuthority, List<UserHasRoleDto> usersWithRole) throws IllegalArgumentException {
        setId(id);
        setName(name);
        setGrantedAuthority(grantedAuthority);
        setUsersWithRole(usersWithRole);
    }

    public static boolean validate(RoleDto role) {
        return validateName(role.getName()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            return "Der Name der Rolle darf nicht leer sein.";
        }
        return "";
    }

    public void setName(String name) throws IllegalArgumentException {
        this.name = name;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

    public String getGrantedAuthority(){
        return grantedAuthority;
    }

    public void setGrantedAuthority(String grantedAuthority){
        this.grantedAuthority = grantedAuthority;
    }

    public List<UserHasRoleDto> getUsersWithRole() {
        return usersWithRole;
    }

    public void setUsersWithRole(List<UserHasRoleDto> usersWithRole) {
        this.usersWithRole = usersWithRole;
    }

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RoleDto other)) {
			return false;
		}
        return id == other.id && Objects.equals(name, other.name);
	}

}

