package org.thirty.app.service;

import org.thirty.app.model.BlogUser;

public interface LikeService {

	void likePost(Long postId, BlogUser user);

	void unlikePost(Long postId, BlogUser user);
}
