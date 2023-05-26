package org.thirty.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.Like;
import org.thirty.app.model.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByBlogUserAndPost(BlogUser blogUser, Post post);
}
