package teameins.lecturerassignmentsystem.model.dto;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void userDtoCreationTest() {
        UserDto user = new UserDto(1, "testuser", "password", true, new ArrayList<>());

        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertTrue(user.isEnabled());
    }

    @Test
    void userDtoDefaultConstructorTest() {
        UserDto user = new UserDto();

        assertNotNull(user);
    }

    @Test
    void userDtoValidateTest() {
        UserDto validUser = new UserDto();
        validUser.setUsername("testuser");
        validUser.setPassword("password");

        assertTrue(validUser.validate());
    }

    @Test
    void userDtoValidateUsernameEmptyTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("");
        invalidUser.setPassword("password");

        assertFalse(invalidUser.validate());
    }

    @Test
    void userDtoValidatePasswordEmptyTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("testuser");
        invalidUser.setPassword("");

        assertFalse(invalidUser.validate());
    }

    @Test
    void userDtoSettersTest() {
        UserDto user = new UserDto();
        user.setId(2);
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setEnabled(false);
        user.setRoles(new ArrayList<>());

        assertEquals(2, user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertFalse(user.isEnabled());
    }

    @Test
    void userDtoSetRolesTest() {
        UserDto user = new UserDto();
        List<UserHasRoleDto> roles = new ArrayList<>();
        roles.add(new UserHasRoleDto(1, 1, 1));

        user.setRoles(roles);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    void userDtoEnableDisableTest() {
        UserDto user = new UserDto();
        user.setEnabled(true);

        assertTrue(user.isEnabled());

        user.setEnabled(false);

        assertFalse(user.isEnabled());
    }

    @Test
    void userDtoEqualsTest() {
        UserDto user1 = new UserDto(1, "testuser", "password", true, new ArrayList<>());
        UserDto user2 = new UserDto(1, "testuser", "password", true, new ArrayList<>());

        assertEquals(user1, user2);
    }

    @Test
    void userDtoNotEqualsTest() {
        UserDto user1 = new UserDto(1, "testuser", "password", true, new ArrayList<>());
        UserDto user2 = new UserDto(2, "otheruser", "password", true, new ArrayList<>());

        assertNotEquals(user1, user2);
    }

    @Test
    void userDtoHashCodeTest() {
        UserDto user1 = new UserDto(1, "testuser", "password", true, new ArrayList<>());
        UserDto user2 = new UserDto(1, "testuser", "password", true, new ArrayList<>());

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void userDtoValidateUsernameBlankTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("   ");
        invalidUser.setPassword("password");

        assertFalse(invalidUser.validate());
    }

    @Test
    void userDtoValidatePasswordBlankTest() {
        UserDto invalidUser = new UserDto();
        invalidUser.setUsername("testuser");
        invalidUser.setPassword("   ");

        assertFalse(invalidUser.validate());
    }
}

