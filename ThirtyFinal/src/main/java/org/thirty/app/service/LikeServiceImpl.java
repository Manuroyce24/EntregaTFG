package org.thirty.app.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.Like;
import org.thirty.app.model.Post;
import org.thirty.app.repository.LikeRepository;
import org.thirty.app.repository.PostRepository;

@Service
public class LikeServiceImpl implements LikeService {
	private final LikeRepository likeRepository;
	private final PostRepository postRepository;

	public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository) {
		this.likeRepository = likeRepository;
		this.postRepository = postRepository;
	}

	@Override
	public void likePost(Long postId, BlogUser user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
		Optional<Like> existingLike = likeRepository.findByBlogUserAndPost(user, post);
		if (existingLike.isEmpty()) {
			Like like = new Like();
			like.setBlogUser(user);
			like.setPost(post);
			likeRepository.save(like);
		}
	}

	@Override
	public void unlikePost(Long postId, BlogUser user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
		Optional<Like> existingLike = likeRepository.findByBlogUserAndPost(user, post);
		existingLike.ifPresent(like -> likeRepository.delete(like));
	}
}
