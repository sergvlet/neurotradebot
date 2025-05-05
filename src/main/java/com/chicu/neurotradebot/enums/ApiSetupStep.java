// src/main/java/com/chicu/neurotradebot/enums/ApiSetupStep.java
package com.chicu.neurotradebot.enums;

/**
 * Этапы пошаговой настройки API-ключей:
 * NONE          — ключи не настраиваются сейчас
 * ENTER_KEY     — ожидаем ввод API-Key
 * ENTER_SECRET  — ожидаем ввод API-Secret
 */
public enum ApiSetupStep {
    NONE,
    ENTER_KEY,
    ENTER_SECRET
}
