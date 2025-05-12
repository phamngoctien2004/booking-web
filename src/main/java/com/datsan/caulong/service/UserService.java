package com.datsan.caulong.service;

import com.datsan.caulong.exception.AppException;
import com.datsan.caulong.exception.Error;
import com.datsan.caulong.model.User;
import com.datsan.caulong.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new AppException(Error.USER_NOT_FOUND));
    }
    public Optional<User> OFindByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public void save(User user){
        userRepository.save(user);
    }
}
