package org.thirty.app.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thirty.app.model.Authority;
import org.thirty.app.model.BlogUser;
import org.thirty.app.repository.AuthorityRepository;
import org.thirty.app.repository.BlogUserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BlogUserServiceImpl implements BlogUserService {

	private static final String DEFAULT_ROLE = "ROLE_USER";
	private final BCryptPasswordEncoder bcryptEncoder;
	private final BlogUserRepository blogUserRepository;
	private final AuthorityRepository authorityRepository;

	public BlogUserServiceImpl(BCryptPasswordEncoder bcryptEncoder, BlogUserRepository blogUserRepository,
			AuthorityRepository authorityRepository) {
		this.bcryptEncoder = bcryptEncoder;
		this.blogUserRepository = blogUserRepository;
		this.authorityRepository = authorityRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<BlogUser> blogUser = blogUserRepository.findByUsername(username);
		if (blogUser.isPresent()) {
			return blogUser.get();
		} else {
			throw new UsernameNotFoundException("No se ha encontrado username " + username);
		}
	}

	@Override
	public Optional<BlogUser> findByUsername(String username) {
		return blogUserRepository.findByUsername(username);
	}

	@Override
	public BlogUser saveNewBlogUser(BlogUser blogUser) {
		blogUser.setPassword(this.bcryptEncoder.encode(blogUser.getPassword()));
		blogUser.setEnabled(true);
		Optional<Authority> optionalAuthority = this.authorityRepository.findByAuthority(DEFAULT_ROLE);
		if (optionalAuthority.isPresent()) {
			Authority authority = optionalAuthority.get();
			Collection<Authority> authorities = Collections.singletonList(authority);
			blogUser.setAuthorities(authorities);
			return this.blogUserRepository.saveAndFlush(blogUser);
		} else {
			throw new RuntimeException(
					"Default role no encontrado en el blog con el username " + blogUser.getUsername());
		}
	}

	@Override
	public void deleteFriend(BlogUser currentUser, BlogUser friend) {
		currentUser.getFriends().remove(friend);
		blogUserRepository.save(currentUser);

		friend.getFriends().remove(currentUser);
		blogUserRepository.save(friend);
	}

	@Override
	public BlogUser findUserById(Long userId) {
		Optional<BlogUser> userOptional = blogUserRepository.findById(userId);
		return userOptional.orElse(null);
	}

	@Override
	public List<BlogUser> findFriends(BlogUser currentUser) {
		return currentUser.getFriends();
	}

	@Override
	public boolean areFriends(BlogUser sender, BlogUser receiver) {
		if (sender == null || receiver == null) {
			return false;
		}
		List<BlogUser> senderFriends = findFriends(sender);
		List<BlogUser> receiverFriends = findFriends(receiver);

		return senderFriends.contains(receiver) && receiverFriends.contains(sender);
	}

	public BlogUser findById(Long id) {
		Optional<BlogUser> optionalBlogUser = blogUserRepository.findById(id);
		if (optionalBlogUser.isPresent()) {
			return optionalBlogUser.get();
		} else {
			throw new NoSuchElementException("No se encontrÃ³ un BlogUser con el ID: " + id);
		}
	}

	@Override
	public List<BlogUser> searchByUsername(String username) {
		return blogUserRepository.findByUsernameStartingWith(username);
	}

	@Override
	public Optional<BlogUser> findByEmail(String email) {
		return Optional.empty();
	}
}