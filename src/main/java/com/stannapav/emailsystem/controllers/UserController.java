package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.ResponseUserDTO;
import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Gets user by username", description = "Gets user from system by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/username")
    public ResponseEntity<ResponseUserDTO> getUserByUsername(@RequestParam @NotBlank String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Operation(summary = "Gets user by email", description = "Gets user from system by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/email")
    public ResponseEntity<ResponseUserDTO> getUserByEmail(@RequestParam @NotBlank String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(summary = "Gets all user", description = "Gets all user from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public Page<ResponseUserDTO> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size ) {
        return userService.getUsersPageable(page, size);
    }

    @Operation(summary = "Creates user", description = "Creates and add user to system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/addUser")
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        ResponseUserDTO createdUser = userService.createUser(userDTO);
        URI location = URI.create("/api/users/" + createdUser.getId());

        return ResponseEntity
                .created(location)
                .body(createdUser);
    }

    @Operation(summary = "Updates user", description = "Updates user in system by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> updateUser(
            @PathVariable @Min(1) Integer userId,
            @Valid @RequestBody UserDTO userDTO ) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @Operation(summary = "Deletes user", description = "Deletes user from system by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Integer userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes all user", description = "Deletes all user from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All user deleted successfully",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers(){
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }
}
