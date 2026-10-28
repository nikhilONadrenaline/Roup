package com.backend.Auth_Svc.Repository;

import com.backend.Auth_Svc.Entity.OAuthAuthorizationCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:oauth-code-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OAuthAuthorizationCodeRepositoryTest {
    @Autowired OAuthAuthorizationCodeRepository repository;

    @Test
    void codeCanBeConsumedOnlyOnce() {
        Instant now = Instant.now();
        repository.save(code("hash-once", now.plusSeconds(60)));

        assertThat(repository.consumeIfValid("hash-once", now)).isEqualTo(1);
        assertThat(repository.consumeIfValid("hash-once", now)).isZero();
    }

    @Test
    void expiredCodeCannotBeConsumed() {
        Instant now = Instant.now();
        repository.save(code("hash-expired", now.minusSeconds(1)));

        assertThat(repository.consumeIfValid("hash-expired", now)).isZero();
    }

    private OAuthAuthorizationCode code(String hash, Instant expiresAt) {
        OAuthAuthorizationCode code = new OAuthAuthorizationCode();
        code.setCodeHash(hash);
        code.setUserId("user-id");
        code.setExpiresAt(expiresAt);
        code.setConsumed(false);
        return code;
    }
}
