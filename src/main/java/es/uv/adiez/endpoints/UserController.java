package es.uv.adiez.endpoints;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.uv.adiez.domain.AuthenticatedUser;
import es.uv.adiez.domain.User;
import es.uv.adiez.endpoints.UserController;
import es.uv.adiez.services.UserService;

@RestController
public class UserController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired UserService us;
	
	@GetMapping("authenticated")
	public ResponseEntity<AuthenticatedUser> getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication!= null) {
			Object userDetails = authentication.getPrincipal();
			if(userDetails != null && userDetails instanceof UserDetails)
			{
				UserDetails secUser = (UserDetails) userDetails;
				String username = secUser.getUsername();
				String psswd = secUser.getPassword();
				
				List<String> authorities = secUser.getAuthorities()
											.stream()
												.map(authority -> authority.getAuthority())
												.collect(Collectors.toList());
				String[] roles = new String[authorities.size()];
				authorities.toArray(roles);
				AuthenticatedUser authenticatedUser = new AuthenticatedUser(username, psswd, roles);
				return new ResponseEntity<>(authenticatedUser,HttpStatus.OK); 
			}
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/api/whoami")
	public String getAuthenticatedUser(Principal principal) {
		LOGGER.debug("Whoami");
		return principal.getName();
	}
	
	@GetMapping("/api/users/{id}")
	public Optional<User> getUser(@PathVariable("id") String id) {
		LOGGER.debug("View user "+id);
		return us.findById(id);
	}
	
	@GetMapping("/api/users")
	public List<User> getUsers() {
		LOGGER.debug("View all users");
		return us.findAll();
	}
	//Sin auteticacion
	@PostMapping("/api/users")
	public ResponseEntity<User> create(@RequestBody User user) {
		LOGGER.debug("Create user", user);
		User u = us.create(user);
		return new ResponseEntity<>(u, HttpStatus.OK);
	}
	//Con autenticacion y estado activo
	//@PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/api/users/{nif}")
	public ResponseEntity<User>  updateUser(@PathVariable("nif") String nif, @RequestBody @Valid User user) {
		User u = us.update(nif, user);
		return new ResponseEntity<>(u, HttpStatus.OK);
	}
	
	@DeleteMapping("/api/users/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		LOGGER.debug("Delete user id: "+id);
		this.us.delete(id);
		return new ResponseEntity<>(id, HttpStatus.OK);
	}
}