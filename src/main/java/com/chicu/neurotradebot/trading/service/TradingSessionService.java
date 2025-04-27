package com.chicu.neurotradebot.trading.service;

import com.chicu.neurotradebot.trading.entity.TradingSession;
import com.chicu.neurotradebot.trading.repository.TradingSessionRepository;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TradingSessionService {

    private final TradingSessionRepository tradingSessionRepository;
    private final UserRepository userRepository;

    public TradingSession startSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        TradingSession session = new TradingSession();
        session.setUser(user);
        session.setExchange("BINANCE"); // Пока жёстко Binance, потом добавим выбор
        session.setStatus(TradingSession.SessionStatus.ACTIVE);
        session.setStartedAt(LocalDateTime.now());

        return tradingSessionRepository.save(session);
    }
}
