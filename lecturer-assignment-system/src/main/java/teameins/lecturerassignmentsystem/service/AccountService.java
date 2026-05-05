package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
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

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final MappingService mappingService;
    private final UserHasRoleRepository userHasRoleRepository;
    private final RoleRepository roleRepository;

    public AccountService(UserRepository userRepository, MappingService mappingService, UserHasRoleRepository userHasRoleRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.mappingService = mappingService;
        this.userHasRoleRepository = userHasRoleRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserDto> listUsers(){
        List<User> user = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User u : user) {
            List<UserHasRoleDto> hasRoles = getRolesForUser(u.getId());
            userDtos.add(mappingService.map(u, hasRoles));
        }
        return userDtos;
    }

    public List<RoleDto> listRoles(){
        List<Role> roles = roleRepository.findAll();
        List<RoleDto> roleDtos = new ArrayList<>();
        for (Role r : roles) {
            List<UserHasRoleDto> usersWithRole = getUsersWithRole(r.getId());
            roleDtos.add(mappingService.map(r, usersWithRole));
        }
        return roleDtos;
    }

    public UserDto getUserById(int userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Es konnte kein Benutzer mit der ID " + userId + " gefunden werden."));
        List<UserHasRoleDto> hasRoles = getRolesForUser(userId);
        return mappingService.map(user, hasRoles);
    }

    public RoleDto getRoleById(int roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Es konnte keine Rolle mit der ID " + roleId + " gefunden werden."));
        List<UserHasRoleDto> usersWithRole = getUsersWithRole(roleId);
        return mappingService.map(role, usersWithRole);
    }

    public List<UserHasRoleDto> getRolesForUser(int userId) {
        List<UserHasRoleDto> hasRoles = new ArrayList<>();
        userRepository.findRolesForUser(userId).forEach(r -> hasRoles.add(mappingService.map(r)));
        return hasRoles;
    }

    public List<UserHasRoleDto> getUsersWithRole(int roleId) {
        List<UserHasRoleDto> usersWithRole = new ArrayList<>();
        roleRepository.findUsersWithRole(roleId).forEach(u -> usersWithRole.add(mappingService.map(u)));
        return usersWithRole;
    }

    public UserDto createUser(UserDto userDto) {
        if (!userDto.validate()) {
            throw new InvalidUserException("Der Benutzer ist ungültig.");
        }
        User user = mappingService.map(userDto);
        int id = userRepository.save(user).getId();
        return getUserById(id);
    }

    public UserDto updateUser(UserDto userDto) {
        if (!userDto.validate()) {
            throw new InvalidUserException("Der Benutzer ist ungültig.");
        }
        userRepository.findById(userDto.getId()).
                orElseThrow(() -> new UserNotFoundException("Es konnte kein Benutzer mit der ID " + userDto.getId() + " gefunden werden."));
        User user = mappingService.map(userDto);
        userRepository.save(user);
        return getUserById(userDto.getId());
    }

    public void deleteUser(UserDto userDto) {
        for (UserHasRoleDto uhr : userDto.getRoles()) {
            userHasRoleRepository.deleteById(uhr.getId());
        }

        userRepository.deleteById(userDto.getId());
    }

    public RoleDto createRole(RoleDto roleDto) {
        if (!roleDto.validate()) {
            throw new InvalidRoleException("Die Rolle ist ungültig.");
        }
        int id = roleRepository.save(mappingService.map(roleDto)).getId();
        return getRoleById(id);
    }

    public void deleteRole(RoleDto roleDto) {
        for (UserHasRoleDto uhr : roleDto.getUsersWithRole()) {
            userHasRoleRepository.deleteById(uhr.getId());
        }

        roleRepository.deleteById(roleDto.getId());
    }

    public UserHasRoleDto assignRoleToUser(int userId, int roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Benutzer nicht gefunden."));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rolle nicht gefunden."));

        if (userHasRoleRepository.existsUserHasRoleByUserIdAndRoleId(userId, roleId)) {
            throw new InvalidUserException("Dieser Benutzer hat diese Rolle bereits.");
        }

        UserHasRole uhr = new UserHasRole(user, role);
        uhr = userHasRoleRepository.save(uhr);
        return mappingService.map(uhr);
    }
}
