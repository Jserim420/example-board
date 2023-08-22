package com.example.exampleboard.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;

import com.example.exampleboard.model.Board;


public interface JpaBoardRepository extends JpaRepository<Board, Long>{

	
	// 페이징
	Page<Board> findAll(Pageable pageable);
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);
	Page<Board> findByBodyContaining(String keyword, Pageable pageable);
	Page<Board> findByUserId(int id, Pageable pageable);
	
	/*데이터의 변경*/
	
	// 조회수 증가
	@Modifying
	@Query("UPDATE Board b set b.viewCount = b.viewCount + 1 "
			+ "WHERE b.id = :id")
	int updateViews(@Param("id") Long id);
	
	// 좋아요 증가
	@Modifying
	@Query("UPDATE Board b set b.likeCount = b.likeCount +1"
			+ "WHERE b.id = :id")
	int updateLikes(@Param("id") Long id);
}
