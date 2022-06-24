package es.uv.adiez.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import es.uv.adiez.security.CustomAuthenticationFilter;
import es.uv.adiez.security.CustomAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    private UserDetailsService customUserDetailsService;
    
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	@Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {   
    	
    	// customize login endpoint by overriding the AuthenticationFilter processor url
    	CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
    	customAuthenticationFilter.setFilterProcessesUrl("/authenticate");
    	
    	http.csrf().disable()
    		.sessionManagement().sessionCreationPolicy(STATELESS)
    		.and()
    		.authorizeRequests()
    		//.antMatchers("/api/**").permitAll()
    		.antMatchers("/authenticate", "/authenticate/refresh").permitAll()
    		.antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
    		.antMatchers(HttpMethod.POST, "/api/users").permitAll()//hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
    		.antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
    		.antMatchers(HttpMethod.GET, "/api/files/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
    		.antMatchers(HttpMethod.POST, "/api/files").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
    		.antMatchers(HttpMethod.PUT, "/api/files/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
    		.antMatchers(HttpMethod.DELETE, "/api/files/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
    		.and()
    		.addFilter(customAuthenticationFilter)
    		.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    	
    }
}
