package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.UserMapper;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.JwtProvider;
import com.tesis.ecommerce.coreapi.domain.port.out.PasswordEncoder;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private UserMapper userMapper;
    private User user;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        user = User.builder()
            .id(UUID.randomUUID())
            .email("user@example.com")
            .password("encoded")
            .name("User")
            .enabled(true)
            .build();
    }

    @Test
    void loginReturnsTokenAndUserWhenCredentialsMatch() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtProvider.generateToken(user.getId(), user.getEmail())).thenReturn("token-test");

        var useCase = new LoginUseCase(userRepository, passwordEncoder, jwtProvider, userMapper);
        var response = useCase.execute(user.getEmail(), "secret");

        assertThat(response.getToken()).isEqualTo("token-test");
        assertThat(response.getUser().getId()).isEqualTo(user.getId());
        assertThat(response.getUser().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void loginRejectsMissingUserAndInvalidPassword() {
        var useCase = new LoginUseCase(userRepository, passwordEncoder, jwtProvider, userMapper);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing@example.com", "secret"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);
        assertThatThrownBy(() -> useCase.execute(user.getEmail(), "bad"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid credentials");
    }

    @Test
    void getCurrentUserMapsUserAndFailsWhenMissing() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        var useCase = new GetCurrentUserUseCase(userRepository, userMapper);

        var dto = useCase.execute(user.getId());

        assertThat(dto.getName()).isEqualTo("User");
        assertThat(dto.getEnabled()).isTrue();

        UUID missingId = UUID.randomUUID();
        when(userRepository.findById(missingId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(missingId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }
}
