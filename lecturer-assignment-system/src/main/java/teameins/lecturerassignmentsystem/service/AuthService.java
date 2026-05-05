package teameins.lecturerassignmentsystem.service;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.User;
import teameins.lecturerassignmentsystem.model.exception.InvalidUserException;
import teameins.lecturerassignmentsystem.repository.UserRepository;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;

	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public @NonNull UserDetails loadUserByUsername(@NonNull String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new InvalidUserException("Ungültige Zugangsdaten."));

		List<SimpleGrantedAuthority> roles = userRepository.findRolesForUser(user.getId())
				.stream()
				.map(uhr -> new SimpleGrantedAuthority(uhr.getRole().getGrantedAuthority()))
				.toList();

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPasswordHash(),
				roles
		);
	}
}
