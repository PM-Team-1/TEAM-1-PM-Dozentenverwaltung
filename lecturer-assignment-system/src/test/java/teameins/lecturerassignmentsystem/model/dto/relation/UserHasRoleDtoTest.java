package teameins.lecturerassignmentsystem.model.dto.relation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserHasRoleDtoTest {

    @Test
    void userHasRoleDtoCreationTest() {
        UserHasRoleDto userHasRole = new UserHasRoleDto(1, 1, 1);

        assertEquals(1, userHasRole.getId());
        assertEquals(1, userHasRole.getUserId());
        assertEquals(1, userHasRole.getRoleId());
    }

    @Test
    void userHasRoleDtoDefaultConstructorTest() {
        UserHasRoleDto userHasRole = new UserHasRoleDto();

        assertNotNull(userHasRole);
    }

    @Test
    void userHasRoleDtoSettersTest() {
        UserHasRoleDto userHasRole = new UserHasRoleDto();
        userHasRole.setId(1);
        userHasRole.setUserId(1);
        userHasRole.setRoleId(1);

        assertEquals(1, userHasRole.getId());
        assertEquals(1, userHasRole.getUserId());
        assertEquals(1, userHasRole.getRoleId());
    }

    @Test
    void userHasRoleDtoEqualsTest() {
        UserHasRoleDto dto1 = new UserHasRoleDto(1, 1, 1);
        UserHasRoleDto dto2 = new UserHasRoleDto(1, 1, 1);

        assertEquals(dto1, dto2);
    }

    @Test
    void userHasRoleDtoNotEqualsTest() {
        UserHasRoleDto dto1 = new UserHasRoleDto(1, 1, 1);
        UserHasRoleDto dto2 = new UserHasRoleDto(2, 2, 2);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void userHasRoleDtoHashCodeTest() {
        UserHasRoleDto dto1 = new UserHasRoleDto(1, 1, 1);
        UserHasRoleDto dto2 = new UserHasRoleDto(1, 1, 1);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void userHasRoleDtoChangeUserIdTest() {
        UserHasRoleDto userHasRole = new UserHasRoleDto(1, 1, 1);

        assertEquals(1, userHasRole.getUserId());

        userHasRole.setUserId(2);

        assertEquals(2, userHasRole.getUserId());
    }

    @Test
    void userHasRoleDtoChangeRoleIdTest() {
        UserHasRoleDto userHasRole = new UserHasRoleDto(1, 1, 1);

        assertEquals(1, userHasRole.getRoleId());

        userHasRole.setRoleId(2);

        assertEquals(2, userHasRole.getRoleId());
    }

    @Test
    void userHasRoleDtoMultipleAssignmentsTest() {
        UserHasRoleDto assignment1 = new UserHasRoleDto(1, 1, 1);
        UserHasRoleDto assignment2 = new UserHasRoleDto(2, 1, 2);

        assertEquals(1, assignment1.getUserId());
        assertEquals(1, assignment2.getUserId());
        assertNotEquals(assignment1.getRoleId(), assignment2.getRoleId());
    }

    @Test
    void userHasRoleDtoSameUserMultipleRolesTest() {
        UserHasRoleDto adminRole = new UserHasRoleDto(1, 1, 1);
        UserHasRoleDto userRole = new UserHasRoleDto(2, 1, 2);
        UserHasRoleDto lecturerRole = new UserHasRoleDto(3, 1, 3);

        assertEquals(1, adminRole.getUserId());
        assertEquals(1, userRole.getUserId());
        assertEquals(1, lecturerRole.getUserId());

        assertNotEquals(adminRole.getId(), userRole.getId());
        assertNotEquals(adminRole.getId(), lecturerRole.getId());
        assertNotEquals(userRole.getId(), lecturerRole.getId());
    }
}

