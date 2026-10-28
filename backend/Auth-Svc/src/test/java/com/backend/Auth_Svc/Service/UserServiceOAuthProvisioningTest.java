package com.backend.Auth_Svc.Service;

import com.backend.Auth_Svc.Entity.User;
import com.backend.Auth_Svc.Enum.ExpertStatus;
import com.backend.Auth_Svc.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceOAuthProvisioningTest {
    @Mock UserRepository users;
    @InjectMocks UserService userService;

    @Test
    void existingVerifiedEmailKeepsExistingAccountDetails() {
        User existing = new User();
        existing.setEmailId("person@example.com");
        existing.setName("Existing Roup Name");
        existing.setAvatar("existing-avatar");
        when(users.findByEmailId("person@example.com")).thenReturn(existing);

        User result = userService.findOrCreateOAuthUser("person@example.com", "Provider Name", "provider-avatar");

        assertThat(result).isSameAs(existing);
        assertThat(result.getName()).isEqualTo("Existing Roup Name");
        assertThat(result.getAvatar()).isEqualTo("existing-avatar");
        verify(users, never()).saveAndFlush(any(User.class));
    }

    @Test
    void newProviderAccountUsesOrdinaryUnfundedAccountDefaults() {
        when(users.findByEmailId("person@example.com")).thenReturn(null);
        when(users.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.findOrCreateOAuthUser("person@example.com", "Provider Name", "provider-avatar");

        assertThat(result.getName()).isEqualTo("Provider Name");
        assertThat(result.getAvatar()).isEqualTo("provider-avatar");
        assertThat(result.getPassword()).isNull();
        assertThat(result.getExpertStatus()).isEqualTo(ExpertStatus.NOT_REGISTERED);
        assertThat(result.getTotalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getReservedBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getCreatedAt()).isNotNull();
        verify(users).saveAndFlush(any(User.class));
    }
}
