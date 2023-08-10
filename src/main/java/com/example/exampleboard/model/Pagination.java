package com.example.exampleboard.model;

import org.springframework.web.util.UriComponentsBuilder;

public class Pagination {
	private int pageNum;
	private int amount;
	
	public Pagination() {
		this(1, 10);
	}
	
	public Pagination(int pageNum, int amount) {
		this.pageNum = pageNum;
		this.amount =amount;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		if(pageNum<=0) this.pageNum=1;
		else this.pageNum = pageNum;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		if(amount != this.amount) this.amount = 10;
		else this.amount =amount;
	}
	

	
	@Override
    public String toString() {
        return "Criteria [page=" + this.pageNum + ", perPageNum=" + this.amount + "]";
    }
	
	
}
