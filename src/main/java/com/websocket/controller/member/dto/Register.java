package com.websocket.controller.member.dto;

import java.io.Serializable;

public class Register implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6756071189242707476L;
	private String userName;
	private String password;
	private String oncePassword;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOncePassword() {
		return oncePassword;
	}
	public void setOncePassword(String oncePassword) {
		this.oncePassword = oncePassword;
	}
	
}
