package com.example.exampleboard.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.model.Comment;
import com.example.exampleboard.repository.JdbcCommentRepository;

public class CommentService {
	
	private JdbcCommentRepository commentRepository;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	
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
	
	public boolean checkPassword(Long id, String password) {
		return encoder.matches(password, findByComment(id).getPassword());
	}
}
