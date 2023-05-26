package org.thirty.app.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thirty.app.model.Post;

public interface PostService {

	Optional<Post> getById(Long id);

	Collection<Post> getAll();

	Post save(Post post);

	void delete(Post post);

	Optional<Post> findById(Long postId);

	Page<Post> findAllPosts(Pageable pageable);

	Page<Post> searchPosts(String searchTerm, Pageable pageable);
}
