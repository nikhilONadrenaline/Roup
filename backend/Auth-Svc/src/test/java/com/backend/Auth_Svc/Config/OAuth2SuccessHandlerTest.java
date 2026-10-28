package com.backend.Auth_Svc.Config;

import com.backend.Auth_Svc.Entity.User;
import com.backend.Auth_Svc.Service.OAuthCodeService;
import com.backend.Auth_Svc.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {
    @Mock UserService userService;
    @Mock OAuthCodeService codeService;

    @Test
    void verifiedGoogleIdentityRedirectsWithOpaqueCodeOnly() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(userService, codeService, "https://app.roup.test");
        User user = new User();
        user.setId("user-1");
        when(userService.findOrCreateOAuthUser("person@example.com", "Person", "https://example.com/avatar.png")).thenReturn(user);
        when(codeService.issueFor(user)).thenReturn("opaque-code");
        var principal = new DefaultOAuth2User(List.of(), Map.of(
                "sub", "google-subject", "email", "person@example.com", "email_verified", true,
                "name", "Person", "picture", "https://example.com/avatar.png"), "sub");
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, new UsernamePasswordAuthenticationToken(principal, null));

        assertThat(response.getRedirectedUrl()).isEqualTo("https://app.roup.test/oauth-success?code=opaque-code");
        assertThat(response.getRedirectedUrl()).doesNotContain("token", "jwt", "access_token");
        verify(userService).findOrCreateOAuthUser("person@example.com", "Person", "https://example.com/avatar.png");
        verify(codeService).issueFor(user);
    }

    @Test
    void unverifiedGoogleEmailDoesNotProvisionAnAccountOrIssueCode() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(userService, codeService, "https://app.roup.test");
        var principal = new DefaultOAuth2User(List.of(), Map.of(
                "sub", "google-subject", "email", "person@example.com", "email_verified", false), "sub");
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, new UsernamePasswordAuthenticationToken(principal, null));

        assertThat(response.getRedirectedUrl()).isEqualTo("https://app.roup.test/login?oauth=failed");
        verifyNoInteractions(userService, codeService);
    }
}
