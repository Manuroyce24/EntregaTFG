package org.thirty.app.service;

import org.springframework.stereotype.Service;
import org.thirty.app.model.Comment;
import org.thirty.app.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;

	public CommentServiceImpl(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	@Override
	public Comment save(Comment comment) {
		return commentRepository.saveAndFlush(comment);
	}

	@Override
	public void delete(Comment comment) {
		commentRepository.delete(comment);
	}
}
