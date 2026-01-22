package com.stannapav.emailsystem.services;

import com.stannapav.emailsystem.db.dtos.ResponseUserDTO;
import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import com.stannapav.emailsystem.db.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_CreateUser_ReturnResponseUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");

        User user = new User();
        ResponseUserDTO responseUserDTO = new ResponseUserDTO();

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(mapper.map(userDTO, User.class)).thenReturn(user);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(mapper.map(user, ResponseUserDTO.class)).thenReturn(responseUserDTO);

        ResponseUserDTO savedUserDTO = userService.createUser(userDTO);

        Assertions.assertThat(savedUserDTO).isNotNull();

        verify(userRepository).save(user);
        verify(mapper).map(userDTO, User.class);
        verify(mapper).map(user, ResponseUserDTO.class);
    }

    @Test
    public void UserService_CreateUserEmailUsed_ThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.createUser(userDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This email is already being used");
    }

    @Test
    public void UserService_UpdateUserEmailAndUsername_ReturnResponseUserDTO() {
        Integer id = 1;

        User existingUser = new User();
        existingUser.setUsername("oldName");
        existingUser.setEmail("old@gmail.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newName");
        userDTO.setEmail("new@gmail.com");

        ResponseUserDTO responseUserDTO = new ResponseUserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(existingUser, ResponseUserDTO.class)).thenReturn(responseUserDTO);

        ResponseUserDTO updatedUser = userService.updateUser(id, userDTO);

        Assertions.assertThat(existingUser.getUsername()).isEqualTo(userDTO.getUsername());
        Assertions.assertThat(existingUser.getEmail()).isEqualTo(userDTO.getEmail());
        Assertions.assertThat(updatedUser).isEqualTo(responseUserDTO);

        verify(userRepository).save(existingUser);
        verify(mapper).map(existingUser, ResponseUserDTO.class);
    }

    @Test
    public void UserService_UpdateUserUsername_ReturnResponseUserDTO() {
        Integer id = 1;

        User existingUser = new User();
        existingUser.setUsername("oldName");
        existingUser.setEmail("old@gmail.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newName");
        userDTO.setEmail("old@gmail.com");

        ResponseUserDTO responseUserDTO = new ResponseUserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(existingUser));
        when(mapper.map(existingUser, ResponseUserDTO.class)).thenReturn(responseUserDTO);

        ResponseUserDTO updatedUser = userService.updateUser(id, userDTO);

        Assertions.assertThat(existingUser.getUsername()).isEqualTo(userDTO.getUsername());
        Assertions.assertThat(existingUser.getEmail()).isEqualTo(userDTO.getEmail());
        Assertions.assertThat(updatedUser).isEqualTo(responseUserDTO);

        verify(userRepository).save(existingUser);
        verify(mapper).map(existingUser, ResponseUserDTO.class);
    }

    @Test
    public void UserService_UpdateUserEmailIsUsed_ThrowException() {
        Integer id = 1;

        User existingUser = new User();
        existingUser.setUsername("oldName");
        existingUser.setEmail("old@gmail.com");

        User usedUser = new User();
        usedUser.setEmail("used@gmail.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newName");
        userDTO.setEmail("used@gmail.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(usedUser));

        Assertions.assertThatThrownBy(() -> userService.updateUser(id, userDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This email is already being used");
    }

    @Test
    public void UserService_UpdateUserNotFound_ThrowException() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.updateUser(id, new UserDTO()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    public void UserService_GetAllUsers_ReturnPageResponseUserDTO() {
        int page = 0;
        int size = 2;

        User user1 = new User();
        user1.setId(1);

        User user2 = new User();
        user2.setId(2);

        ResponseUserDTO responseDto1 = new ResponseUserDTO();
        ResponseUserDTO responseDto2 = new ResponseUserDTO();

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = new PageImpl<>(List.of(user2, user1), pageable, size);

        when(userRepository.findAllByOrderByCreatedOnDesc(pageable)).thenReturn(users);
        when(mapper.map(user1, ResponseUserDTO.class)).thenReturn(responseDto1);
        when(mapper.map(user2, ResponseUserDTO.class)).thenReturn(responseDto2);

        Page<ResponseUserDTO> response = userService.getUsersPageable(page, size);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getContent().size()).isEqualTo(size);

        verify(userRepository).findAllByOrderByCreatedOnDesc(pageable);
        verify(mapper).map(user1, ResponseUserDTO.class);
        verify(mapper).map(user2, ResponseUserDTO.class);
    }

    @Test
    public void UserService_GetUserById_ReturnResponseUserDTO() {
        Integer id = 1;

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User getUser = userService.getUserById(id);

        Assertions.assertThat(getUser).isNotNull();
        Assertions.assertThat(getUser.getId()).isEqualTo(id);

        verify(userRepository).findById(id);
    }

    @Test
    public void UserService_GetUserByIdNotFound_ThrowException() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    public void UserService_GetUserByUsername_ReturnResponseUserDTO() {
        String username = "name";

        User user = new User();
        user.setUsername(username);

        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(mapper.map(user, ResponseUserDTO.class)).thenReturn(responseUserDTO);

        ResponseUserDTO getUser = userService.getUserByUsername(username);

        Assertions.assertThat(getUser).isNotNull();
        Assertions.assertThat(getUser.getUsername()).isEqualTo(username);

        verify(userRepository).findByUsername(username);
        verify(mapper).map(user, ResponseUserDTO.class);
    }

    @Test
    public void UserService_GetUserByUsernameNotFound_ThrowException() {
        String username = "name";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserByUsername(username))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    public void UserService_GetUserByEmail_ReturnResponseUserDTO() {
        String email = "email@gmail.com";

        User user = new User();
        user.setEmail(email);

        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mapper.map(user, ResponseUserDTO.class)).thenReturn(responseUserDTO);

        ResponseUserDTO getUser = userService.getUserByEmail(email);

        Assertions.assertThat(getUser).isNotNull();
        Assertions.assertThat(getUser.getEmail()).isEqualTo(email);

        verify(userRepository).findByEmail(email);
        verify(mapper).map(user, ResponseUserDTO.class);
    }

    @Test
    public void UserService_GetUserByEmailNotFound_ThrowException() {
        String username = "name";

        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserByEmail(username))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    public void UserService_DeleteUserById_Void() {
        Integer id = 1;

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).findById(id);
        verify(userRepository).delete(user);
    }

    @Test
    public void UserService_DeleteUserByIdNotFound_ThrowException() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }
}
