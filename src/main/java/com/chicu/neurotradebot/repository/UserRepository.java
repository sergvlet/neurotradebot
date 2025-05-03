// src/main/java/com/chicu/neurotradebot/repository/UserRepository.java
package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramUserId(Long telegramUserId);
}
