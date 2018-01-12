package co.com.m4h.seido.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import co.com.m4h.seido.model.Company;
import co.com.m4h.seido.security.JwtUser;

public final class SecurityUtil {

	public static final String getRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		JwtUser data = (JwtUser) authentication.getPrincipal();
		String role = data.getAuthorities().iterator().next().getAuthority();

		return role;
	}

	public static final Long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		JwtUser data = (JwtUser) authentication.getPrincipal();
		Long userId = data.getId();

		return userId;
	}

	/**
	 * Specific function that gets the companyID of the current logged user from the
	 * JWT
	 * 
	 * @return Identifier of the company for the current user
	 */
	public static final Long getCompanyId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		JwtUser data = (JwtUser) authentication.getPrincipal();
		Long companyId = data.getCompany() == null ? 0L : data.getCompany().getId();

		return companyId;
	}

	/**
	 * Usefult method that creates the company object with the information from JWT.
	 * 
	 * @return
	 */
	public static final Company getCompany() {
		Long companyId = getCompanyId();
		return new Company(companyId);
	}
}
