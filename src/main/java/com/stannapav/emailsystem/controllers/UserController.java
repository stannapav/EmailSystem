package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.ResponseUserDTO;
import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/username")
    public ResponseEntity<ResponseUserDTO> getUserByUsername(@RequestParam @NotBlank String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/email")
    public ResponseEntity<ResponseUserDTO> getUserByEmail(@RequestParam @NotBlank String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    public Page<ResponseUserDTO> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size ) {
        return userService.getUsersPageable(page, size);
    }

    @PostMapping("/addUser")
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        ResponseUserDTO createdUser = userService.createUser(userDTO);
        URI location = URI.create("/api/users/" + createdUser.getId());

        return ResponseEntity
                .created(location)
                .body(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> updateUser(
            @PathVariable @Min(1) Integer userId,
            @Valid @RequestBody UserDTO userDTO ) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Integer userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers(){
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }
}
