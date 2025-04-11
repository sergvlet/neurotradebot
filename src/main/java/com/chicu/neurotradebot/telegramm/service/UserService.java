package com.chicu.neurotradebot.telegramm.service;

import com.chicu.neurotradebot.telegramm.model.User;
import com.chicu.neurotradebot.telegramm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Получение пользователя по chatId
    public User getUserByChatId(Long chatId) {
        Optional<User> userOptional = userRepository.findByChatId(chatId);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found"));
    }
}
