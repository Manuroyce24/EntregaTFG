package org.thirty.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thirty.app.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
