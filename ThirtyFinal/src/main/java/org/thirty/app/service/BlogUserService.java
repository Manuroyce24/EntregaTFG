package org.thirty.app.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.thirty.app.model.BlogUser;

import java.util.List;
import java.util.Optional;

public interface BlogUserService extends UserDetailsService {

	Optional<BlogUser> findByUsername(String username);

	BlogUser saveNewBlogUser(BlogUser blogUser);

	BlogUser findUserById(Long userId);

	List<BlogUser> findFriends(BlogUser currentUser);

	boolean areFriends(BlogUser sender, BlogUser receiver);

	BlogUser findById(Long id);

	Optional<BlogUser> findByEmail(String email);

	void deleteFriend(BlogUser currentUser, BlogUser friend);

	List<BlogUser> searchByUsername(String username);
}