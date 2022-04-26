package com.tene.controller.house.actgrp.period;

public class DispOrd {
	private boolean satisfy = false;
	private byte key = 0;
	
	public DispOrd(byte key, boolean satisfy) {
		this.satisfy = satisfy;
		this.key = key;		
	}

	public boolean isSatisfy() {
		return satisfy;
	}

	public void setSatisfy(boolean satisfy) {
		this.satisfy = satisfy;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}
}
