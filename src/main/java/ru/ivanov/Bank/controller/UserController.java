package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") UUID id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody CreateUserRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(requestDto));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UserResponseDto> edit(@PathVariable("id") UUID id,
                                                    @RequestBody UpdateUserRequestDto requestDto){
        return ResponseEntity.ok(userService.save(requestDto, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
