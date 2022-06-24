package es.uv.adiez.security;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import es.uv.adiez.domain.User;
import es.uv.adiez.services.UserService;


@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired 
	UserService us;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = us.findByEmailAndActive(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return  new org.springframework.security.core.userdetails.User(user.getEmail(), 
														              user.getPassword(),
														              getAuthorities(user));
    }

	private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String[] userRoles = user.getRoles();
        
        System.out.println(userRoles);
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }
}