package teameins.lecturerassignmentsystem.model.dto;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleDtoTest {

    @Test
    void roleDtoCreationTest() {
        RoleDto role = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());

        assertEquals(1, role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("ROLE_ADMIN", role.getGrantedAuthority());
    }

    @Test
    void roleDtoDefaultConstructorTest() {
        RoleDto role = new RoleDto();

        assertNotNull(role);
    }

    @Test
    void roleDtoValidateTest() {
        RoleDto validRole = new RoleDto();
        validRole.setName("ADMIN");

        assertTrue(validRole.validate());
    }

    @Test
    void roleDtoValidateNameEmptyTest() {
        RoleDto invalidRole = new RoleDto();
        invalidRole.setName("");

        assertFalse(invalidRole.validate());
    }

    @Test
    void roleDtoValidateNameBlankTest() {
        RoleDto invalidRole = new RoleDto();
        invalidRole.setName("   ");

        assertFalse(invalidRole.validate());
    }

    @Test
    void roleDtoSettersTest() {
        RoleDto role = new RoleDto();
        role.setId(2);
        role.setName("USER");
        role.setGrantedAuthority("ROLE_USER");
        role.setUsersWithRole(new ArrayList<>());

        assertEquals(2, role.getId());
        assertEquals("USER", role.getName());
        assertEquals("ROLE_USER", role.getGrantedAuthority());
    }

    @Test
    void roleDtoSetUsersWithRoleTest() {
        RoleDto role = new RoleDto();
        List<UserHasRoleDto> users = new ArrayList<>();
        users.add(new UserHasRoleDto(1, 1, 1));

        role.setUsersWithRole(users);

        assertEquals(1, role.getUsersWithRole().size());
    }

    @Test
    void roleDtoEqualsTest() {
        RoleDto role1 = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());
        RoleDto role2 = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());

        assertEquals(role1, role2);
    }

    @Test
    void roleDtoNotEqualsTest() {
        RoleDto role1 = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());
        RoleDto role2 = new RoleDto(2, "USER", "ROLE_USER", new ArrayList<>());

        assertNotEquals(role1, role2);
    }

    @Test
    void roleDtoHashCodeTest() {
        RoleDto role1 = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());
        RoleDto role2 = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());

        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void roleDtoNameChangeTest() {
        RoleDto role = new RoleDto();
        role.setName("ADMIN");

        assertEquals("ADMIN", role.getName());

        role.setName("SUPERADMIN");

        assertEquals("SUPERADMIN", role.getName());
    }

    @Test
    void roleDtoGrantedAuthorityChangeTest() {
        RoleDto role = new RoleDto();
        role.setGrantedAuthority("ROLE_ADMIN");

        assertEquals("ROLE_ADMIN", role.getGrantedAuthority());

        role.setGrantedAuthority("ROLE_SUPERADMIN");

        assertEquals("ROLE_SUPERADMIN", role.getGrantedAuthority());
    }

    @Test
    void multipleRolesDtoCreationTest() {
        RoleDto adminRole = new RoleDto(1, "ADMIN", "ROLE_ADMIN", new ArrayList<>());
        RoleDto userRole = new RoleDto(2, "USER", "ROLE_USER", new ArrayList<>());
        RoleDto lecturerRole = new RoleDto(3, "LECTURER", "ROLE_LECTURER", new ArrayList<>());

        assertEquals("ADMIN", adminRole.getName());
        assertEquals("USER", userRole.getName());
        assertEquals("LECTURER", lecturerRole.getName());

        assertNotEquals(adminRole.getId(), userRole.getId());
        assertNotEquals(adminRole.getId(), lecturerRole.getId());
        assertNotEquals(userRole.getId(), lecturerRole.getId());
    }
}

