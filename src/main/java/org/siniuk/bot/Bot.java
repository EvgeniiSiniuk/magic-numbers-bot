package org.siniuk.bot;

import org.siniuk.ButtonFactory;
import org.siniuk.cache.BotState;
import org.siniuk.cache.UserDataCache;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    UserDataCache userDataCache = new UserDataCache();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            // Handle callback data
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
            long userId = callbackQuery.getFrom().getId();
            System.out.println(callbackData + " " + chatId);

            if (callbackData.contains("year_energy")) {
                sendMessageWithBackBtn(chatId, "Введите дату рождения в формате YYYY-MM-DD");
                userDataCache.setUsersCurrentBotState(userId, BotState.YEAR_ENERGY);
            } else if (callbackData.contains("month_energy")) {
                sendMessageWithBackBtn(chatId, "Введите дату рождения в формате YYYY-MM-DD");
                userDataCache.setUsersCurrentBotState(userId, BotState.MONTH_ENERGY);
            } else if (callbackData.contains("personal_energy")) {
                sendMessageWithBackBtn(chatId, "Введите дату рождения в формате YYYY-MM-DD");
                userDataCache.setUsersCurrentBotState(userId, BotState.PERSONAL_ENERGY);
            } else {
                initBot(chatId);
            }

        } else if ((update.hasMessage())) {
            Message inMess = update.getMessage();
            String chatId = inMess.getChatId().toString();
            String inMessText = inMess.getText();
            long userId = inMess.getFrom().getId();
            System.out.println(inMessText + " " + chatId);

            BotState botState = userDataCache.getUsersCurrentBotState(userId);
            if (inMessText.equals("/start")) {
                initBot(chatId);
            } else if (isValidLocalDate(inMessText)) {
                switch (botState) {
                    case YEAR_ENERGY:
                        sendMessageWithBackBtn(chatId, "Ваша энергия года: " + EnergyCalculation.calculateYearEnergy(inMessText));
                        break;
                    case MONTH_ENERGY:
                        sendMessageWithBackBtn(chatId, "Ваша энергия месяца: " + EnergyCalculation.calculateMonthEnergy(inMessText));
                        break;
                    case PERSONAL_ENERGY:
                        sendMessageWithBackBtn(chatId, "Ваша персональная энергия: " + EnergyCalculation.calculatePersonalEnergy(inMessText));
                        break;
                    default:
                        initBot(chatId);
                        break;
                }
            } else {
                sendMessageWithBackBtn(chatId, "Ошибка при вводе даты. Введите дату рождения в формате YYYY-MM-DD");
            }
        }
    }

    private void initBot(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();

        firstRow.add(ButtonFactory.createButton("Расчитать энергию года", "year_energy"));
        secondRow.add(ButtonFactory.createButton("Расчитать энергию месяца", "month_energy"));
        thirdRow.add(ButtonFactory.createButton("Расчитать персональную энергию", "personal_energy"));
        keyboardRowList.add(firstRow);
        keyboardRowList.add(secondRow);
        keyboardRowList.add(thirdRow);

        keyboardMarkup.setKeyboard(keyboardRowList);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выбери энергию которую хочешь расчитать!");
        message.enableMarkdown(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageWithBackBtn(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        // Create the inline keyboard
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Create a row of inline keyboard buttons
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();
        rowButtons.add(ButtonFactory.createButton("Назад", "back"));

        // Add the row of buttons to the inline keyboard
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowButtons);
        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        // Set the inline keyboard in the message
        message.setReplyMarkup(inlineKeyboardMarkup);

        // Send the message
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidLocalDate(String dateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate.parse(dateString, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String getBotUsername() {
        return "astrologitec_bot";
    }

    @Override
    public String getBotToken() {
        return "6232575817:AAGSsJbz5QxM9SKx4fLqOfKX15SyJtSiCbA";
    }
}
