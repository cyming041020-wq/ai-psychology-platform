package com.psychology.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.psychology.auth.entity.SysUser;
import com.psychology.auth.exception.InvalidCredentialsException;
import com.psychology.auth.mapper.SysUserMapper;
import com.psychology.auth.model.LoginRequest;
import com.psychology.auth.model.LoginResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void rejectsInvalidPasswordWithoutIssuingToken() {
        SysUser user = user(1L, "user@example.com", "USER", 1);
        when(sysUserMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("wrong-password"), eq("bcrypt-hash"))).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("invalid username or password");
        verify(jwtService, never()).issueToken(any());
    }

    @Test
    void returnsProfileAndBearerTokenForActiveUser() {
        SysUser user = user(7L, "user@example.com", "USER", 1);
        user.setDisplayName("心理支持用户");
        when(sysUserMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("correct-password"), eq("bcrypt-hash"))).thenReturn(true);
        when(jwtService.issueToken(user)).thenReturn("jwt-token");
        when(jwtService.expirationSeconds()).thenReturn(86400L);

        LoginResponse response = authService.login(new LoginRequest("user@example.com", "correct-password"));

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86400L);
        assertThat(response.user().id()).isEqualTo(7L);
        assertThat(response.user().role()).isEqualTo("USER");
    }

    private SysUser user(Long id, String username, String role, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash("bcrypt-hash");
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
