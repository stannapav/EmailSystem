package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    public ResponseEntity<User> getUserByUsername(@RequestParam @NotBlank String username) {
        try {
            return ResponseEntity.ok(userService.getUserByUsername(username));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam @NotBlank String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size ) {
        return userService.getUsersPageable(page, size);
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User createdUser = userService.createUser(userDTO);
            URI location = URI.create("/api/users/" + createdUser.getId());

            return ResponseEntity
                    .created(location)
                    .body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserDTO userDTO ) {
        try {
            return ResponseEntity.ok(userService.updateUser(userId, userDTO));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId){
        try {
            userService.deleteUser(userId);

            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers(){
        userService.deleteAllUsers();

        return ResponseEntity.noContent().build();
    }
}
