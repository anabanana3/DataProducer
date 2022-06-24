package es.uv.adiez.services;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.uv.adiez.domain.Status;
import es.uv.adiez.domain.User;
import es.uv.adiez.domain.UserType;

@Service
public class UserService {
	
	@Value("${enpoint.usersAPI}")
	private String usersURL;
	@Value("${enpoint.filesAPI}")
	private String filesURL;
	private Gson gson = new Gson();
	
	
	public User getUserByEmail(String email) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/userAPI/email/"+email;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   return gson.fromJson(result.getBody(), User.class);
	   }
	   return null;
	}
	
	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(getUserByEmail(email));//this.us.findByEmail(username);
	}
	
	public Optional<User> findByEmailAndActive(String email) {
		return Optional.ofNullable(getUserByEmailAndActive(email));//this.us.findByEmail(username);
	}
	
	public User getUserByNif(String nif) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/userAPI/"+nif;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   return gson.fromJson(result.getBody(), User.class);
	   }
	   return null;
	}
	public Optional<User> findById(String id){
		Optional<User> user = Optional.ofNullable(getUserByNif(id));
		if(user.isEmpty()) return Optional.empty();
		user.get().setPassword(null);
		return user;
	}
	
	public List<User> findAll() {
		List<User> users = (List<User>) getAllUsers();
		return users.stream().map(u -> {u.setPassword(null); return u;}).collect(Collectors.toList());
	}
	
	public User create(User user) {
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		user.setStatus(Status.P);
		user.setUserType(UserType.P);
		return createUser(user);
		//return this.us.save(user);
	}
	
	public User update(String id, User user) {
		Optional<User> u = Optional.ofNullable(getUserByNif(id));
		if(u.isEmpty()) return create(user);
		u.get().setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		u.get().setEmail(user.getEmail());
		u.get().setName(user.getName());
		u.get().setPersonType(user.getPersonType());
		u.get().setQuantity(user.getQuantity());
		return createUser(u.get());
	}
	
	public User createUser(User user) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/userAPI";
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new User();
		}
	     
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, user, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 201) {
		   return gson.fromJson(result.getBody(), User.class);
	   }
	   return new User();
	}
	
	public User getUserByEmailAndActive(String email) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/userAPI/emailActive/"+email;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   return gson.fromJson(result.getBody(), User.class);
	   }
	   return null;
	}
	
	public List<User> getAllUsers() {
		RestTemplate restTemplate = new RestTemplate();
	    List<User> users = new ArrayList<User>();
	    final String baseUrl = "http://"+usersURL+"/userAPI";
	    URI uri;
	    try{
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return users;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   Type userListType = new TypeToken<ArrayList<User>>(){}.getType();

		   users = (List<User>) gson.fromJson(result.getBody(), userListType);
	   }
	   return users;
	}
	
	public void delete(String id) {
		//this.us.deleteById(id);
	}
	
	public void deleteAll() {
		//this.us.deleteAll();
	}
	
}
