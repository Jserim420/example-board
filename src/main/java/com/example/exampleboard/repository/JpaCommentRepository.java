package com.example.exampleboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.exampleboard.model.Comment;

public interface JpaCommentRepository extends JpaRepository<Comment, Long>{

	List<Comment> findByBoardId(Long id);
	
	@Modifying
	@Query("UPDATE Comment c set c.likeCount = c.likeCount + 1"
			+ "WHERE c.id = :id")
	int updateLikes(@Param("id") Long id);
	
}
