package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import teameins.lecturerassignmentsystem.model.db.Role;
import teameins.lecturerassignmentsystem.model.db.User;
import teameins.lecturerassignmentsystem.model.db.relation.UserHasRole;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;
import teameins.lecturerassignmentsystem.model.exception.InvalidRoleException;
import teameins.lecturerassignmentsystem.model.exception.InvalidUserException;
import teameins.lecturerassignmentsystem.model.exception.RoleNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.UserNotFoundException;
import teameins.lecturerassignmentsystem.repository.RoleRepository;
import teameins.lecturerassignmentsystem.repository.UserHasRoleRepository;
import teameins.lecturerassignmentsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserHasRoleRepository userHasRoleRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    AccountService accountService;
    MappingService mappingService;

    @BeforeEach
    void setUp() {
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashedpassword");
        mappingService = new MappingService(passwordEncoder);
        accountService = new AccountService(userRepository, mappingService, userHasRoleRepository, roleRepository);
    }

    @Test
    void listUsersTest() {
        List<Integer> userIds = List.of(1, 2, 3);
        List<User> users = listAllUsers(userIds);

        Mockito.doReturn(users).when(userRepository).findAll();
        users.forEach(user -> Mockito.doReturn(new ArrayList<>())
                .when(userRepository).findRolesForUser(user.getId())
        );

        List<UserDto> userDtos = accountService.listUsers();

        assertEquals(3, userDtos.size());
    }

    @Test
    void listRolesTest() {
        List<Integer> roleIds = List.of(1, 2, 3);
        List<Role> roles = listAllRoles(roleIds);

        Mockito.doReturn(roles).when(roleRepository).findAll();
        roles.forEach(role -> Mockito.doReturn(new ArrayList<>())
                .when(roleRepository).findUsersWithRole(role.getId())
        );

        List<RoleDto> roleDtos = accountService.listRoles();

        assertEquals(3, roleDtos.size());
    }

    @Test
    void getUserByIdTest() {
        int userId = 1;
        User user = getUserById(userId);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(new ArrayList<>()).when(userRepository).findRolesForUser(userId);

        UserDto result = accountService.getUserById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserByIdTestError() {
        int userId = 100;
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(userId);

        assertThrows(UserNotFoundException.class, () -> accountService.getUserById(userId));
    }

    @Test
    void getRoleByIdTest() {
        int roleId = 1;
        Role role = getRoleById(roleId);

        Mockito.doReturn(Optional.of(role)).when(roleRepository).findById(roleId);
        Mockito.doReturn(new ArrayList<>()).when(roleRepository).findUsersWithRole(roleId);

        RoleDto result = accountService.getRoleById(roleId);
        assertNotNull(result);
        assertEquals(roleId, result.getId());
    }

    @Test
    void getRoleByIdTestError() {
        int roleId = 100;
        Mockito.doReturn(Optional.empty()).when(roleRepository).findById(roleId);

        assertThrows(RoleNotFoundException.class, () -> accountService.getRoleById(roleId));
    }

    @Test
    void createUserTest() {
        int userId = 1;

        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEnabled(true);
        userDto.setRoles(new ArrayList<>());

        User user = getUserById(userId);
        Mockito.doReturn(user).when(userRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(new ArrayList<>()).when(userRepository).findRolesForUser(userId);

        UserDto result = accountService.createUser(userDto);
        assertNotNull(result);
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void createUserTestInvalid() {
        UserDto invalid = new UserDto();
        invalid.setId(1);
        invalid.setUsername("");
        invalid.setPassword("password");
        invalid.setEnabled(true);
        invalid.setRoles(new ArrayList<>());

        assertThrows(InvalidUserException.class, () -> accountService.createUser(invalid));
    }

    @Test
    void createUserTestPasswordInvalid() {
        UserDto invalid = new UserDto();
        invalid.setId(1);
        invalid.setUsername("testuser");
        invalid.setPassword("");
        invalid.setEnabled(true);
        invalid.setRoles(new ArrayList<>());

        assertThrows(InvalidUserException.class, () -> accountService.createUser(invalid));
    }

    @Test
    void updateUserTest() {
        int userId = 1;

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEnabled(true);
        userDto.setRoles(new ArrayList<>());

        User user = getUserById(userId);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(user).when(userRepository).save(Mockito.any());
        Mockito.doReturn(new ArrayList<>()).when(userRepository).findRolesForUser(userId);

        UserDto result = accountService.updateUser(userDto);
        assertNotNull(result);
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void updateUserTestNotFound() {
        int userId = 100;
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(userId);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEnabled(true);
        userDto.setRoles(new ArrayList<>());

        assertThrows(UserNotFoundException.class, () -> accountService.updateUser(userDto));
    }

    @Test
    void updateUserTestInvalid() {
        UserDto invalid = new UserDto();
        invalid.setId(1);
        invalid.setUsername("testuser");
        invalid.setPassword("");
        invalid.setEnabled(true);
        invalid.setRoles(new ArrayList<>());

        assertThrows(InvalidUserException.class, () -> accountService.updateUser(invalid));
    }

    @Test
    void deleteUserTest() {
        int userId = 1;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEnabled(true);
        userDto.setRoles(new ArrayList<>());

        assertDoesNotThrow(() -> accountService.deleteUser(userDto));
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserWithRolesTest() {
        int userId = 1;
        int roleId = 1;
        UserHasRoleDto roleDto = new UserHasRoleDto(1, userId, roleId);
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEnabled(true);
        userDto.setRoles(List.of(roleDto));

        assertDoesNotThrow(() -> accountService.deleteUser(userDto));
        Mockito.verify(userHasRoleRepository).deleteById(roleDto.getId());
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    void createRoleTest() {
        int roleId = 1;
        Role role = getRoleById(roleId);

        Mockito.doReturn(role).when(roleRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(role)).when(roleRepository).findById(roleId);
        Mockito.doReturn(new ArrayList<>()).when(roleRepository).findUsersWithRole(roleId);

        RoleDto roleDto = new RoleDto();
        roleDto.setName("ROLE_TEST1");
        roleDto.setGrantedAuthority("ROLE_TEST_1");
        roleDto.setUsersWithRole(new ArrayList<>());

        RoleDto result = accountService.createRole(roleDto);
        assertNotNull(result);
        Mockito.verify(roleRepository).save(Mockito.any());
    }

    @Test
    void createRoleTestInvalid() {
        RoleDto invalid = new RoleDto();
        invalid.setId(1);
        invalid.setName("");
        invalid.setGrantedAuthority("ROLE_TEST");
        invalid.setUsersWithRole(new ArrayList<>());

        assertThrows(InvalidRoleException.class, () -> accountService.createRole(invalid));
    }

    @Test
    void deleteRoleTest() {
        int roleId = 1;
        RoleDto roleDto = new RoleDto();
        roleDto.setId(roleId);
        roleDto.setName("ADMIN");
        roleDto.setGrantedAuthority("ROLE_ADMIN");
        roleDto.setUsersWithRole(new ArrayList<>());

        assertDoesNotThrow(() -> accountService.deleteRole(roleDto));
        Mockito.verify(roleRepository).deleteById(roleId);
    }

    @Test
    void deleteRoleWithUsersTest() {
        int roleId = 1;
        int userId = 1;
        UserHasRoleDto userDto = new UserHasRoleDto(1, userId, roleId);
        RoleDto roleDto = new RoleDto();
        roleDto.setId(roleId);
        roleDto.setName("ADMIN");
        roleDto.setGrantedAuthority("ROLE_ADMIN");
        roleDto.setUsersWithRole(List.of(userDto));

        assertDoesNotThrow(() -> accountService.deleteRole(roleDto));
    }

    @Test
    void assignRoleToUserTest() {
        int userId = 1;
        int roleId = 1;

        User user = getUserById(userId);
        Role role = getRoleById(roleId);
        UserHasRole userHasRole = new UserHasRole(user, role);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(Optional.of(role)).when(roleRepository).findById(roleId);
        Mockito.doReturn(false).when(userHasRoleRepository).existsUserHasRoleByUserIdAndRoleId(userId, roleId);
        Mockito.doReturn(userHasRole).when(userHasRoleRepository).save(Mockito.any());

        UserHasRoleDto result = accountService.assignRoleToUser(userId, roleId);
        assertNotNull(result);
        Mockito.verify(userHasRoleRepository).save(Mockito.any());
    }

    @Test
    void assignRoleToUserTestUserNotFound() {
        int userId = 100;
        int roleId = 1;

        Mockito.doReturn(Optional.empty()).when(userRepository).findById(userId);

        assertThrows(UserNotFoundException.class, () -> accountService.assignRoleToUser(userId, roleId));
    }

    @Test
    void assignRoleToUserTestRoleNotFound() {
        int userId = 1;
        int roleId = 100;
        User user = getUserById(userId);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(Optional.empty()).when(roleRepository).findById(roleId);

        assertThrows(RoleNotFoundException.class, () -> accountService.assignRoleToUser(userId, roleId));
    }

    @Test
    void assignRoleToUserTestAlreadyAssigned() {
        int userId = 1;
        int roleId = 1;
        User user = getUserById(userId);
        Role role = getRoleById(roleId);

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(userId);
        Mockito.doReturn(Optional.of(role)).when(roleRepository).findById(roleId);
        Mockito.doReturn(true).when(userHasRoleRepository).existsUserHasRoleByUserIdAndRoleId(userId, roleId);

        assertThrows(InvalidUserException.class, () -> accountService.assignRoleToUser(userId, roleId));
    }

    @Test
    void getRolesForUserTest() {
        int userId = 1;
        Mockito.doReturn(new ArrayList<>()).when(userRepository).findRolesForUser(userId);

        List<UserHasRoleDto> roles = accountService.getRolesForUser(userId);

        assertEquals(0, roles.size());
    }

    @Test
    void getUsersWithRoleTest() {
        int roleId = 1;
        Mockito.doReturn(new ArrayList<>()).when(roleRepository).findUsersWithRole(roleId);

        List<UserHasRoleDto> users = accountService.getUsersWithRole(roleId);

        assertEquals(0, users.size());
    }

    private User getUserById(int id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testuser" + id);
        user.setPasswordHash("hashedpassword");
        user.setEnabled(true);
        return user;
    }

    private List<User> listAllUsers(List<Integer> ids) {
        return ids.stream().map(this::getUserById).toList();
    }

    private Role getRoleById(int id) {
        Role role = new Role();
        role.setId(id);
        role.setName("ROLE_TEST" + id);
        role.setGrantedAuthority("ROLE_TEST_" + id);
        return role;
    }

    private List<Role> listAllRoles(List<Integer> ids) {
        return ids.stream().map(this::getRoleById).toList();
    }
}

