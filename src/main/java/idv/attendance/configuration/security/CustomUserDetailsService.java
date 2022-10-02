package idv.attendance.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginName(username)
                .orElseThrow(() -> new UsernameNotFoundException("username is not found for: " + username));

        return new CustomUserDetails(
                user.getCompanyId(),
                user.getUserId(),
                user.getName(),
                user.getPassword(),
                user.getRole().toGrantedAuthorities());
    }
}
