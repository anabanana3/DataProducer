package es.uv.adiez.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;
	
	//He intentado hacerlo configurable, pero no me funciona, por eso lo he dejado así
	@Value("${sys.token.key}")
	private String key ="MySuperSecureEncriptedAndProtectedKey";
	
	@Value("${sys.token.issuer}")
	private String issuer;
	
	@Value("${sys.token.duration}")
	private Integer duration=3600000;
	
	@Autowired TokenProvider tp;
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) { 
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		System.out.println("--------------->"+key);
	
		String body  = "";
		JsonObject json = null;
		try {
			if(request.getMethod().equalsIgnoreCase("POST")) {
				body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
				json = new Gson().fromJson(body, JsonObject.class);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String user = json != null ? json.get("username").getAsString() : request.getParameter("username");
		String pswd = json != null ? json.get("password").getAsString() : request.getParameter("password");
		System.out.println("--------------->"+user);
		System.out.println("--------------->"+pswd);
		UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(user, 
																								pswd);
		return this.authenticationManager.authenticate(authtoken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		User user = (User)auth.getPrincipal();
		List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		//No me va :(
		//String access_token = tp.generateAccessToken(user.getUsername(), roles);
		//String refresh_token = tp.generateRefreshToken(user.getUsername(), roles);
		Algorithm alg = Algorithm.HMAC256(key.getBytes());
		String access_token = JWT.create()
				 .withSubject(user.getUsername())
				 .withExpiresAt(new Date(System.currentTimeMillis()+this.duration))
				 .withIssuer(request.getRequestURL().toString())
				 .withClaim("roles", roles)
				 .sign(alg);

		String refresh_token = JWT.create()
			.withSubject(user.getUsername())
			.withExpiresAt(new Date(System.currentTimeMillis()+(this.duration*2)))
			.withIssuer(request.getRequestURL().toString())
			.withClaim("roles", roles)
			.sign(alg);
		
		response.setHeader("access_token", access_token);
		response.setHeader("refresh_token", refresh_token);
		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
	}
}
