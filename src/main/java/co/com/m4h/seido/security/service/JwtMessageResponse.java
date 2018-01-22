package co.com.m4h.seido.security.service;

import java.io.Serializable;

public class JwtMessageResponse implements Serializable {

	private static final long serialVersionUID = -2690962780270062329L;

	private final String message;

	public JwtMessageResponse(String message) {
		this.message = message;

	}

	public String getmessage() {
		return this.message;
	}
}
