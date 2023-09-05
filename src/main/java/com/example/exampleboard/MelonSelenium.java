package com.example.exampleboard;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;



public class MelonSelenium {

	private WebDriver driver;
	private String url;
	
	// 드라이버 설치 경로
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "D:\\tpfla9876\\chromedriver-win64\\chromedriver.exe";
	
	public MelonSelenium() {
		// WebDriver 경로 설정
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		// WebDriver 옵션 설정
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
        
		driver = new ChromeDriver(options);
		
		url = "https://www.melon.com/chart/index.htm";
		driver.get(url);
	}
	
	public void setURL(String url) {
		if(url==null) {
			driver.get("https://www.melon.com/chart/index.htm");
		} else driver.get(url);
	}
	
	public List<WebElement> getTitle() {
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		return driver.findElements(By.cssSelector(".ellipsis.rank01"));
	}
	
	public List<WebElement> getSinger() {
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		return driver.findElements(By.cssSelector(".ellipsis.rank02"));
	}
	
	public List<WebElement> getLikeCount() {
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		return driver.findElements(By.className("cnt"));
	}
	
	public List<WebElement> getRank() {
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		return driver.findElements(By.className("rank"));
	}
	
	public List<WebElement> getSongNo() {
		return driver.findElements(By.id("lst50"));
	}
    
	public WebElement getLyric(String no) {
		setURL("https://www.melon.com/song/detail.htm?songId="+no);
		return driver.findElement(By.className("lyric"));
	}
	
	public WebElement getSongName() {
		return driver.findElement(By.className("song_name"));
	}
	
	public WebElement getArtist() {
		return driver.findElement(By.className("artist"));
	}
	
	public WebElement getSongLikeCnt() {
		return driver.findElement(By.id("d_like_count"));
	}
	
	public WebElement getReply() {
		return driver.findElement(By.id("revCnt"));
	}
}
