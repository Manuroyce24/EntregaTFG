package org.thirty.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thirty.app.model.BlogUser;
import org.thirty.app.repository.BlogUserRepository;

@Service
public class BlogUserDetailsService implements UserDetailsService {

	@Autowired
	private BlogUserRepository blogUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BlogUser blogUser = blogUserRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("No se encontró al usuario con el nombre de usuario: " + username));

		return blogUser;
	}
}
