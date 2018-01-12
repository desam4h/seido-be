package co.com.m4h.seido.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import co.com.m4h.seido.model.User;
import co.com.m4h.seido.model.security.Authority;

public final class JwtUserFactory {

	private JwtUserFactory() {
	}

	public static JwtUser create(User user) {
		return new JwtUser(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname(), user.getEmail(),
				user.getPassword(), mapToGrantedAuthorities(Arrays.asList(user.getAuthority())),
				user.getEnabled() == 1 ? true : false, user.getLastPasswordResetDate(), user.getCompany());
	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(List<Authority> authorities) {
		return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
				.collect(Collectors.toList());
	}
}
