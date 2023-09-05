package com.example.exampleboard.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.MelonSelenium;
import com.example.exampleboard.model.Melon;
import com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MelonController {

	private final MelonSelenium melonSelenium;
	
	@Autowired
	public MelonController(MelonSelenium melonSelenium) {
		this.melonSelenium = melonSelenium;
	}
	
	@GetMapping("/chart")
	public String melonPage(Model model, 
			@PageableDefault(page=0, size=10, sort="writeDate", direction = Sort.Direction.DESC) Pageable pageable) {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:00");
		
		melonSelenium.setURL(null);
		
		List<Melon> melonList = new ArrayList<>();
		List<WebElement> titles = melonSelenium.getTitle();
		List<WebElement> singers = melonSelenium.getSinger();
		List<WebElement> counts = melonSelenium.getLikeCount();
		List<WebElement> ranks = melonSelenium.getRank();
		List<WebElement> songNumbers = melonSelenium.getSongNo();
		
		for(int i=pageable.getPageNumber()*10; i<pageable.getPageNumber()*10+10;) {
			Melon melon = new Melon();
			melon.setTitle(titles.get(i).getText());
			melon.setSinger(singers.get(i).getText());
			melon.setLikeCount(counts.get(i+7).getText());
			melon.setRank(ranks.get(i+1).getText());
			melon.setSongNumber(songNumbers.get(i).getAttribute("data-song-no"));
		
			melonList.add(melon);

			log.info("MelonList {} ", melonList.size());
			log.info("List {} [ 제목 : {} , 가수 : {} , 곡번호 : {} ] ", ++i, melon.getTitle(), melon.getSinger(), melon.getSongNumber());
		}
		
		
		model.addAttribute("charts", melonList);
		model.addAttribute("time", simpleDateFormat.format(now));
		
		return "/melon/melonChart";
	}
	
	@GetMapping("/chart/detail")
	public String infoPage(Model model, @RequestParam(name="no") String songNumber) {
		
		
		List<WebElement> songNumbers = melonSelenium.getSongNo();
		String lyric = "";
		
		for(int i=0; i<songNumbers.size(); i++) {
			if(songNumbers.get(i).getAttribute("data-song-no").equals(songNumber)) {
				lyric = melonSelenium.getLyric(songNumber).getText();
				log.info("가사 => \n{} ", lyric);
				break;
			}
		}
		
		Melon songInfo = new Melon();
	    songInfo.setTitle(melonSelenium.getSongName().getText());
	    songInfo.setSinger(melonSelenium.getArtist().getText());
	    songInfo.setLikeCount(melonSelenium.getSongLikeCnt().getText());
	    songInfo.setReply(melonSelenium.getReply().getText());
	    
	    log.info("노래 정보 : [ 제목 : {} , 가수 : {} , 좋아요 수 : {} , 댓글  : {} ]", 
	    		songInfo.getTitle(), songInfo.getSinger(), songInfo.getLikeCount(), songInfo.getReply());
		
	    model.addAttribute("songInfo", songInfo);
		model.addAttribute("songNum", songNumber);
		model.addAttribute("lyric", lyric);
		
		return "/melon/songInfo";
	}
	
}
