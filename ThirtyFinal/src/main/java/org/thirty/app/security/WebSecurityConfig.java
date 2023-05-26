package org.thirty.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final DataSource dataSource;
	private static final String USERS_SQL_QUERY = "select username,password,enabled from users where username = ?";
	private static final String AUTHORITIES_SQL_QUERY = "select users.username, authorities.authority\n"
			+ "from users\n" + "inner join users_authorities on (users.id = users_authorities.user_id)\n"
			+ "inner join authorities on (users_authorities.authority_id = authorities.id)\n"
			+ "where users.username = ?;";

	public WebSecurityConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// Encriptador
	@Bean
	public BCryptPasswordEncoder bcryptEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Reparto de Permisos por Roles.
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().headers().frameOptions().disable().and().authorizeRequests()
				.antMatchers("/createNewPost/**", "/editPost/**", "/comment/**").hasRole("USER")
				.antMatchers("/deletePost/**").hasRole("USER").antMatchers("/**").permitAll().anyRequest()
				.authenticated().and().formLogin().loginPage("/login").loginProcessingUrl("/login")
				.usernameParameter("username").passwordParameter("password")
				.successHandler(authenticationSuccessHandler()).failureUrl("/login?error").permitAll().and()
				.rememberMe().rememberMeParameter("remember-me").and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout").permitAll().and().sessionManagement().maximumSessions(1);
	}

	// Define el AuthenticationSuccessHandler para redirigir a /home
	private AuthenticationSuccessHandler authenticationSuccessHandler() {
		SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
		successHandler.setDefaultTargetUrl("/home");
		return successHandler;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

		authenticationManagerBuilder.jdbcAuthentication().usersByUsernameQuery(USERS_SQL_QUERY)
				.authoritiesByUsernameQuery(AUTHORITIES_SQL_QUERY).dataSource(dataSource)
				.passwordEncoder(bcryptEncoder());
	}
}
