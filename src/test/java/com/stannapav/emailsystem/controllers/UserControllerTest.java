package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.ResponseUserDTO;
import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    private ResponseUserDTO responseUser;

    @BeforeEach
    public void init(){
        userDTO = new UserDTO();
        userDTO.setUsername("name");
        userDTO.setEmail("name@gmail.com");

        responseUser = new ResponseUserDTO();
        responseUser.setId(1);
        responseUser.setUsername("name");
        responseUser.setEmail("name@gmail.com");
    }

    @Test
    public void UserController_CreateUser_ReturnCreated() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(responseUser);

        ResultActions response = mockMvc.perform(post("/api/users/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    public void UserController_UpdateUser_ReturnOk() throws Exception {
        when(userService.updateUser(eq(1),any(UserDTO.class))).thenReturn(responseUser);

        ResultActions response = mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    public void UserController_GetUserByUsername_ReturnOk() throws Exception {
        when(userService.getUserByUsername(any(String.class))).thenReturn(responseUser);

        ResultActions response = mockMvc.perform(get("/api/users/username")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", userDTO.getUsername()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    public void UserController_GetUserByEmail_ReturnOk() throws Exception {
        when(userService.getUserByEmail(any(String.class))).thenReturn(responseUser);

        ResultActions response = mockMvc.perform(get("/api/users/email")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", userDTO.getEmail()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    void UserController_GetUserByBlankUsername_ReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/username")
                        .param("username", ""))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void UserController_GetUserByBlankEmail_ReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/email")
                        .param("email", ""))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void UserController_GetAllUsers_ReturnOk() throws Exception {
        Page<ResponseUserDTO> page =
                new PageImpl<>(List.of(responseUser), PageRequest.of(0, 20), 1);

        when(userService.getUsersPageable(0, 20)).thenReturn(page);

        ResultActions response = mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "20"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.content[0].email").value(userDTO.getEmail()));
    }

    @Test
    void deleteUser_ok() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

}
