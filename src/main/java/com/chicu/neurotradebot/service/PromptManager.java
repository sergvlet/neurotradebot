// src/main/java/com/chicu/neurotradebot/service/PromptManager.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PromptManager {
    private final TelegramSender sender;

    /**
     * Заменяет интерактивное сообщение: удаляет старое (если есть),
     * отправляет новое и сохраняет его ID.
     *
     * @param chatId     ID чата
     * @param buildMsg   функция, строящая SendMessage по chatId
     * @param getOldId   функция, возвращающая из cfg старый messageId
     * @param setNewId   BiConsumer(cfg, newMsgId)
     * @param saveCfg    Consumer для сохранения cfg после изменения promptMsgId
     * @param cfg        объект настроек
     * @param <T>        тип cfg (например AiTradeSettings)
     */
    public <T> void replacePrompt(
            Long chatId,
            Function<Long, SendMessage> buildMsg,
            Function<T, Integer> getOldId,
            BiConsumer<T, Integer> setNewId,
            Consumer<T> saveCfg,
            T cfg
    ) {
        Integer oldId = getOldId.apply(cfg);
        if (oldId != null) {
            try {
                DeleteMessage delete = DeleteMessage.builder()
                        .chatId(chatId.toString())
                        .messageId(oldId)
                        .build();
                sender.execute(delete);
            } catch (Exception ignored) {
                // Если не удалось удалить — игнорируем
            }
        }

        // Отправляем новое сообщение и сохраняем его ID
        Message sent;
        try {
            sent = sender.execute(buildMsg.apply(chatId));
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить prompt", e);
        }

        setNewId.accept(cfg, sent.getMessageId());
        saveCfg.accept(cfg);
    }
}
