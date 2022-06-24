package es.uv.adiez.endpoints;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.uv.adiez.security.CustomUserDetailsService;

@RestController
@RequestMapping("/authenticate/refresh")
public class RefreshController {

	@Value("${sys.token.key}")
	private String key;
	@Value("${sys.token.duration}")
	private Integer duration=3600000;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@GetMapping()
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws StreamWriteException, DatabindException, IOException {
		String authHeader = request.getHeader(AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			try {
				String refresh_token = authHeader.substring("Bearer ".length());
				Algorithm alg = Algorithm.HMAC256(key.getBytes());
				JWTVerifier verifier = JWT.require(alg).build();
				DecodedJWT decoded = verifier.verify(refresh_token);
				String username = decoded.getSubject();
				UserDetails user = this.customUserDetailsService.loadUserByUsername(username);

				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + this.duration))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles",
								user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
										.collect(Collectors.toList()))
						.sign(alg);

				response.setHeader("access_token", access_token);
				response.setHeader("refresh_token", refresh_token);
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception exception) {
				response.setHeader("error", exception.getMessage());
				// response.sendError(403);
				response.setStatus(403);
				Map<String, String> error = new HashMap<>();
				error.put("error_msg", exception.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("missing refresh token");
		}
	}
}
