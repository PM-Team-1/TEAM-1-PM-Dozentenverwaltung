package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;

import java.util.List;
import java.util.Objects;

public class UserDto {

    private int id;
    private String username;
    private String password;
    private boolean enabled;
    private List<UserHasRoleDto> roles;
    
    public UserDto() {
    	
    }

    public UserDto(int id, String username, String password, boolean enabled, List<UserHasRoleDto> roles) throws IllegalArgumentException {
        setId(id);
        setUsername(username);
        setPassword(password);
        setEnabled(enabled);
        setRoles(roles);
    }

    public static boolean validate(UserDto user) {
        return validateUsername(user.getUsername()).isEmpty() &&
               validatePasswordHash(user.getPassword()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

    public static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return "Der Benutzername darf nicht leer sein.";
        }
        return "";
    }

    public void setUsername(String username) throws IllegalArgumentException {
        this.username = username;
    }

    public static String validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            return "Das Passwort darf nicht leer sein.";
        }
        return "";
    }

    public void setPassword(String password) throws IllegalArgumentException {
        this.password = password;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<UserHasRoleDto> getRoles() {
		return roles;
	}

	public void setRoles(List<UserHasRoleDto> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, id, password, roles, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof UserDto other)) {
			return false;
		}
        return enabled == other.enabled && id == other.id && Objects.equals(password, other.password)
				&& Objects.equals(roles, other.roles) && Objects.equals(username, other.username);
	}
	
}

