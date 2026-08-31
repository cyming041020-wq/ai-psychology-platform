package com.psychology.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.psychology.auth.entity.SysUser;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "local-test-secret-that-is-longer-than-32-bytes",
            Duration.ofHours(2));

    @Test
    void tokenContainsOnlyTheUserIdentityAndRoleClaims() {
        SysUser user = new SysUser();
        user.setUsername("user@example.com");
        user.setRole("COUNSELOR");

        String token = jwtService.issueToken(user);
        var claims = jwtService.parse(token).getPayload();

        assertThat(claims.getSubject()).isEqualTo("user@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("COUNSELOR");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        assertThat(jwtService.expirationSeconds()).isEqualTo(7200L);
    }
}
