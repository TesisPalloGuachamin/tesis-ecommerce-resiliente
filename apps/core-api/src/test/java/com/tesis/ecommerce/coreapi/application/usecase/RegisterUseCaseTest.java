package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.JwtProvider;
import com.tesis.ecommerce.coreapi.domain.port.out.PasswordEncoder;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.application.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private UserMapper userMapper;
    private RegisterUseCase registerUseCase;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        registerUseCase = new RegisterUseCase(userRepository, passwordEncoder, jwtProvider, userMapper);
    }

    @Test
    void testRegisterSuccess() {
        String email = "test@example.com";
        String password = "password123";
        String name = "Test User";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtProvider.generateToken(1L, email)).thenReturn("jwt-token");

        var response = registerUseCase.execute(email, password, name);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals(email, response.getUser().getEmail());
    }

    @Test
    void testRegisterWithDuplicateEmail() {
        String email = "existing@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            registerUseCase.execute(email, "password123", "Test User");
        });
    }
}

