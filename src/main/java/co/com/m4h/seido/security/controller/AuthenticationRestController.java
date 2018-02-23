package co.com.m4h.seido.security.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.model.Addon;
import co.com.m4h.seido.model.security.AuthorityName;
import co.com.m4h.seido.security.JwtAuthenticationRequest;
import co.com.m4h.seido.security.JwtTokenUtil;
import co.com.m4h.seido.security.JwtUser;
import co.com.m4h.seido.security.service.JwtAuthenticationResponse;

@RestController
public class AuthenticationRestController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			Device device) throws AuthenticationException {

		// Perform the security
		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername().toLowerCase(),
						authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Reload password post-security so we can generate token
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername().toLowerCase());
		final String token = jwtTokenUtil.generateToken(userDetails, device);

		JwtUser user = (JwtUser) userDetails;

		String role = user.getAuthorities().iterator().next().getAuthority();

		List<Addon> addons = new ArrayList<>();
		if (role.equals(AuthorityName.ROLE_ADMIN.name()))
			if (user.getCompany().getAddons() != null)
				for (Addon addon : user.getCompany().getAddons())
					addons.add(addon);

		// Return the token
		return ResponseEntity.ok(new JwtAuthenticationResponse(user.getId(), user.getUsername(), user.getFirstname(),
				user.getLastname(), role, user.getCompany().getId(), token, addons));
	}

	@RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

		String role = user.getAuthorities().iterator().next().getAuthority();

		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);

			List<Addon> addons = new ArrayList<>();
			if (role.equals(AuthorityName.ROLE_ADMIN.name()))
				if (user.getCompany().getAddons() != null)
					for (Addon addon : user.getCompany().getAddons())
						addons.add(addon);

			return ResponseEntity.ok(new JwtAuthenticationResponse(user.getId(), user.getUsername(),
					user.getFirstname(), user.getLastname(), role, user.getCompany().getId(), refreshedToken, addons));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

}
