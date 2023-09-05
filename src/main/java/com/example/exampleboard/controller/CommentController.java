package com.example.exampleboard.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;
import com.example.exampleboard.service.CommentService;
import com.example.exampleboard.service.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CommentController {

	private final CommentService commentService;
	private final JwtProvider jwtProvider;
	
	public CommentController(CommentService commentService, JwtProvider jwtProvider) {
		this.commentService = commentService;
		this.jwtProvider = jwtProvider;
	}
	
	// 댓글 작성 로직
	@PostMapping("/api/comment/write")
	public void write(@CookieValue(name="loginUser", required = false) String loginToken, 
			@RequestParam(name="boardNo") Long boardId,
			Board board, CommentForm commentForm,
			HttpServletResponse response) throws Exception  {
		Comment comment = new Comment();
		comment.setBody(commentForm.getBody());
		comment.setUserName(commentForm.getName());
		comment.setPassword(commentForm.getPassword());
		comment.setBoardId(boardId);
		comment.setLikeCount(0);
		comment.setWriteDate(new Date());
		
		if(commentForm.getBody()=="")
			AlertMessage.alertAndMove(response, "내용을 작성해주세요.","/board?boardNo=" + boardId);
		else if(commentForm.getName()=="")
			AlertMessage.alertAndMove(response, "작성자 닉네임을 입력해주세요.", "/board?boardNo=" + boardId);
		else if(commentForm.getPassword()=="")
			AlertMessage.alertAndMove(response, "비밀번호를 입력해주세요.", "/board?boardNo=" + boardId);
		else {
			try {
				commentService.write(comment);
			} catch (Exception e) {
				log.warn("댓글[ 글번호 : {} ] 작성 문제발생 => {} \n 작성자 [ 회원번호 : {} , 작성시 닉네임 : {} ] ", 
						boardId, e.getMessage(), jwtProvider.getToken("Id", loginToken), comment.getUserName());
				AlertMessage.alertAndBack(response, "댓글 작성 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			
			log.info("댓글 작성 완료 [ 글번호 : {} , 작성자 닉네임 : {} , 본문 : {} ] ", boardId, comment.getUserName(), comment.getBody());
			AlertMessage.alertAndMove(response, "댓글이 정상적으로 등록되었습니다.",
					"/board?boardNo=" + boardId);
		}
		
	}
	

	
	// 댓글 좋아요
	@GetMapping("/api/comment/like")
	public String like(@RequestParam(name="commentNo") Long commentId,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		Long boardId = commentService.findByComment(commentId).getBoardId();
		
		 try {
			 commentService.updateLikeCount(commentId, request, response);
		} catch (Exception e) {
			log.warn("댓글 좋아요수 증가 문제발생 => {} \n 글번호 {} , 댓글번호 {}", 
					e.getMessage(), boardId , commentId);
			AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
		
		 log.info("댓글 [ 번호 : {} ] 좋아요 수 증가 {}", commentId, commentService.findByComment(commentId).getLikeCount());
		return "redirect:/board?boardNo=" + commentService.findByComment(commentId).getBoardId();
	}
	
	// 댓글 수정-비밀번호 확인
	@GetMapping("/comment/userConfirm")
	public String confirm(@RequestParam(name="commentNo") Long id, Model model) {
	
		model.addAttribute("commentNo", id);
		model.addAttribute("option", "update");
		return "comment/userConfirm";
	}
	
	// 댓글 수정 로직
	@PostMapping("/api/comment/update")
	public void modify(@RequestParam(name="commentNo") Long commentId, CommentForm commentForm,
			HttpServletResponse response) throws Exception {
	
		if(commentForm.getBody()=="")AlertMessage.alertAndClose(response, "내용을 입력해주세요.");
		else {
			try {
				commentService.modify(commentId, commentForm.getBody());
			} catch (Exception e) {
				log.warn("댓글 [ 번호 : {} , 글번호 : {} ] 수정 문제발생 => {} ]", 
						 commentId, commentService.findByComment(commentId).getBoardId(), e.getMessage() );
				AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			log.info("댓글 [ 번호 : {} , 글번호 : {} ] 수정완료 \n => 본문 : {}", 
					commentId, commentService.findByComment(commentId).getBoardId(), commentForm.getBody() );
			AlertMessage.alertAndClose(response, "댓글이 정상적으로 수정되었습니다.");
		}
		
	}
	
	// 수정- 비밀번호 확인 로직
	@PostMapping("/api/comment/userConfirm")
	public String confirmAPI(@RequestParam(name="commentNo") Long commentId, Comment comment, CommentForm commentForm,
			HttpServletResponse response, Model model) throws Exception {
		try {
			if(commentService.checkPassword(commentId, commentForm.getPassword())) {
				System.out.println(commentService.findByComment(commentId).getBody());
				model.addAttribute("comment", commentService.findByComment(commentId));
				log.info("댓글 [ 번호 : {} ] 수정 비밀번호 확인 완료", commentId);
				return "comment/commentForm";
			}
			else AlertMessage.alertAndBack(response, "비밀번호가 일치하지 않습니다.");
		} catch (Exception e) {
			log.warn("댓글 [ 번호 : {} ] 비밀번호 확인 문제발생 => {} ]", 
					commentId, commentService.findByComment(commentId).getBoardId() ,e.getMessage() );
			AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
		
		return "";
	}
	
	// 댓글 삭제
	@GetMapping("/comment/delete")
	public String confirmDelete(@RequestParam(name="commentNo") Long id, Model model) {
	
		model.addAttribute("commentNo", id);
		model.addAttribute("option", "delete");
		return "comment/userConfirm";
	}
	
	// 댓글 삭제 로직
		@PostMapping("/api/comment/delete")
		public void delete(@RequestParam(name="commentNo") Long commentId,
				HttpServletResponse response, CommentForm commentForm) throws Exception {
			try {
				if(commentService.checkPassword(commentId, commentForm.getPassword())) {
					commentService.delete(commentId);
					log.info("댓글 [ 번호 : {} ] 삭제 완료", commentId);
					AlertMessage.alertAndClose(response, "댓글이 정상적으로 삭제되었습니다.");
				}
				else AlertMessage.alertAndBack(response, "비밀번호가 일치하지 않습니다.");
			} catch (Exception e) {
				log.warn("댓글 [ 번호 : {} , 글번호 : {} ] 삭제 문제발생 => {} ]", 
						commentId, commentService.findByComment(commentId).getBoardId() ,e.getMessage() );
				AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
		}
	
	
	
}
