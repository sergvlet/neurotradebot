package com.chicu.neurotradebot.trade.service;

/**
 * Контракт для авто- и ручной торговли.
 */
public interface TradingService {
    /** Запуск одного цикла автоматической торговли. */
    void executeCycle();

    /** Выполнить ручной ордер по символу. */
    void executeManualOrder(String symbol, boolean buy);

    /** Новый метод – цикл для конкретного чата. */
    void executeCycle(Long chatId);
}
