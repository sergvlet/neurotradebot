package com.chicu.neurotradebot.user.repository;

import com.chicu.neurotradebot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
