package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
