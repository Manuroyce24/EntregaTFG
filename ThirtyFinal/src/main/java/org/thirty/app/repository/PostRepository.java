package org.thirty.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thirty.app.model.Post;

import java.util.Collection;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

	Collection<Post> findAllByOrderByCreationDateDesc();

	Optional<Post> findById(Long id);

	Page<Post> findAll(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.body) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);
}
