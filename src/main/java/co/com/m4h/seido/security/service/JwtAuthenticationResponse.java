package co.com.m4h.seido.security.service;

import java.io.Serializable;
import java.util.List;

import co.com.m4h.seido.model.Addon;

public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 1250166508152483573L;

	private final Long id;
	private final String userName;
	private final String firstName;
	private final String lastName;
	private final String role;
	private final Long companyid;
	private final String token;
	private final List<Addon> addons;

	public JwtAuthenticationResponse(Long id, String userName, String firstName, String lastName, String role,
			Long companyid, String token, List<Addon> addons) {
		this.id = id;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
		this.companyid = companyid;
		this.token = token;
		this.addons = addons;
	}

	public Long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getRole() {
		return role;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public String getToken() {
		return this.token;
	}

	public List<Addon> getAddons() {
		return this.addons;
	}
}
