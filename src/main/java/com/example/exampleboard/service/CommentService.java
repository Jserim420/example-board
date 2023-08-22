package com.example.exampleboard.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.model.Comment;
import com.example.exampleboard.repository.JpaCommentRepository;

import jakarta.transaction.Transactional;

@Transactional
public class CommentService {
	
	private JpaCommentRepository commentRepository;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public CommentService(JpaCommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}
	
	// 댓글 작성
	public void write(Comment comment) {
		comment.setPassword(encoder.encode(comment.getPassword()));
		commentRepository.save(comment);
	}
	
	// 특정게시글의 모든 댓글 찾기
	public List<Comment> findByAllComments(Long id) {
		return commentRepository.findByBoardId(id);
	}
	
	// 특정 댓글 찾기
	public <Optional>Comment findByComment(Long id) {
		return commentRepository.findById(id).get();
	}
	
	// 댓글 수정
	public void modify(Long id, String body) {
		Comment updateComment = findByComment(id);
		updateComment.setBody(body);
	}
	
	// 댓글 삭제
	public void delete(Long id) {
		commentRepository.deleteById(id);
	}
	
	// 좋아요 체크
//	public void updateLike(Long id) {
//		commentRepository.updateLikeCount(id);
//	}
	
	// 비밀번호 채크
	public boolean checkPassword(Long id, String password) {
		return encoder.matches(password, findByComment(id).getPassword());
	}
}
