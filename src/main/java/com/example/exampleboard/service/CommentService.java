package com.example.exampleboard.service;

import java.util.List;

import com.example.exampleboard.model.Comment;
import com.example.exampleboard.repository.JdbcCommentRepository;

public class CommentService {
	
	private JdbcCommentRepository commentRepository;
	
	public CommentService(JdbcCommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}
	
	public void write(Comment comment) {
		commentRepository.save(comment);
	}
	
	public List<Comment> findByAllComments(Long id) {
		return commentRepository.findByAll(id);
	}
}
