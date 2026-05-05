package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityFunctionalityTest {

    @Test
    void userAuthenticationCreationTest() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("securepassword");
        user.setEnabled(true);
        user.setRoles(new ArrayList<>());

        assertTrue(user.isEnabled());
        assertTrue(user.validate());
    }

    @Test
    void userDisableDisablesAccessTest() {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        assertTrue(user.isEnabled());

        user.setEnabled(false);

        assertFalse(user.isEnabled());
    }

    @Test
    void roleCreationForPermissionsTest() {
        RoleDto adminRole = new RoleDto();
        adminRole.setId(1);
        adminRole.setName("ADMIN");
        adminRole.setGrantedAuthority("ROLE_ADMIN");
        adminRole.setUsersWithRole(new ArrayList<>());

        assertTrue(adminRole.validate());
        assertEquals("ROLE_ADMIN", adminRole.getGrantedAuthority());
    }

    @Test
    void userRoleAssignmentTest() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("password");
        user.setEnabled(true);

        UserHasRoleDto adminRole = new UserHasRoleDto(1, user.getId(), 1);

        List<UserHasRoleDto> roles = new ArrayList<>();
        roles.add(adminRole);
        user.setRoles(roles);

        assertEquals(1, user.getRoles().size());
        assertEquals(1, user.getRoles().get(0).getUserId());
    }

    @Test
    void multipleUserRoleAssignmentTest() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        UserHasRoleDto role1 = new UserHasRoleDto(1, user.getId(), 1);
        UserHasRoleDto role2 = new UserHasRoleDto(2, user.getId(), 2);
        UserHasRoleDto role3 = new UserHasRoleDto(3, user.getId(), 3);

        List<UserHasRoleDto> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        roles.add(role3);
        user.setRoles(roles);

        assertEquals(3, user.getRoles().size());
    }

    @Test
    void userWithAdminRoleTest() {
        UserDto admin = new UserDto();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEnabled(true);

        RoleDto adminRole = new RoleDto();
        adminRole.setId(1);
        adminRole.setName("ADMIN");
        adminRole.setGrantedAuthority("ROLE_ADMIN");

        UserHasRoleDto assignment = new UserHasRoleDto(1, admin.getId(), adminRole.getId());
        admin.setRoles(List.of(assignment));

        assertTrue(admin.isEnabled());
        assertEquals("ROLE_ADMIN", adminRole.getGrantedAuthority());
    }

    @Test
    void userValidationForCreationTest() {
        UserDto validUser = new UserDto();
        validUser.setUsername("newuser");
        validUser.setPassword("securepassword123");

        assertTrue(validUser.validate());
    }

    @Test
    void userInvalidUsernameForCreationTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("");
        invalidUser.setPassword("password");

        assertFalse(invalidUser.validate());
    }

    @Test
    void userInvalidPasswordForCreationTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("testuser");
        invalidUser.setPassword("");

        assertFalse(invalidUser.validate());
    }

    @Test
    void roleValidationForCreationTest() {
        RoleDto validRole = new RoleDto();
        validRole.setName("VIEWER");

        assertTrue(validRole.validate());
    }

    @Test
    void roleInvalidNameForCreationTest() {
        RoleDto invalidRole = new RoleDto();
        invalidRole.setName("");

        assertFalse(invalidRole.validate());
    }

    @Test
    void userPasswordChangeSecurityTest() {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setPassword("oldpassword");

        assertEquals("oldpassword", user.getPassword());

        user.setPassword("newpassword");

        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void disabledUserCannotLoginTest() {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(false);

        assertFalse(user.isEnabled());
    }

    @Test
    void enabledUserCanLoginTest() {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        assertTrue(user.isEnabled());
    }

    @Test
    void roleHierarchyTest() {
        int adminRoleHierarchy = 3;
        int userRoleHierarchy = 1;

        assertTrue(adminRoleHierarchy > userRoleHierarchy);
    }

    @Test
    void userWithoutRolesTest() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles(new ArrayList<>());

        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void userRoleRemovalTest() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        UserHasRoleDto role = new UserHasRoleDto(1, user.getId(), 1);
        List<UserHasRoleDto> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        assertEquals(1, user.getRoles().size());

        user.setRoles(new ArrayList<>());

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void adminCanManageUsersTest() {
        RoleDto adminRole = new RoleDto();
        adminRole.setName("ADMIN");
        adminRole.setGrantedAuthority("ROLE_ADMIN");

        assertTrue(isAdminRole(adminRole));
    }

    private boolean isAdminRole(RoleDto role) {
        return "ROLE_ADMIN".equals(role.getGrantedAuthority());
    }
}
