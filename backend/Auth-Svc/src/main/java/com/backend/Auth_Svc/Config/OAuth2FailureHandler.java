package com.backend.Auth_Svc.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final String frontendBaseUrl;
    public OAuth2FailureHandler(@Value("${app.frontend-base-url}") String frontendBaseUrl) { this.frontendBaseUrl = frontendBaseUrl; }
    @Override public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        getRedirectStrategy().sendRedirect(request, response, frontendBaseUrl + "/login?oauth=failed");
    }
}
