package org.siniuk.bot;

import org.siniuk.cache.BotState;
import org.siniuk.cache.UserDataCache;
import org.siniuk.messages.GeneralMessages;
import org.siniuk.messages.InvoiceMessages;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.siniuk.messages.GeneralMessages.*;

public class Bot extends TelegramLongPollingBot {

    UserDataCache userDataCache = new UserDataCache();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
            long userId = callbackQuery.getFrom().getId();
            System.out.println(callbackData + " " + chatId);

            if (callbackData.contains("year_energy")) {
                sendMessageWithBackBtn(chatId, ENTER_DATE.getMessage());
                userDataCache.setUsersCurrentBotState(userId, BotState.YEAR_ENERGY);
            } else if (callbackData.contains("month_energy")) {
                sendMessageWithBackBtn(chatId, ENTER_DATE.getMessage());
                userDataCache.setUsersCurrentBotState(userId, BotState.MONTH_ENERGY);
            } else if (callbackData.contains("personal_energy")) {
                sendMessageWithBackBtn(chatId, ENTER_DATE.getMessage());
                userDataCache.setUsersCurrentBotState(userId, BotState.PERSONAL_ENERGY);
            } else if (callbackData.contains("single_payment")) {
                sendInvoice(chatId, "Разовая подписка", InvoiceMessages.SINGLE.getPrice(), "Разовая: расчёт 1 энергии на выбор для одной даты.", "single_payment");
            } else if (callbackData.contains("year_payment")) {
                sendInvoice(chatId, "Годовая подписка", InvoiceMessages.YEAR.getPrice(), "Подписка на год: до 6 запросов расчёта энергии года и 2 энергий месяца.", "year_payment");
            } else if (callbackData.contains("forever_payment")) {
                sendInvoice(chatId, "Подписка навсегда", InvoiceMessages.FOREVER.getPrice(), "Подписка навсегда: безвременный доступ к расчёту всех энергий до 6 запросов в месяц + бонус «Денежные энергии».", "personal_energy");
            } else {
                selectEnergyType(chatId);
            }

        } else if ((update.hasMessage())) {
            Message inMess = update.getMessage();
            String chatId = inMess.getChatId().toString();
            if (inMess.hasSuccessfulPayment()) {
                SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();
                String payload = successfulPayment.getInvoicePayload();
                switch (payload) {
                    case "single_payment":
                        sendMessage(chatId, SUCCESSFUL_SINGLE_PAYMENT.getMessage());
                        break;
                    case "year_payment":
                        sendMessage(chatId, SUCCESSFUL_YEAR_PAYMENT.getMessage());
                        break;
                    case "personal_energy":
                        sendMessage(chatId, SUCCESSFUL_FOREVER_PAYMENT.getMessage());
                        break;
                    default:
                        sendMessage(chatId, PAYMENT_ERROR.getMessage());
                }
                selectEnergyType(chatId);
            } else {
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
                            selectEnergyType(chatId);
                            break;
                    }
                } else {
                    sendMessageWithBackBtn(chatId, GeneralMessages.ERROR_DATE.getMessage());
                }
            }
        } else if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            String preCheckoutQueryId = preCheckoutQuery.getId();
            answerPreCheckoutQuery(preCheckoutQueryId);
        }
    }

    private void answerPreCheckoutQuery(String preCheckoutQueryId) {
        AnswerPreCheckoutQuery answer = new AnswerPreCheckoutQuery();
        answer.setPreCheckoutQueryId(preCheckoutQueryId);
        answer.setOk(true);
        try {
            execute(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectEnergyType(String chatId) {
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

    private void sendInvoice(String chatId, String title, Integer price, String description, String payload) {
        String paymentToken = "381764678:TEST:63269";
        String currency = "RUB";

        List<LabeledPrice> prices = new ArrayList<>();
        prices.add(new LabeledPrice(title, price));

        SendInvoice sendInvoice = new SendInvoice(chatId, title, description, payload, paymentToken, "start_parameter", currency, prices);

        try {
            execute(sendInvoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBot(String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();

        firstRow.add(ButtonFactory.createButton(InvoiceMessages.SINGLE.getDescription(), "single_payment"));
        secondRow.add(ButtonFactory.createButton(InvoiceMessages.YEAR.getDescription(), "year_payment"));
        thirdRow.add(ButtonFactory.createButton(InvoiceMessages.FOREVER.getDescription(), "forever_payment"));
        keyboardRowList.add(firstRow);
        keyboardRowList.add(secondRow);
        keyboardRowList.add(thirdRow);

        keyboardMarkup.setKeyboard(keyboardRowList);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Сейчас выбери подписку:");
        message.enableMarkdown(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
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

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> rowButtons = new ArrayList<>();
        rowButtons.add(ButtonFactory.createButton("Назад", "back"));

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowButtons);
        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(inlineKeyboardMarkup);

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

//    private void initPayment(String chatId, long userId, Update update) {
//        String query = update.getInlineQuery().getQuery();
//
//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
////        List<InlineQueryResult> results = new ArrayList<>();
//
//        InputInvoiceMessageContent singleInvoice = InvoiceFactory.createInvoice(InvoiceMessages.SINGLE.getDescription(), InvoiceMessages.SINGLE.getPrice(), String.valueOf(userId) + "_single_payment");
////        InputInvoiceMessageContent yearInvoice = InvoiceFactory.createInvoice(InvoiceMessages.YEAR.getDescription(), InvoiceMessages.YEAR.getPrice(), String.valueOf(userId) + "_year_payment");
////        InputInvoiceMessageContent foreverInvoice = InvoiceFactory.createInvoice(InvoiceMessages.FOREVER.getDescription(), InvoiceMessages.FOREVER.getPrice(), String.valueOf(userId) + "_forever_payment");
//
//        // Create the InlineQueryResultArticle
//        InlineQueryResultArticle article = new InlineQueryResultArticle();
//        article.setId("1");
//        article.setTitle("Your Invoice Title");
//        article.setDescription("Description of your invoice");
//        article.setInputMessageContent(singleInvoice);
//
////        results.add(singleInvoice);
////        results.add(yearInvoice);
////        results.add(foreverInvoice);
//
//        // Create the response
//        List<InlineQueryResult> results = new ArrayList<>();
//        results.add(article);
//        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
//        answerInlineQuery.setInlineQueryId(update.getInlineQuery().getId());
//        answerInlineQuery.setResults(results);
//
//        try {
//            execute(answerInlineQuery);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
    @Override
    public String getBotUsername() {
        return "astrologitec_bot";
    }

    @Override
    public String getBotToken() {
        return "6232575817:AAGSsJbz5QxM9SKx4fLqOfKX15SyJtSiCbA";
    }
}
