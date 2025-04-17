package org.latouche.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	 @Override
	    public void onAuthenticationSuccess(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        Authentication authentication) throws IOException {

	        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

	        for (GrantedAuthority authority : authorities) {
	            String role = authority.getAuthority();

	            if (role.equals("ROLE_Administrateur")) {
	                response.sendRedirect("/user");
	                return;
	            } else if (role.equals("ROLE_bibliothecaire")) {
	                response.sendRedirect("/book");
	                return;
	            }
	        }

	        // Default fallback
	        response.sendRedirect("/");
	    }
	

}
