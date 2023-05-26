package org.thirty.app.service;

import org.thirty.app.model.Comment;

public interface CommentService {

	Comment save(Comment comment);

	void delete(Comment comment);

}
