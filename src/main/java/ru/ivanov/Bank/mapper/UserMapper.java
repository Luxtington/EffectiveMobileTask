package ru.ivanov.Bank.mapper;

import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toUserFromCreateRequest(CreateUserRequestDto requestDto, @Context PasswordEncoder passwordEncoder);
    @AfterMapping
    default void encodePassword(CreateUserRequestDto requestDto,
                                @Context PasswordEncoder passwordEncoder,
                                @MappingTarget User user) {
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
    }

    @Mapping(target = "password", ignore = true)
    User toUserFromUpdateRequest(UpdateUserRequestDto requestDto, @Context PasswordEncoder passwordEncoder);
    @AfterMapping
    default void encodePassword(UpdateUserRequestDto requestDto,
                                @Context PasswordEncoder passwordEncoder,
                                @MappingTarget User user) {
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
    }

    UserResponseDto toDtoFromUser(User user);
}
