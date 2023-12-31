package org.siniuk.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class ButtonFactory {

    public static InlineKeyboardButton createButton(String name, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(name);
        button.setCallbackData(callbackData);
        return button;
    }
}
