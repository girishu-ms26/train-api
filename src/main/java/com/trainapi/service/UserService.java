package com.trainapi.service;

import com.trainapi.model.User;
import com.trainapi.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void removeUser(int userId) {
        userRepository.deleteById(userId);
    }
}
