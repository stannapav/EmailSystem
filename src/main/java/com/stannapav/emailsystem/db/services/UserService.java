package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.dtos.UserDTO;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Integer userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public Page<User> getUsersPageable(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllByOrderByCreatedOnDesc(pageable);
    }

    public User createUser(UserDTO userDTO){
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("This email is already being used");
        }

        User user = mapper.map(userDTO, User.class);
        userRepository.save(user);

        return user;
    }

    public User updateUser(Integer id, UserDTO userDTO){
        User updateUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if(optionalUser.isEmpty()) {
            updateUser.setUsername(userDTO.getUsername());
            updateUser.setEmail(userDTO.getEmail());
        }
        else if(updateUser.getEmail().equals(userDTO.getEmail())) {
            updateUser.setUsername(userDTO.getUsername());
        }
        else{
            throw new IllegalArgumentException("This email is already being used");
        }

        userRepository.save(updateUser);
        return updateUser;
    }

    public void deleteUser(Integer id){
        User deleteUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(deleteUser);
    }

    public void deleteAllUsers(){
        userRepository.deleteAll();
    }
}
