package com.example.exampleboard.model;


public class PageDto {
	private int totalData; // 게시판 전체 데이터 수
	private int thisPage;
	private int displayAmount = 10 ; // 한 페이지당 5개
	private int startPage; // 페이지네이션 시작페이지
	private int endPage; // 페이지네이션 마지막페이지
	private int realEnd;
	private boolean prev,next; // 이전, 다음 버튼 활성화
	private Pagination pagination;


	public int getThisPage() {
		return thisPage;
	}


	public int getTotalData() {
		return totalData;
	}


	public int getDisplayAmount() {
		return displayAmount;
	}


	public int getStartPage() {
		return startPage;
	}


	public int getEndPage() {
		return endPage;
	}


	public boolean isPrev() {
		return prev;
	}


	public boolean isNext() {
		return next;
	}


	public int getRealEnd() {
		return realEnd;
	}


	public void setRealEnd(int realEnd) {
		this.realEnd =(int)Math.ceil(this.totalData/(double)this.displayAmount);
	}

	public PageDto(Pagination pagination, int totalData) {
		this.thisPage = pagination.getPageNum();
		this.displayAmount = pagination.getAmount();
		this.totalData = totalData ;
		this.pagination = pagination;
		
		this.endPage = (int)Math.ceil(this.thisPage / 5.0) * 5;
		this.startPage = endPage - 5 +1 ;
		this.realEnd = (int)Math.ceil(totalData/10.0);
		
		
		if(this.endPage > this.realEnd) {
			this.endPage = realEnd;
		}
		
		if(this.startPage>1) this.prev=true;
		else this.prev=false;
		
		if(this.endPage<realEnd) this.next=true;
		else this.next=false;
	}
	
	@Override
	public String toString() {
		return this.thisPage + ", " + startPage + ", " + endPage + ", " + realEnd;
	}
	
}
