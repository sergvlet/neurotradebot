package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.ConnectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
}