// src/main/java/com/chicu/neurotradebot/model/UserInputState.java
package com.chicu.neurotradebot.entity;

public enum UserInputState {
    NONE,
    WAIT_API_LABEL,
    WAIT_API_KEY,
    WAIT_API_SECRET,
    WAIT_PAIR_INPUT,
    WAIT_RSI_PERIOD,
    // добавляешь любые состояния по мере роста проекта
}
