package es.uv.adiez.security;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@ComponentScan
public class CustomAuthorizationFilter extends OncePerRequestFilter {
	//He intentado hacerlo configurable, pero no me funciona, por eso lo he dejado así
	@Value("${sys.token.key}")
	private String key ="MySuperSecureEncriptedAndProtectedKey";

	//private Environment env;	 

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		System.out.println("keeeey ------>"+this.key);

		if(request.getServletPath().equals("/authenticate") || request.getServletPath().equals("/authenticate/refresh")) {
			filterChain.doFilter(request, response);
		}
		else {
			String authHeader = request.getHeader(AUTHORIZATION);
			if(authHeader != null && authHeader.startsWith("Bearer ")) {
				try {
					String token = authHeader.substring("Bearer ".length());
					Algorithm alg = Algorithm.HMAC256(this.key.getBytes());
					JWTVerifier verifier = JWT.require(alg).build();
					DecodedJWT decoded = verifier.verify(token);
					String username = decoded.getSubject();
					String[] roles = decoded.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					for(String r : roles) { authorities.add(new SimpleGrantedAuthority(r)); }
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);
				}
				catch(Exception exception) {
					response.setHeader("error", exception.getMessage());
					//response.sendError(403);
					response.setStatus(403);
					Map<String, String> error = new HashMap<>();
					error.put("error_msg", exception.getMessage());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			}
			else {
				filterChain.doFilter(request, response);
			}
		}
	}

	
	
}
