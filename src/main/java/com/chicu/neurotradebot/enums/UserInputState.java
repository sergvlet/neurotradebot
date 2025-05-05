// src/main/java/com/chicu/neurotradebot/enums/UserInputState.java
package com.chicu.neurotradebot.enums;

/**
 * Состояние диалога с пользователем:
 * NONE — нет активного ввода
 * EXCHANGE_SELECTION — выбор биржи
 * API_KEY_ENTRY      — ввод API Key
 * API_SECRET_ENTRY   — ввод API Secret
 */
public enum UserInputState {
    NONE,
    EXCHANGE_SELECTION,
    API_KEY_ENTRY,
    API_SECRET_ENTRY
}
