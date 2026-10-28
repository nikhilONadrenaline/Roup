package com.backend.Auth_Svc.Config;

import com.backend.Auth_Svc.Entity.User;
import com.backend.Auth_Svc.Service.OAuthCodeService;
import com.backend.Auth_Svc.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final OAuthCodeService codeService;
    private final String frontendBaseUrl;

    public OAuth2SuccessHandler(UserService userService, OAuthCodeService codeService,
                                @Value("${app.frontend-base-url}") String frontendBaseUrl) {
        this.userService = userService;
        this.codeService = codeService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User provider = (OAuth2User) authentication.getPrincipal();
        String email = provider.getAttribute("email");
        Object verified = provider.getAttribute("email_verified");
        if (email == null || email.isBlank() || !Boolean.TRUE.equals(verified)) {
            getRedirectStrategy().sendRedirect(request, response, frontendBaseUrl + "/login?oauth=failed");
            return;
        }
        String name = provider.getAttribute("name");
        String picture = provider.getAttribute("picture");
        User user = userService.findOrCreateOAuthUser(email, name, picture);
        String code = codeService.issueFor(user);
        String target = UriComponentsBuilder.fromUriString(frontendBaseUrl)
                .path("/oauth-success").queryParam("code", code).build().encode().toUriString();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
