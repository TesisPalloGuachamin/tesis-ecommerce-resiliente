package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.JwtProvider;
import com.tesis.ecommerce.coreapi.domain.port.out.PasswordEncoder;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthResponse;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UserDTO;
import com.tesis.ecommerce.coreapi.application.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userMapper = userMapper;
    }

    public AuthResponse execute(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtProvider.generateToken(user.getId(), user.getEmail());
        UserDTO userDTO = userMapper.toDTO(user);

        return AuthResponse.builder()
            .token(token)
            .user(userDTO)
            .build();
    }
}

