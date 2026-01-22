package com.stannapav.emailsystem.repository;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser).isEqualTo(user);
    }

    @Test
    public void UserRepository_FindById_ReturnUserWithId() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        userRepository.save(user);
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void UserRepository_FindByUsername_ReturnUserWithUsername() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        userRepository.save(user);
        User foundUser = userRepository.findByUsername(user.getUsername()).orElse(null);

        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void UserRepository_FindByEmail_ReturnUserWithEmail() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        userRepository.save(user);
        User foundUser = userRepository.findByUsername(user.getUsername()).orElse(null);

        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void UserRepository_UpdateUser_ReturnUpdatedUser(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        User savedUser = userRepository.save(user);
        savedUser.setUsername("test2");
        User updatedUser = userRepository.save(savedUser);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser).isEqualTo(savedUser);
    }

    @Test
    public void UserRepository_DeleteUser_ReturnUserIsEmpty(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        userRepository.save(user);
        userRepository.deleteById(user.getId());
        Optional<User> deletedUser = userRepository.findById(user.getId());

        Assertions.assertThat(deletedUser).isEmpty();
    }

    @Test
    public void UserRepository_DeleteAllUser_ReturnUserListIsEmpty(){
        User user1 = new User();
        user1.setUsername("test1");
        user1.setEmail("test1@gmail.com");

        User user2 = new User();
        user2.setUsername("test2");
        user2.setEmail("test2@gmail.com");

        userRepository.save(user1);
        userRepository.save(user2);

        userRepository.deleteAll();
        List<User> deletedUsers = userRepository.findAll();

        Assertions.assertThat(deletedUsers).isEmpty();
    }

    @Test
    public void UserRepository_UserExistByEmail_ReturnTrue(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");

        userRepository.save(user);
        boolean exist = userRepository.existsByEmail(user.getEmail());

        Assertions.assertThat(exist).isEqualTo(true);
    }

    @Test
    public void UserRepository_FindAllByOrderByCreatedOnDesc_ReturnUserPageOrderedOnDesc(){
        User user1 = new User();
        user1.setUsername("test1");
        user1.setEmail("test1@gmail.com");

        User user2 = new User();
        user2.setUsername("test2");
        user2.setEmail("test2@gmail.com");

        userRepository.save(user1);
        userRepository.save(user2);

        Pageable pageable = PageRequest.of(0,2);
        Page<User> usersPage = userRepository.findAllByOrderByCreatedOnDesc(pageable);
        Page<User> mokingPage = new PageImpl<>(List.of(user2, user1), pageable, 0);


        Assertions.assertThat(usersPage).isEqualTo(mokingPage);
    }
}
