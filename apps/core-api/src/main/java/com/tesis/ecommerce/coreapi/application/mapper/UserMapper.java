package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .enabled(user.getEnabled())
            .build();
    }

    public User toDomain(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
            .id(dto.getId())
            .email(dto.getEmail())
            .name(dto.getName())
            .enabled(dto.getEnabled())
            .build();
    }
}

