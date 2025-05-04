package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.UserInputState;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TextInputAwareMenu extends MenuDefinition {
    void handleText(Update update);
    boolean supports(UserInputState state);
}
