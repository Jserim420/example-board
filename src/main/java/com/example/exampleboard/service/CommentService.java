package com.example.exampleboard.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Comment;
import com.example.exampleboard.repository.JpaCommentRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	
	// 비밀번호 체크
	public boolean checkPassword(Long id, String password) {
		return encoder.matches(password, findByComment(id).getPassword());
	}

	// 좋아요 수 증가
	public int updateLikeCount(Long id, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Cookie[] cookies = request.getCookies();
        boolean checkCookie = false;
        int result = 0;
        // 현재 브라우저에 쿠키가 존재한다면
        if(cookies != null){
            for (Cookie cookie : cookies) // 브라우저 쿠키 조사
            {  // 이미 조회를 한 경우
            	if (cookie.getName().equals("Comment"+id)) { 
            		checkCookie = true;
            		AlertMessage.alertAndBack(response, "이미 좋아요를 눌렀습니다.");
            		return -1;
            	}
            }
            if(!checkCookie){ // 조회를 안한경우 새로운 쿠키 생성
                Cookie newCookie = createNewCommentCookie(id);
                response.addCookie(newCookie);
                result = commentRepository.updateLikes(id);
            }
        } else { // 브라우저에 아무런 쿠키도 없다면
            Cookie newCookie = createNewCommentCookie(id);
            response.addCookie(newCookie);
            result = commentRepository.updateLikes(id);
        }
        return result;
	}
	
	// 새로운 쿠키 생성 로직
	 private Cookie createNewCommentCookie(Long id) {
		 Cookie cookie = new Cookie("Comment"+id, String.valueOf(id));
	     cookie.setComment("조회수 중복 증가 방지 쿠키");	// 쿠키 용도 설명 기재
	     cookie.setMaxAge(getForTommorow()); 	// 하루
	     cookie.setHttpOnly(true);				// 서버에서만 조작 가능
	     return cookie;
	 }

	// 다음 날 정각까지 남은 시간
	 private int getForTommorow() {
		 LocalDateTime now = LocalDateTime.now();
	     LocalDateTime tommorow = LocalDateTime.now().plusDays(1L).truncatedTo(ChronoUnit.DAYS);// 현재시간+하루=>return 그때의 시간
	     return (int) now.until(tommorow, ChronoUnit.SECONDS); // 남은 시간 초단위로 리턴
	 }
}
