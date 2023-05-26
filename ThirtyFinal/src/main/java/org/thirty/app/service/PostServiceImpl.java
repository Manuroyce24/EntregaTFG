package org.thirty.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thirty.app.model.Post;
import org.thirty.app.repository.PostRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;

	public PostServiceImpl(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Override
	public Optional<Post> getById(Long id) {
		return postRepository.findById(id);
	}

	@Override
	public Collection<Post> getAll() {
		return postRepository.findAllByOrderByCreationDateDesc();
	}

	@Override
	public Post save(Post post) {
		return postRepository.saveAndFlush(post);
	}

	@Override
	public void delete(Post post) {
		postRepository.delete(post);
	}

	@Override
	public Optional<Post> findById(Long postId) {
		return Optional.empty();
	}

	@Override
	public Page<Post> findAllPosts(Pageable pageable) {
		return postRepository.findAll(pageable);
	}

	@Override
	public Page<Post> searchPosts(String searchTerm, Pageable pageable) {
		return postRepository.searchPosts(searchTerm, pageable);
	}
}
