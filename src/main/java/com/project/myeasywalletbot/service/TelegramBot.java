package com.project.myeasywalletbot.service;

import com.project.myeasywalletbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final static String HELP_TEXT = "Here will be described all the bot commands";
    private final static String UNRECOGNIZED_COMMAND = "Sorry, command was not recognized";
    private final static long INVALID_ID = -1;
    public static final String CATCH_ERROR = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (getUserChatId(update) != INVALID_ID){
            long chatId = getUserChatId(update);
            switch (getUserMessageText(update)){
                case "/start" -> {
                    prepareAndSendMessage(chatId, HELP_TEXT);
                }
                default -> getUserMessageText(update);

            }
        }
    }

    private String getUserMessageText(Update update){
        if (update.hasMessage() && update.getMessage().hasText()){
            return update.getMessage().getText();
        }
        return UNRECOGNIZED_COMMAND;
    }
    private long getUserChatId(Update update){
        if (update.hasMessage()){
            return update.getMessage().getChatId();
        }
        return INVALID_ID;
    }
    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }
    private void executeMessage(SendMessage messageText){
        try {
            execute(messageText);
        } catch (TelegramApiException e) {
            log.error(CATCH_ERROR + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
