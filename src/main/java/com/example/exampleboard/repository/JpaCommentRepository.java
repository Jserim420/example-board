package com.example.exampleboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exampleboard.model.Comment;

public interface JpaCommentRepository extends JpaRepository<Comment, Long>{

	List<Comment> findByBoardId(Long id);
}
