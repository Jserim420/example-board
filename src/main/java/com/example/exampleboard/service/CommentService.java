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
		return commentRepository.findByAllComments(id);
	}
	
	public Comment findByComment(Long id) {
		return commentRepository.findByComment(id).get();
	}
	
	public void modify(Long id, String body) {
		commentRepository.update(id, body);
	}
	
	public void delete(Long id) {
		commentRepository.delete(id);
	}
	
	public void updateLike(Long id) {
		commentRepository.updateLikeCount(id);
	}
}
