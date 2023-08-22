package com.example.exampleboard.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Board;
import com.example.exampleboard.repository.JpaBoardRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Transactional
public class BoardService {
	
	private final JpaBoardRepository boardRepository;
	
	@Autowired
	public BoardService(JpaBoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	
	// 글 작성
	public Board write(Board board) {
		return boardRepository.save(board); 
	}
	
	// 글 찾기
	public Board findBoard(Long boardId) {
		return boardRepository.findById(boardId).get();
	}
	
	// 전체 글 사이즈 리턴
	public int boardSize() {
		return boardRepository.findAll().size();
	}
	
	// 페이징
	public Page<Board> boardList(Pageable pageable) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 0부터 시작
        pageable = PageRequest.of(page, 10, Sort.by("writeDate").descending().and(Sort.by("id").descending()));

		return boardRepository.findAll(pageable);
	}
	
	// 검색어 페이징
	public Page<Board> search(String option, String keyword, Pageable pageable) {
		if(option.equals("제목")) {
			int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 0부터 시작
	        pageable = PageRequest.of(page, 10, Sort.by("writeDate").descending().and(Sort.by("id").descending()));

			return boardRepository.findByTitleContaining(keyword, pageable);
		} else if(option.equals("내용")) {
			int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 0부터 시작
	        pageable = PageRequest.of(page, 10, Sort.by("writeDate").descending().and(Sort.by("id").descending()));

			return boardRepository.findByBodyContaining(keyword, pageable);
		} else if(option.equals("작성자")) {
			int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 0부터 시작
	        pageable = PageRequest.of(page, 10, Sort.by("writeDate").descending().and(Sort.by("id").descending()));

			return boardRepository.findByUserId(Integer.valueOf(keyword), pageable);
		} else return null;
		
	}
	
	
	/* 조회수&좋아요 증가 */
	public int updateCount(Long id, HttpServletRequest request, 
			HttpServletResponse response, String option) throws Exception {
		Cookie[] cookies = request.getCookies();
        boolean checkCookie = false;
        int result = 0;
        // 현재 브라우저에 쿠키가 존재한다면
        if(cookies != null){
            for (Cookie cookie : cookies) // 브라우저 쿠키 조사
            {
                // 이미 조회를 한 경우
            	if(option.equals("view")) {
            		if (cookie.getName().equals("Read"+id)) checkCookie = true;
            	} else if(option.equals("like")) {
            		if(cookie.getName().equals("Like"+id)) {
            			checkCookie = true;
            			AlertMessage.alertAndBack(response, "좋아요를 이미 눌렀습니다.");
                		return -1;
            		}
            	} else break;
            }
            if(!checkCookie){ // 조회를 안한경우 새로운 쿠키 생성
                Cookie newCookie = createNewBoardCookie(id, option);
                response.addCookie(newCookie);
               if(option.equals("view")) result = boardRepository.updateViews(id);
               else if(option.equals("like")) result = boardRepository.updateLikes(id);
               else result = 0;
            }
        } else { // 브라우저에 아무런 쿠키도 없다면
            Cookie newCookie = createNewBoardCookie(id, option);
            response.addCookie(newCookie);
            if(option.equals("view")) result = boardRepository.updateViews(id);
            else if(option.equals("like")) result = boardRepository.updateLikes(id);
            else result = 0;
        }
        return result;
	}
	
	// 새로운 쿠키 생성 로직
	 private Cookie createNewBoardCookie(Long id, String option) {
		 Cookie cookie = new Cookie("null", null);	
		  if(option.equals("view")) {
				cookie = new Cookie("Read"+id, String.valueOf(id));
	     	} else if(option.equals("like")) {
	     		cookie = new Cookie("Like"+id, String.valueOf(id));
	     	} else return null;
	        cookie.setComment("조회수 중복 증가 방지 쿠키");	// 쿠키 용도 설명 기재
	        cookie.setMaxAge(getForTommorow()); 	// 하루
	        cookie.setHttpOnly(true);				// 서버에서만 조작 가능
	        return cookie;
	    }

	// 다음 날 정각까지 남은 시간
	 private int getForTommorow() {
		 LocalDateTime now = LocalDateTime.now();
	     LocalDateTime tommorow = LocalDateTime.now().plusDays(1L).truncatedTo(ChronoUnit.DAYS); // 현재시간+하루=>return 그때의 시간
	     return (int) now.until(tommorow, ChronoUnit.SECONDS); // 남은 시간 초단위로 리턴
	 }
	 
	    
	 // 글 수정
	public void update(Board board, Long id) {
		Board findBoard = boardRepository.findById(id).get();
		findBoard.setBody(board.getBody());
		findBoard.setTitle(board.getTitle());
	}
		
	// 글 삭제
	public void delete(Long id) {
		boardRepository.deleteById(id);
	}
	
	
//	public List<Board> findAllBoards() {
//		return boardRepository.findByBoards();
//	}
//	

//	
//	
//	public List<Board> findKeywordPage(String selected, String keyword, int pageNum, int pageSize) {
//		int offset = (pageNum - 1) * pageSize;
//		System.out.println("offset : " + offset);
//		return boardRepository.findByKeyword(selected, keyword, offset);
//	}
//	
//	
//	


//	

//	

//	
//	public int keywordSize(String selected, String keyword) {
//		return boardRepository.findByKeywordSize(selected,keyword);
//	}
}
	

