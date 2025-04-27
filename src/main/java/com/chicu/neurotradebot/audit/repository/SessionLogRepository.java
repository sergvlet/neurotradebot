package com.chicu.neurotradebot.audit.repository;

import com.chicu.neurotradebot.audit.entity.SessionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {
    List<SessionLog> findByUserId(Long userId);
}
