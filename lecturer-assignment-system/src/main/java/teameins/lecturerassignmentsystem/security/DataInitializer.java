package teameins.lecturerassignmentsystem.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import teameins.lecturerassignmentsystem.model.db.Role;
import teameins.lecturerassignmentsystem.model.db.User;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.repository.RoleRepository;
import teameins.lecturerassignmentsystem.repository.UserHasRoleRepository;
import teameins.lecturerassignmentsystem.repository.UserRepository;
import teameins.lecturerassignmentsystem.service.AccountService;
import teameins.lecturerassignmentsystem.service.MappingService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final AccountService accountService;
    private final MappingService mappingService;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserHasRoleRepository userHasRoleRepository, AccountService accountService, MappingService mappingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userHasRoleRepository = userHasRoleRepository;
        this.accountService = accountService;
        this.mappingService = mappingService;
    }

    @Override
    public void run(String... args) {

        // Rolle erstellen, falls nicht vorhanden
        Role adminRole = roleRepository.findByName("Admin")
                .orElseGet(() -> {
                    RoleDto adminRoleDto = new RoleDto(0, "Admin", "ROLE_ADMIN", null);
                    return mappingService.map(accountService.createRole(adminRoleDto));
                });

        // Admin User erstellen, falls nicht vorhanden
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    UserDto adminDto = new UserDto(0, "admin","admin", true, null);
                    return mappingService.map(accountService.createUser(adminDto));
                });

        if (!userHasRoleRepository.existsUserHasRoleByUserIdAndRoleId(adminUser.getId(), adminRole.getId())) {
            accountService.assignRoleToUser(adminUser.getId(), adminRole.getId());
        }
    }
}
